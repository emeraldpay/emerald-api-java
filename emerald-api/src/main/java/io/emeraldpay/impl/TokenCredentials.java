package io.emeraldpay.impl;

import io.emeraldpay.api.proto.AuthGrpc;
import io.emeraldpay.api.proto.AuthOuterClass;
import io.grpc.Metadata;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of the MetadataHandler that uses a JWT token to authenticate the requests.
 * It gets the JWT from the Auth API when needed. Uses a blocking stub, but runs it in a separate thread, launched only when needed.
 * Also, by default, it refreshes the token one minute before it expires.
 */
public class TokenCredentials implements MetadataHandler {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(0);
    private static final long REFRESH_ADVANCE = TimeUnit.SECONDS.toMillis(60);

    private final String token;
    private final AuthGrpc.AuthBlockingStub authStub;

    private JwtToken jwt;
    private boolean autoRefresh = true;

    /**
     *
     * @param token secret token (like `emrld_y40SYbbZclSZPX4r6nL9hNKUGaknAwqyv2qslI`)
     * @param authStub Auth API stub
     */
    public TokenCredentials(String token, AuthGrpc.AuthBlockingStub authStub) {
        this.token = token;
        this.authStub = authStub;
    }

    /**
     * Blocking call to authenticate and get a JWT token
     */
    public void authSync() {
        AuthOuterClass.AuthResponse response = authStub.authenticate(
                AuthOuterClass.AuthRequest.newBuilder()
                        //TODO module version
                        .addAgentDetails("emerald-client-java/0.14.0")
                        .setAuthSecret(token)
                        .addCapabilities("JWT_RS256")
                        .build()
        );
        if (response.getStatus() != 0) {
            throw new RuntimeException("Failed to authenticate: code=" + response.getStatus() + ", message=" + response.getDenyMessage());
        }
        jwt = new JwtToken(
                response.getAccessToken(),
                Instant.ofEpochMilli(response.getExpiresAt()),
                response.getRefreshToken()
        );
        scheduleRefresh();
    }

    /**
     * Asynchronous call to authenticate and get a JWT token
     */
    public void authAsync() {
        executor.submit(this::authSync);
    }

    /**
     * Blocking call to refresh the JWT token
     */
    public void refreshSync() {
        AuthOuterClass.AuthResponse response = authStub.refresh(
                AuthOuterClass.RefreshRequest.newBuilder()
                        .setAuthSecret(token)
                        .setRefreshToken(jwt.getRefreshToken())
                        .build()
        );
        if (response.getStatus() != 0) {
            jwt = null;
            throw new RuntimeException("Failed to refresh: code=" + response.getStatus() + ", message=" + response.getDenyMessage());
        }
        jwt = new JwtToken(
                response.getAccessToken(),
                Instant.ofEpochMilli(response.getExpiresAt()),
                response.getRefreshToken()
        );
        scheduleRefresh();
    }

    /**
     * Asynchronous call to refresh the JWT token
     */
    public void refreshAsync() {
        executor.submit(this::refreshSync);
    }

    private void scheduleRefresh() {
        if (!autoRefresh) {
            return;
        }
        long delay = jwt.getExpireAt().toEpochMilli() - System.currentTimeMillis() - REFRESH_ADVANCE;
        if (delay < 0) {
            delay = 0;
        }
        executor.schedule(this::refreshSync, delay, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     *
     * @return true if the token refreshes automatically before it expires
     */
    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    /**
     * Set if the token should refresh automatically before it expires
     *
     * @param autoRefresh true to enable auto refresh (default is true)
     */
    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    /**
     *
     * @return current JWT or null
     */
    public JwtToken getJwt() {
        return jwt;
    }

    @Override
    public boolean isReady() {
        return jwt != null && jwt.getExpireAt().isAfter(Instant.now());
    }

    @Override
    public void request(AuthHolder caller) {
        executor.submit(() -> {
            if (jwt == null) {
                authSync();
            } else {
                refreshSync();
            }
            caller.setAuth(this);
        });
    }

    @Override
    public void accept(Metadata metadata) {
        if (jwt == null) {
            throw new IllegalStateException("JWT Token is not set");
        }
        metadata.put(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER), "Bearer " + jwt.getJwt());
    }
}
