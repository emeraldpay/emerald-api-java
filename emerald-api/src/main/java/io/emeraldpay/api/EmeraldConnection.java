package io.emeraldpay.api;

import io.emeraldpay.api.proto.AuthGrpc;
import io.emeraldpay.impl.AuthHolder;
import io.emeraldpay.impl.AuthInterceptor;
import io.emeraldpay.impl.TokenCredentials;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;

import java.net.InetAddress;
import java.net.URI;
import java.util.function.Function;

/**
 * A connection configuration to access Emerald APIs
 */
public class EmeraldConnection {

    private final Channel channel;
    private final ClientInterceptor credentials;

    public EmeraldConnection(Channel channel) {
        this.channel = channel;
        this.credentials = null;
    }

    public EmeraldConnection(Channel channel, ClientInterceptor credentials) {
        this.channel = channel;
        this.credentials = credentials;
    }

    /**
     *
     * @return a default connection
     */
    public static EmeraldConnection newDefault() {
        return newBuilder().build();
    }

    /**
     * @return a default configuration builder
     * @see #newDefault()
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    public Channel getChannel() {
        return channel;
    }

    /**
     * Credentials used to authenticate on Emerald API calls
     * @return credentials
     */
    public ClientInterceptor getCredentials() {
        return credentials;
    }

    /**
     * Check if the connection has credentials
     * @return true if credentials are set
     */
    public boolean hasCredentials() {
        return credentials != null;
    }

    public static class Builder {
        private String host;
        private Integer port;
        private boolean usePlaintext = false;
        private boolean useLoadBalancing = true;

        /**
         * Default Netty config allows messages up to 4Mb, but in practice Ethereum RPC responses may be larger. Here it allows up to 32Mb by default.
         */
        private Integer maxMessageSize = 32 * 1024 * 1024;

        private Function<NettyChannelBuilder, ManagedChannelBuilder<?>> customChannel = null;

        private String secretToken;

        /**
         * Set target address as a host and port pair
         *
         * @param host remote host
         * @param port remote port
         * @return builder
         */
        public Builder connectTo(String host, int port) {
            this.host = host;
            this.port = port;
            return this;
        }

        /**
         * Set target address, as a host only or as a host:port pair
         *
         * @param host remote address
         * @return builder
         */
        public Builder connectTo(String host) {
            if (host.indexOf(":") > 0) {
                String[] split = host.split(":");
                this.connectTo(split[0], Integer.parseInt(split[1]));
            } else {
                this.host = host;
            }
            return this;
        }

        public Builder connectTo(URI url) {
            boolean isSecure = "https".equals(url.getScheme());
            if (isSecure) {
                this.usePlaintext = false;
            }
            int port = url.getPort();
            if (port == -1) {
                port = isSecure ? 443 : 80;
            }
            return this.connectTo(url.getHost(), port);
        }

        public Builder connectTo(InetAddress host, int port) {
            this.port = port;
            return this.connectTo(host);
        }

        public Builder connectTo(InetAddress host) {
            this.host = host.getHostAddress();
            return this;
        }

        public Builder usePlaintext() {
            this.usePlaintext = true;
            return this;
        }

        public Builder disableLoadBalancing() {
            this.useLoadBalancing = false;
            return this;
        }

        /**
         * St max inbound message size. Default is 32mb.
         *
         * @param value max message size. Set as null to use Netty default config
         * @return builder
         */
        public Builder maxMessageSize(Integer value) {
            this.maxMessageSize = value;
            return this;
        }

        /**
         * Authenticate on Emerald API using the provided secret token
         *
         * @param secret a token like `emrld_y40SYbbZclSZPX4r6nL9hNKUGaknAwqyv2qslI`
         * @return builder
         */
        public Builder withAuthToken(String secret) {
            this.secretToken = secret;
            return this;
        }

        /**
         * Customize Channel Builder by applying any custom options not covered by this Builder
         *
         * @param customChannel function that transforms NettyChannelBuilder prepared by builder before creating api from it
         * @return builder
         */
        public Builder withChannelBuilder(Function<NettyChannelBuilder, ManagedChannelBuilder<?>> customChannel) {
            this.customChannel = customChannel;
            return this;
        }

        protected void initDefaults() {
            if (host == null) {
                host = "api.emrld.io";
            }
            if (port == null) {
                port = 443;
            }
        }

        /**
         * Build the API instance
         *
         * @return Emerald API instance
         */
        public EmeraldConnection build() {
            initDefaults();

            NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder
                    .forAddress(host, port);

            if (port == 80 || usePlaintext) {
                nettyChannelBuilder.usePlaintext();
            }

            if (maxMessageSize != null && maxMessageSize > 0) {
                nettyChannelBuilder.maxInboundMessageSize(maxMessageSize);
            }

            ManagedChannelBuilder<?> channelBuilder;

            if (customChannel != null) {
                channelBuilder = customChannel.apply(nettyChannelBuilder);
            } else {
                channelBuilder = nettyChannelBuilder;
            }

            if (useLoadBalancing) {
                channelBuilder.defaultLoadBalancingPolicy("round_robin");
            }

            Channel channel = channelBuilder.build();

            AuthInterceptor authInterceptor = null;
            if (secretToken != null) {
                if (usePlaintext) {
                    System.err.println("WARNING: Authentication with a secret token over an unsecure plaintext connection.");
                }
                AuthHolder holder = new AuthHolder(new TokenCredentials(secretToken, AuthGrpc.newBlockingStub(channel)));
                authInterceptor = new AuthInterceptor(holder);
            }

            return new EmeraldConnection(channel, authInterceptor);
        }


    }
}
