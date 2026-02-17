package io.emeraldpay.api;

import io.emeraldpay.api.proto.AuthGrpc;
import io.emeraldpay.impl.AuthHolder;
import io.emeraldpay.impl.AuthInterceptor;
import io.emeraldpay.impl.TokenCredentials;
import io.grpc.Channel;

/**
 * Credentials configuration for Emerald API authentication
 */
public class EmeraldCredentials {

    public static EmeraldCredentials.Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private boolean autoConnect = false;
        private boolean waitForAuth = false;
        private String secretToken;

        /**
         * Authenticate on Emerald API using the provided secret token
         *
         * @param secret a token like `emrld_y40SYbbZclSZPX4r6nL9hNKUGaknAwqyv2qslI`
         * @return builder
         */
        public EmeraldCredentials.Builder withAuthToken(String secret) {
            this.secretToken = secret;
            return this;
        }

        /**
         * Automatically authenticate on Emerald API when the connection is built.
         * By default, it authenticates on the first request, which may cause delays.
         *
         * @return builder
         */
        public EmeraldCredentials.Builder withAutoConnect() {
            this.autoConnect = true;
            return this;
        }

        /**
         * When auto-connect is enabled, wait for authentication to complete before returning the EmeraldConnection instance.
         *
         * @return builder
         */
        public EmeraldCredentials.Builder withWaitingForAuth() {
            this.waitForAuth = true;
            return this;
        }

        /**
         * Setup the credentials instance
         *
         * @param channel the existing channel to use for authentication
         * @return credentials instance
         */
        protected TokenCredentials getOrCreateCredentials(Channel channel) {
            TokenCredentials result = new TokenCredentials(secretToken, AuthGrpc.newBlockingStub(channel));
            if (autoConnect) {
                if (waitForAuth) {
                    result.authSync();
                } else {
                    result.authAsync();
                }
            }
            return result;
        }

        /**
         *
         * @return true if the credentials are configured with a token, false otherwise
         */
        public boolean hasToken() {
            return secretToken != null;
        }

        /**
         * Build the authentication interceptor. It needs the Channel to setup the authentication. It can be the same Channel as the one used for actual requests.
         *
         * @param channel the [existing] channel to use for authentication
         * @return an instance of the authentication interceptor, or null if no authentication is configured
         */
        public AuthInterceptor build(Channel channel) {
            AuthInterceptor authInterceptor = null;
            if (secretToken != null) {
                AuthHolder holder = new AuthHolder(getOrCreateCredentials(channel));
                authInterceptor = new AuthInterceptor(holder);
            }
            return authInterceptor;
        }
    }


}
