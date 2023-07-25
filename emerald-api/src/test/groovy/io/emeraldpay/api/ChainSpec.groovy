package io.emeraldpay.api

import spock.lang.Specification

class ChainSpec extends Specification {

    def "get by code"() {
        expect:
        Chain.byCode(code) == chain
        where:
        code    | chain
        "BTC"   | Chain.BITCOIN
        "ETH"   | Chain.ETHEREUM
        "ETC"   | Chain.ETHEREUM_CLASSIC
    }

    def "get by name as code"() {
        expect:
        Chain.byCode(code) == chain
        where:
        code                    | chain
        "BITCOIN"               | Chain.BITCOIN
        "ETHEREUM"              | Chain.ETHEREUM
        "ETHEREUM_CLASSIC"      | Chain.ETHEREUM_CLASSIC
    }

    def "get all by code"() {
        expect:
        Chain.byCode(chain.getChainCode()) == chain
        where:
        chain << Chain.values().toList()
    }

    def "get unspecified for a wrong code"() {
        when:
        def act = Chain.byCode("WRONG")
        then:
        act == Chain.UNSPECIFIED
    }

    def "get by code"() {
        expect:
        Chain.byId(code) == chain
        where:
        code    | chain
        1       | Chain.BITCOIN
        100     | Chain.ETHEREUM
        101     | Chain.ETHEREUM_CLASSIC
    }

    def "get all by id"() {
        expect:
        Chain.byId(chain.getId()) == chain
        where:
        chain << Chain.values().toList()
    }

    def "get unspecified for a wrong id"() {
        when:
        def act = Chain.byId(999)
        then:
        act == Chain.UNSPECIFIED
    }
}
