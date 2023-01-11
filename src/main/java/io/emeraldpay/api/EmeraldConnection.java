package io.emeraldpay.api;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.NettyChannelBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.function.Function;

/**
 * A connection configuration to access Emerald APIs
 */
public class EmeraldConnection {

    private final Channel channel;

    private EmeraldConnection(Channel channel) {
        this.channel = channel;
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

    public static class Builder {
        private String host;
        private Integer port;
        private boolean usePlaintext = false;

        /**
         * Default Netty config allows messages up to 4Mb, but in practice Ethereum RPC responses may be larger. Here it allows up to 32Mb by default.
         */
        private Integer maxMessageSize = 32 * 1024 * 1024;

        private Function<NettyChannelBuilder, ManagedChannelBuilder<?>> customChannel = null;

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

            return new EmeraldConnection(channelBuilder.build());
        }


    }
}
