package me.ivan.darazhanov.loginregisterbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private static final String SESSION_MOBILE_KEY = "mobileNumber";
    private static final String SESSION_EMAIL_KEY = "email";
    private static final String SESSION_CODE_KEY = "verificationCode";

    private final SecureRandom random = new SecureRandom();

    /**
     * Register user details and generate verification code
     */
    public Map<String, String> registerUser(String mobileNumber, String email, HttpSession session) {
        // Save to session
        session.setAttribute(SESSION_MOBILE_KEY, mobileNumber);
        session.setAttribute(SESSION_EMAIL_KEY, email);

        // Generate 6-digit code
        String verificationCode = generateVerificationCode();
        session.setAttribute(SESSION_CODE_KEY, verificationCode);

        // Log the code to console (as per requirements)
        logger.info("==============================================");
        logger.info("Verification code for {}: {}", email, verificationCode);
        logger.info("==============================================");

        // Prepare response
        Map<String, String> response = new HashMap<>();
        response.put("verificationCode", verificationCode);
        response.put("message", "Verification code generated successfully");

        return response;
    }

    /**
     * Verify the code entered by user
     */
    public boolean verifyCode(String code, HttpSession session) {
        String storedCode = (String) session.getAttribute(SESSION_CODE_KEY);

        if (storedCode == null) {
            logger.warn("No verification code found in session");
            return false;
        }

        boolean isValid = storedCode.equals(code);

        if (isValid) {
            logger.info("Verification code validated successfully");
            // Optionally clear the code after successful verification
            session.removeAttribute(SESSION_CODE_KEY);
        } else {
            logger.warn("Invalid verification code entered: {}", code);
        }

        return isValid;
    }

    /**
     * Generate a random 6-digit verification code
     */
    private String generateVerificationCode() {
        int code = 100000 + random.nextInt(900000); // Generates number between 100000-999999
        return String.valueOf(code);
    }

    /**
     * Get stored user data from session
     */
    public Map<String, String> getUserData(HttpSession session) {
        Map<String, String> userData = new HashMap<>();
        userData.put("mobileNumber", (String) session.getAttribute(SESSION_MOBILE_KEY));
        userData.put("email", (String) session.getAttribute(SESSION_EMAIL_KEY));
        return userData;
    }
}