package io.emeraldpay.grpc.blockchain;

import io.emeraldpay.api.proto.BlockchainOuterClass;
import io.emeraldpay.grpc.Chain;

public class EstimateFeeRequestBuilder {

    private Mode mode = Mode.AVG_MIDDLE;
    private int blocks = 3;
    private Chain chain;

    public EstimateFeeRequestBuilder forChain(Chain chain) {
        if (mode == null) {
            throw new NullPointerException("Target Chain cannot be null");
        }
        this.chain = chain;
        return this;
    }

    public EstimateFeeRequestBuilder basedOnBlocks(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Blocks limit must be positive number. Provided: " + n);
        }
        this.blocks = n;
        return this;
    }

    public EstimateFeeRequestBuilder using(Mode mode) {
        if (mode == null) {
            throw new NullPointerException("Estimation Mode cannot be null");
        }
        this.mode = mode;
        return this;
    }

    public BlockchainOuterClass.EstimateFeeRequest build() {
        if (chain == null) {
            throw new NullPointerException("Chain is not set");
        }
        return BlockchainOuterClass.EstimateFeeRequest.newBuilder()
                .setChainValue(chain.getId())
                .setBlocks(blocks)
                .setModeValue(mode.protobufId)
                .build();
    }

    enum Mode {
        /**
         * Average over last transaction in each block
         */
        AVG_LAST(1),
        /**
         * Average over transaction 5th from the end in each block
         */
        AVG_T5(2),
        /**
         * Average over transaction 20th from the end in each block
         */
        AVG_T20(3),
        /**
         * Average over transaction 50th from the end in each block
         */
        AVG_T50(4),
        /**
         * Minimal fee that would be accepted by every last block
         */
        MIN_ALWAYS(5),
        /**
         * Average over transaction in the middle of each block
         */
        AVG_MIDDLE(6),
        /**
         * Average over transaction in head of each block. Note that for Bitcoin it doesn't count COINBASE tx as top tx.
         */
        AVG_TOP(7);

        private final int protobufId;

        Mode(int protobufId) {
            this.protobufId = protobufId;
        }

        public int getProtobufId() {
            return protobufId;
        }
    }
}
