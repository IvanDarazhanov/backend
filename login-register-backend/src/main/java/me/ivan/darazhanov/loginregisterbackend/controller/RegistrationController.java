package me.ivan.darazhanov.loginregisterbackend.controller;

import me.ivan.darazhanov.loginregisterbackend.dto.ApiResponse;
import me.ivan.darazhanov.loginregisterbackend.dto.RegistrationRequest;
import me.ivan.darazhanov.loginregisterbackend.dto.VerificationRequest;
import me.ivan.darazhanov.loginregisterbackend.service.RegistrationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowCredentials = "true")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Server is running", "OK"));
    }

    /**
     * Register user with mobile number and email
     * Step 2: Submit mobile number and email
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, String>>> register(
            @Valid @RequestBody RegistrationRequest request,
            BindingResult bindingResult,
            HttpSession session) {

        logger.info("Registration request received: mobile={}, email={}",
                request.getMobileNumber(), request.getEmail());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            logger.warn("Validation errors: {}", errors);
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(errors));
        }

        try {
            // Register user and generate verification code
            Map<String, String> response = registrationService.registerUser(
                    request.getMobileNumber(),
                    request.getEmail(),
                    session
            );

            return ResponseEntity.ok(
                    ApiResponse.success("Registration successful. Verification code generated.", response)
            );

        } catch (Exception e) {
            logger.error("Error during registration", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Verify the 6-digit code
     * Step 3: Verify code
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyCode(
            @Valid @RequestBody VerificationRequest request,
            BindingResult bindingResult,
            HttpSession session) {

        logger.info("Verification request received: code={}", request.getCode());

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            logger.warn("Validation errors: {}", errors);
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(errors));
        }

        try {
            // Verify the code
            boolean isValid = registrationService.verifyCode(request.getCode(), session);

            if (isValid) {
                Map<String, Object> response = new HashMap<>();
                response.put("verified", true);
                response.put("userData", registrationService.getUserData(session));

                return ResponseEntity.ok(
                        ApiResponse.success("Code verified successfully", response)
                );
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Invalid verification code"));
            }

        } catch (Exception e) {
            logger.error("Error during verification", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Verification failed: " + e.getMessage()));
        }
    }

    /**
     * Resend verification code (optional endpoint)
     */
    @PostMapping("/resend-code")
    public ResponseEntity<ApiResponse<Map<String, String>>> resendCode(HttpSession session) {
        logger.info("Resend code request received");

        try {
            Map<String, String> userData = registrationService.getUserData(session);
            String mobile = userData.get("mobileNumber");
            String email = userData.get("email");

            if (mobile == null || email == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("No active session found. Please register first."));
            }

            // Generate new code
            Map<String, String> response = registrationService.registerUser(mobile, email, session);

            return ResponseEntity.ok(
                    ApiResponse.success("Verification code resent successfully", response)
            );

        } catch (Exception e) {
            logger.error("Error resending code", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to resend code: " + e.getMessage()));
        }
    }

    /**
     * Get session info (for debugging)
     */
    @GetMapping("/session")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSession(HttpSession session) {
        Map<String, String> userData = registrationService.getUserData(session);
        return ResponseEntity.ok(ApiResponse.success("Session data retrieved", userData));
    }
}