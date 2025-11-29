package me.ivan.darazhanov.loginregistercode.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.ivan.darazhanov.loginregistercode.DTO.*;
import me.ivan.darazhanov.loginregistercode.service.SessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
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
        service.saveUserInfo(session, request.getMobileNumber(), request.getEmail());

        return ResponseEntity.ok(new RegistrationResponse(true,"Verification code generated",code));
    }

    @PostMapping("/verify")
    public String verifyCode(
            @Valid @RequestBody VerificationRequest request,
            HttpSession session
    ) {
        boolean ok = service.verify(session, request.getCode());
        return ok ? "Correct code!" : "Invalid or expired code!";
    }
}