package io.emeraldpay.api


import spock.lang.Specification

class EmeraldConnectionBuilderSpec extends Specification {

    def "Parse host with port"() {
        when:
        def act = EmeraldConnection.newBuilder()
                .connectTo("localhost:1234")
        then:
        act.port == 1234
        act.host == "localhost"
    }

    def "Use host if only provided"() {
        when:
        def act = EmeraldConnection.newBuilder()
                .connectTo("google.com")
        then:
        act.port == null
        act.host == "google.com"
    }
}
