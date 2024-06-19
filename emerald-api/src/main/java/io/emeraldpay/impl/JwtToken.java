package io.emeraldpay.impl;

import java.time.Instant;

/**
 * JWT Token
 */
public class JwtToken {

    private final String jwt;
    private final Instant expireAt;
    private final String refreshToken;

    public JwtToken(String jwt, Instant expireAt, String refreshToken) {
        this.jwt = jwt;
        this.expireAt = expireAt;
        this.refreshToken = refreshToken;
    }

    public String getJwt() {
        return jwt;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
