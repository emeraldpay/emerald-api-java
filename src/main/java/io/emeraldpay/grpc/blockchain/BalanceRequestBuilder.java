package io.emeraldpay.grpc.blockchain;

import io.emeraldpay.api.proto.BlockchainOuterClass;
import io.emeraldpay.api.proto.Common;
import io.emeraldpay.etherjar.domain.Address;
import io.emeraldpay.grpc.BlockchainType;
import io.emeraldpay.grpc.Chain;

public class BalanceRequestBuilder {

    private String address;
    private Chain chain;
    private String asset;
    private boolean includeUtxo = false;

    public BalanceRequestBuilder address(String address) {
        this.address = address;
        return this;
    }

    public BalanceRequestBuilder address(Address address) {
        this.address = address.toHex();
        if (chain == null) {
            chain = Chain.ETHEREUM;
        }
        return this;
    }

    public BalanceRequestBuilder asset(Chain chain, String asset) {
        this.chain = chain;
        this.asset = asset;
        return this;
    }

    public BalanceRequestBuilder asset(Chain chain) {
        BlockchainType type = BlockchainType.from(chain);
        if (type == BlockchainType.ETHEREUM || type == BlockchainType.ETHEREUM_POS) {
            return this.asset(chain, "ether");
        }
        if (type == BlockchainType.BITCOIN) {
            return this.asset(chain, "btc");
        }
        throw new IllegalStateException("No default asset for " + chain);
    }

    public BalanceRequestBuilder includeUtxo(boolean value) {
        this.includeUtxo = value;
        return this;
    }

    public BalanceRequestBuilder includeUtxo() {
        return this.includeUtxo(true);
    }

    public BalanceRequestBuilder excludeUtxo() {
        return this.includeUtxo(false);
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
                .setIncludeUtxo(includeUtxo)
                .build();
    }


}
