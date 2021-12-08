package io.emeraldpay.grpc.blockchain;

public class BlockchainRequest {

    public static BalanceRequestBuilder balance() {
        return new BalanceRequestBuilder();
    }

    public static EstimateFeeRequestBuilder estimateFee() {
        return new EstimateFeeRequestBuilder();
    }
}
