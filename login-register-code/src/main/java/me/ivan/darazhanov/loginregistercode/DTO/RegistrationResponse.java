package me.ivan.darazhanov.loginregistercode.DTO;

public class RegistrationResponse {
    private boolean success;
    private String message;
    private int verificationCode;

    public RegistrationResponse() {
    }

    public RegistrationResponse(boolean success, String message, int verificationCode) {
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

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }
}
