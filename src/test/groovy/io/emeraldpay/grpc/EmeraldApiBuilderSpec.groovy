package io.emeraldpay.grpc

import spock.lang.Specification

class EmeraldApiBuilderSpec extends Specification {

    def "Parse host with port"() {
        when:
        def act = EmeraldApi.newBuilder()
                .connectTo("localhost:1234")
        then:
        act.port == 1234
        act.host == "localhost"
    }

    def "Use host if only provided"() {
        when:
        def act = EmeraldApi.newBuilder()
                .connectTo("google.com")
        then:
        act.port == null
        act.host == "google.com"
    }
}
