package com.microtest.UserService.service.impl;

import com.microtest.UserService.service.RefreshTokenStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenStoreImpl implements RefreshTokenStore {

    // user -> current valid refresh token id (jti) and expiry
    private final Map<String, Entry> active = new ConcurrentHashMap<>();

    public record Entry(String jti, Instant expiresAt) {
    }

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
