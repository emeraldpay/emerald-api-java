package io.emeraldpay.grpc;

public enum BlockchainType {
    BITCOIN,
    ETHEREUM;

    public static BlockchainType from(Chain chain) {
        if (chain == Chain.ETHEREUM
                || chain == Chain.ETHEREUM_CLASSIC
                || chain == Chain.TESTNET_KOVAN
                || chain == Chain.TESTNET_MORDEN
        ) {
            return BlockchainType.ETHEREUM;
        }
        if (chain == Chain.BITCOIN
                || chain == Chain.TESTNET_BITCOIN
        ) {
            return BlockchainType.BITCOIN;
        }
        throw new IllegalArgumentException("Unknown type of blockchain: " + chain);
    }
}
