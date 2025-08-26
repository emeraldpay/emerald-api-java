package io.emeraldpay.impl

import io.grpc.ClientCall
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.testing.GrpcCleanupRule
import spock.lang.Specification

class AuthenticatedClientCallSpec extends Specification {

    GrpcCleanupRule grpcCleanup
    ManagedChannel channel

    def setup() {
        grpcCleanup = new GrpcCleanupRule()
        channel = grpcCleanup
                .register(InProcessChannelBuilder.forName("my-test-case").directExecutor().build())
    }

    def cleanup() {
        grpcCleanup.after()
    }

    def "Adds headers"() {
        setup:
        def handler = Mock(MetadataHandler.class)
        def call = new AuthenticatedClientCall(Mock(ClientCall.class), handler)
        def meta = new Metadata()

        when:
        call.start(Stub(ClientCall.Listener.class), meta)

        then:
        1 * handler.accept(meta)
    }
}
