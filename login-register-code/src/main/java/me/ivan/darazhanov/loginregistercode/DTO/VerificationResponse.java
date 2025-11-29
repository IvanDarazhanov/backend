package me.ivan.darazhanov.loginregistercode.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class VerificationResponse {
    private String message;

    public VerificationResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }

    private int verificationCode;

    public VerificationResponse(String message,int verificationCode) {
        this.verificationCode = verificationCode;
        this.message = message;
    }
}