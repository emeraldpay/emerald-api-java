package io.emeraldpay.api.blockchain;

import io.emeraldpay.api.EmeraldConnection;
import io.emeraldpay.api.proto.ReactorBlockchainGrpc;

public class BlockchainApi {

    private EmeraldConnection connection;
    private final ReactorBlockchainGrpc.ReactorBlockchainStub blockchainStub;

    public BlockchainApi(EmeraldConnection connection) {
        this.connection = connection;
        this.blockchainStub = ReactorBlockchainGrpc.newReactorStub(connection.getChannel());
    }

    /**
     * @return Reactive gRPC Stub for Blockchain APIs
     */
    public ReactorBlockchainGrpc.ReactorBlockchainStub getReactorStub() {
        return blockchainStub;
    }

}
