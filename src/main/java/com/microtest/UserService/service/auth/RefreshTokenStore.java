package com.microtest.UserService.service.auth;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RefreshTokenStore {

    // user -> current valid refresh token id (jti) and expiry
    private final Map<String, Entry> active = new ConcurrentHashMap<>();

    public static record Entry(String jti, Instant expiresAt) {}

    public String issue(String userId, Instant expiresAt) {
        String jti = UUID.randomUUID().toString();
        active.put(userId, new Entry(jti, expiresAt));
        return jti;
    }

    public boolean isCurrent(String userId, String jti, Instant now) {
        Entry e = active.get(userId);
        return e != null && e.jti().equals(jti) && e.expiresAt().isAfter(now);
    }

    public void revoke(String userId) {
        active.remove(userId);
    }

}
