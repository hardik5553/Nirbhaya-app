package com.SIH.Women.Safety.Device.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class AdminSessionService {
    private static final Duration SESSION_TTL = Duration.ofHours(8);
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String create(String email) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new Session(email, Instant.now().plus(SESSION_TTL)));
        return token;
    }

    public boolean isValid(String token, String email) {
        if (token == null || email == null) return false;
        Session session = sessions.get(token);
        if (session == null || Instant.now().isAfter(session.expiresAt())) {
            sessions.remove(token);
            return false;
        }
        return session.email().equalsIgnoreCase(email);
    }

    public void revoke(String token) {
        if (token != null) sessions.remove(token);
    }

    private record Session(String email, Instant expiresAt) {}
}