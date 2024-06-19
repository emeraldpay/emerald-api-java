package io.emeraldpay.api

import io.emeraldpay.api.proto.MonitoringGrpc
import io.emeraldpay.api.proto.MonitoringOuterClass
import spock.lang.Requires
import spock.lang.Specification

@Requires({
    System.getenv("EMRLD_TEST") != null
})
class EmeraldConnectionAuthIntegrationSpec extends Specification {

    def "Ping the staging server with auth"() {
        setup:
        def connection = EmeraldConnection.newBuilder()
            .connectTo(URI.create("https://api.emeraldpay.dev"))
            .withAuthToken("emrld_8ntrHbZN67DF8TWKgCMO1I9nSaMG0cpoMhj3GP")
            .build()
        def monitoring = MonitoringGrpc.newBlockingStub(connection.channel)
            .withInterceptors(connection.credentials)
        when:
        def response = monitoring.ping(MonitoringOuterClass.PingRequest.newBuilder().build())
        then:
        response != null
    }

}
