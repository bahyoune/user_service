package com.microtest.UserService.service;


import java.time.Instant;

public interface RefreshTokenStore {


    String issue(String userId, Instant expiresAt);

    boolean isCurrent(String userId, String jti, Instant now);

    void revoke(String userId);
}
