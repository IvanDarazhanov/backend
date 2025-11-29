package me.ivan.darazhanov.loginregistercode.service;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String SESSION_KEY = "verificationCode";
    private static final String SESSION_EMAIL = "email";
    private static final String SESSION_PHONE = "phone";

    public int generateCode(HttpSession session) {
        int code = (int) (Math.random() * 900_000) + 100_000;
        session.setAttribute(SESSION_KEY, code);
        return code;
    }
    public void saveUserInfo(HttpSession session, String email, String phone) {
        session.setAttribute(SESSION_EMAIL, email);
        session.setAttribute(SESSION_PHONE, phone);
    }

    public boolean verify(HttpSession session, int inputCode) {
        Integer stored = (Integer) session.getAttribute(SESSION_KEY);

        if (stored == null) {
            return false; // no session or code expired
        }

        return stored == inputCode;
    }
}