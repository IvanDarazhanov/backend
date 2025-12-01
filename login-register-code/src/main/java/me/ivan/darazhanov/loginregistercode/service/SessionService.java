package me.ivan.darazhanov.loginregistercode.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String SESSION_KEY = "verificationCode";
    private static final String SESSION_EMAIL = "email";
    private static final String SESSION_PHONE = "phone";

    public int generateCode(HttpSession session) {
        int code = (int) (Math.random() * 900_000) + 100_000;
        session.setAttribute(SESSION_KEY, code);
        System.out.println("Generated code: " + code + " for session: " + session.getId());
        return code;
    }

    public void saveUserInfo(HttpSession session, String email, String phone) {
        session.setAttribute(SESSION_EMAIL, email);
        session.setAttribute(SESSION_PHONE, phone);
        System.out.println("Saved user info - Email: " + email + ", Phone: " + phone);
    }

    public boolean verify(HttpSession session, int inputCode) {
        Integer stored = (Integer) session.getAttribute(SESSION_KEY);

        if (stored == null) {
            System.out.println("No code found in session");
            return false;
        }

        boolean isValid = stored == inputCode;
        System.out.println("Verification result: " + isValid + " (stored: " + stored + ", input: " + inputCode + ")");

        return isValid;
    }

    public String getEmail(HttpSession session) {
        return (String) session.getAttribute(SESSION_EMAIL);
    }

    public String getPhone(HttpSession session) {
        return (String) session.getAttribute(SESSION_PHONE);
    }
}