package io.emeraldpay.api;

public enum BlockchainType {
    BITCOIN,
    ETHEREUM;

    public static BlockchainType from(Chain chain) {
        if (chain == Chain.ETHEREUM
                || chain == Chain.ETHEREUM_CLASSIC
                || chain == Chain.MATIC
                || chain == Chain.FANTOM
                || chain == Chain.RSK
                || chain == Chain.TESTNET_KOVAN
                || chain == Chain.TESTNET_MORDEN
                || chain == Chain.TESTNET_GOERLI
                || chain == Chain.TESTNET_RINKEBY
                || chain == Chain.TESTNET_ROPSTEN
                || chain == Chain.TESTNET_HOLESKY
                || chain == Chain.TESTNET_SEPOLIA
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
