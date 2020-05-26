package io.emeraldpay.grpc;

import io.emeraldpay.api.proto.ReactorBlockchainGrpc;
import io.grpc.Channel;
import io.grpc.netty.NettyChannelBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Root of Emerald API service
 */
public class EmeraldApi {

    private final ReactorBlockchainGrpc.ReactorBlockchainStub blockchainStub;

    private Channel channel;

    private EmeraldApi(Channel channel) {
        blockchainStub = ReactorBlockchainGrpc.newReactorStub(channel);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public ReactorBlockchainGrpc.ReactorBlockchainStub getBlockchainApi() {
        return blockchainStub;
    }

    public static class Builder {
        private InetAddress host;
        private Integer port;

        public Builder connectTo(String host, int port) throws UnknownHostException {
            this.connectTo(InetAddress.getByName(host), port);
            return this;
        }

        public Builder connectTo(String host) throws UnknownHostException {
            this.connectTo(InetAddress.getByName(host));
            return this;
        }

        public Builder connectTo(InetAddress host, int port) {
            this.connectTo(host);
            this.port = port;
            return this;
        }

        public Builder connectTo(InetAddress host) {
            this.host = host;
            return this;
        }

        protected void initDefaults() throws IOException {
            if (host == null) {
                host = InetAddress.getByName("rpc.emeraldpay.dev");
            }
            if (port == null) {
                port = 443;
            }
        }

        /**
         * Build the API instance
         *
         * @return Emerald API instance
         * @throws Exception if some of config params are invalid
         */
        public EmeraldApi build() throws Exception {
            initDefaults();

            NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(host.getHostAddress(), port)
                    .usePlaintext();

            return new EmeraldApi(channelBuilder.build());
        }


    }
}
