package me.ivan.darazhanov.backend.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import me.ivan.darazhanov.backend.auth.dto.*;
import me.ivan.darazhanov.backend.auth.service.SessionService;
import me.ivan.darazhanov.backend.auth.service.SessionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private final SessionServiceImpl sessionService;

    public AuthController(SessionServiceImpl sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/health")
    public ResponseEntity<VerificationResponse> health() {
        return ResponseEntity.ok().body(new VerificationResponse(true, "Server is running"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRegistrationRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        System.out.println("=== REGISTER REQUEST ===");
        System.out.println("Session ID: " + httpRequest.getSession().getId());
        System.out.println("Mobile: " + request.getMobileNumber());
        System.out.println("Email: " + request.getEmail());

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Validation error";
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(false, errorMessage));
        }

        // Generate and save only the verification code
        String verificationCode = sessionService.generateVerificationCode();
        sessionService.saveVerificationCode(verificationCode);


        return ResponseEntity.ok(new RegistrationResponse(
                true,
                "Verification code sent",
                verificationCode  // Remove in production
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @Valid @RequestBody VerificationRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);

        System.out.println("=== VERIFY REQUEST ===");
        System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
        System.out.println("Provided Code: " + request.getCode());

        if (session == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(false, "Session expired"));
        }

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "Validation error";
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(false, errorMessage));
        }

        boolean isValid = sessionService.verifyCode(request.getCode());

        if (isValid) {
            // TODO: Update user verification status in database
            // userRepository.updateVerificationStatus(userId, true);

            return ResponseEntity.ok(new VerificationResponse(
                    true,
                    "Verification successful"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(false, "Invalid verification code"));
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> getSessionData(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(false, "No active session"));
        }

        System.out.println("=== SESSION DATA REQUEST ===");
        System.out.println("Session ID: " + session.getId());

        String code = sessionService.getVerificationCode();

        return ResponseEntity.ok().body(new Object() {
            public final String sessionId = session.getId();
            public final String verificationCode = code;
        });
    }
}