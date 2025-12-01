package me.ivan.darazhanov.loginregistercode.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.ivan.darazhanov.loginregistercode.DTO.*;
import me.ivan.darazhanov.loginregistercode.service.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class VerificationController {

    private final SessionService service;

    public VerificationController(SessionService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRegistrationRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest,
            HttpSession session) {

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

        int code = service.generateCode(session);
        service.saveUserInfo(session, request.getEmail(), request.getMobileNumber());

        return ResponseEntity.ok(new RegistrationResponse(true, "Verification code generated", code));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @Valid @RequestBody VerificationRequest request,
            HttpSession session) {

        System.out.println("=== VERIFY REQUEST ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Code: " + request.getCode());

        boolean isValid = service.verify(session, request.getCode());

        if (isValid) {
            return ResponseEntity.ok(new VerificationResponse( "Verification successful",request.getCode()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new VerificationResponse( "Invalid or expired code",request.getCode()));
        }
    }

    @PostMapping("/resend-email")
    public ResponseEntity<?> resendEmailCode(HttpSession session) {
        System.out.println("=== RESEND EMAIL CODE ===");
        System.out.println("Session ID: " + session.getId());

        String email = session.getAttribute("email").toString();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(false, "No registration data found"));
        }

        int newCode = service.generateCode(session);
        System.out.println("New email code: " + newCode + " for " + email);

        return ResponseEntity.ok(new RegistrationResponse(true, "Code resent to email", newCode));
    }

    @PostMapping("/resend-mobile")
    public ResponseEntity<?> resendMobileCode(HttpSession session) {
        System.out.println("=== RESEND MOBILE CODE ===");
        System.out.println("Session ID: " + session.getId());

        String phone = session.getAttribute("phone").toString();
        if (phone == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(false, "No registration data found"));
        }

        int newCode = service.generateCode(session);
        System.out.println("New mobile code: " + newCode + " for " + phone);

        return ResponseEntity.ok(new RegistrationResponse(true, "Code resent to mobile", newCode));
    }

    @PostMapping("/switch-to-mobile")
    public ResponseEntity<?> switchToMobile(HttpSession session) {
        System.out.println("=== SWITCH TO MOBILE ===");

        String phone = session.getAttribute("phone").toString();
        if (phone == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(false, "No registration data found"));
        }

        int newCode = service.generateCode(session);
        System.out.println("Switched to mobile. New code: " + newCode);

        return ResponseEntity.ok(new RegistrationResponse(true, "Code sent to mobile", newCode));
    }

    @PostMapping("/switch-to-email")
    public ResponseEntity<?> switchToEmail(HttpSession session) {
        System.out.println("=== SWITCH TO EMAIL ===");

        String email = session.getAttribute("email").toString();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(false, "No registration data found"));
        }

        int newCode = service.generateCode(session);
        System.out.println("Switched to email. New code: " + newCode);

        return ResponseEntity.ok(new RegistrationResponse(true, "Code sent to email", newCode));
    }

    @GetMapping("/session-info")
    public ResponseEntity<?> getSessionInfo(HttpSession session) {
        String email = session.getAttribute("email").toString();
        String phone = session.getAttribute("phone").toString();

        if (email == null || phone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(false, "No session data found"));
        }

        Map<String, String> info = new HashMap<>();
        info.put("email", email);
        info.put("phone", phone);

        return ResponseEntity.ok(info);
    }
}