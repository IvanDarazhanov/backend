package me.ivan.darazhanov.backend.auth.dto;

public class RegistrationResponse {
    private boolean success;
    private String message;
    private String verificationCode;

    public RegistrationResponse() {
    }

    public RegistrationResponse(boolean success, String message, String verificationCode) {
        this.success = success;
        this.message = message;
        this.verificationCode = verificationCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
