package io.emeraldpay.api.blockchain;

public class BlockchainRequest {

    public static BalanceRequestBuilder balance() {
        return new BalanceRequestBuilder();
    }

    public static EstimateFeeRequestBuilder estimateFee() {
        return new EstimateFeeRequestBuilder();
    }
}
