package me.ivan.darazhanov.loginregisterbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class VerificationRequest {

    @NotBlank(message = "Verification code is required")
    @Pattern(regexp = "^\\d{6}$", message = "Verification code must be exactly 6 digits")
    private String code;

    // Constructors
    public VerificationRequest() {}

    public VerificationRequest(String code) {
        this.code = code;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}