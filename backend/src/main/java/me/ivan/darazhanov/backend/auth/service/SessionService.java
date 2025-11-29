package me.ivan.darazhanov.backend.auth.service;

public interface SessionService {
    String generateVerificationCode();
    void saveRegistrationData(String mobileNumber, String email, String verificationCode);
    String getMobileNumber();
    String getEmail();
    String getVerificationCode();
    boolean verifyCode(String providedCode);
    void clearSession();
}
