package io.emeraldpay.grpc.blockchain;

import io.emeraldpay.api.proto.BlockchainOuterClass;
import io.emeraldpay.api.proto.Common;
import io.emeraldpay.grpc.BlockchainType;
import io.emeraldpay.grpc.Chain;

public class BalanceRequestBuilder {

    private String address;
    private Chain chain;
    private String asset;

    public BalanceRequestBuilder address(String address) {
        this.address = address;
        return this;
    }

    public BalanceRequestBuilder asset(Chain chain, String asset) {
        this.chain = chain;
        this.asset = asset;
        return this;
    }

    public BalanceRequestBuilder asset(Chain chain) {
        BlockchainType type = BlockchainType.from(chain);
        if (type == BlockchainType.ETHEREUM) {
            return this.asset(chain, "ether");
        }
        if (type == BlockchainType.BITCOIN) {
            return this.asset(chain, "btc");
        }
        throw new IllegalStateException("No default asset for " + chain);
    }

    public BlockchainOuterClass.BalanceRequest build() {
        return BlockchainOuterClass.BalanceRequest.newBuilder()
                .setAddress(
                        Addresses.singleAddressAsAny(address)
                )
                .setAsset(
                        Common.Asset.newBuilder()
                                .setChain(Common.ChainRef.forNumber(chain.getId()))
                                .setCode(asset)
                )
                .build();
    }


}
