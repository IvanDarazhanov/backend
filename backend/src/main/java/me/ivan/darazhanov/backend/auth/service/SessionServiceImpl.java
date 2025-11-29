package me.ivan.darazhanov.backend.auth.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.SecureRandom;

@Service
public class SessionServiceImpl  {

    private static final String VERIFICATION_CODE_KEY = "verificationCode";

    /**
     * Generate a 6-digit verification code
     */
    public String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * Save verification code in Redis session
     */
    public void saveVerificationCode(String verificationCode) {
        HttpSession session = getCurrentSession();
        session.setAttribute(VERIFICATION_CODE_KEY, verificationCode);

        System.out.println("✓ Code saved to Redis - Session ID: " + session.getId());
        System.out.println("  Code: " + verificationCode);
    }

    /**
     * Verify the provided code against session
     */
    public boolean verifyCode(String providedCode) {
        HttpSession session = getCurrentSession();
        String storedCode = (String) session.getAttribute(VERIFICATION_CODE_KEY);

        System.out.println("=== VERIFICATION ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Stored Code: " + storedCode);
        System.out.println("Provided Code: " + providedCode);

        if (storedCode == null) {
            System.out.println("✗ No code in session");
            return false;
        }

        boolean isValid = storedCode.equals(providedCode);

        if (isValid) {
            System.out.println("✓ Verification successful");
            // Clear the code after successful verification
            session.removeAttribute(VERIFICATION_CODE_KEY);
        } else {
            System.out.println("✗ Invalid code");
        }

        return isValid;
    }

    /**
     * Get verification code (for debugging)
     */
    public String getVerificationCode() {
        return (String) getCurrentSession().getAttribute(VERIFICATION_CODE_KEY);
    }

    /**
     * Get current HTTP session
     */
    private HttpSession getCurrentSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }
}