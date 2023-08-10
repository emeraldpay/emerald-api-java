package io.emeraldpay.api.blockchain

import io.emeraldpay.api.EmeraldConnection
import io.emeraldpay.api.proto.BlockchainGrpcKt

class BlockchainApiKt(connect: EmeraldConnection) {

    val stub = BlockchainGrpcKt.BlockchainCoroutineStub(connect.channel)

}