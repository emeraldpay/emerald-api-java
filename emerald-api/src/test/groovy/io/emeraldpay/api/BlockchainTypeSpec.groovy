package io.emeraldpay.api


import spock.lang.Specification

class BlockchainTypeSpec extends Specification {

    def "Can extract type from all chains"() {
        expect:
        BlockchainType.from(chain) != null
        where:
        chain << Chain.values().toList().findAll { it != Chain.UNSPECIFIED }
    }
}
