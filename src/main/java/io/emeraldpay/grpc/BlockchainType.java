package io.emeraldpay.grpc;

public enum BlockchainType {
    BITCOIN,
    ETHEREUM,
    ETHEREUM_POS;

    public static BlockchainType from(Chain chain) {
        if (chain == Chain.TESTNET_ROPSTEN
                || chain == Chain.TESTNET_GOERLI
                || chain == Chain.ETHEREUM) {
            return BlockchainType.ETHEREUM_POS;
        }
        if (chain == Chain.ETHEREUM_CLASSIC
                || chain == Chain.MATIC
                || chain == Chain.FANTOM
                || chain == Chain.RSK
                || chain == Chain.TESTNET_KOVAN
                || chain == Chain.TESTNET_MORDEN
                || chain == Chain.TESTNET_RINKEBY
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
