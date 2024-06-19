package io.emeraldpay.impl

import io.emeraldpay.api.proto.MonitoringGrpc
import io.emeraldpay.api.proto.MonitoringOuterClass
import io.emeraldpay.api.testing.TestService
import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class AuthInterceptorSpec extends Specification {

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


    def "Uses the existing authentication"() {
        setup:
        def service = new TestService()

        def auth = Spy(MetadataHandler.Empty)
        def holder = Mock(AuthHolder.class)
        def interceptor = new AuthInterceptor(holder)

        grpcCleanup.register(
                InProcessServerBuilder
                        .forName("my-test-case")
                        .directExecutor()
                        .addService(service)
                        .build().start()
        )


        def client = MonitoringGrpc.newFutureStub(channel)

        when:
        def response = client
                .withInterceptors(interceptor)
                .ping(MonitoringOuterClass.PingRequest.newBuilder().build())
                .get(1, TimeUnit.SECONDS)

        then:
        1 * holder.getAuth() >> auth
        1 * auth.accept(_)
        response != null
        service.calls == 1
    }

    def "Request a new authentication"() {
        setup:
        def service = new TestService()

        def auth = Spy(MetadataHandler.Empty)
        def holder = Mock(AuthHolder.class)
        def interceptor = new AuthInterceptor(holder)

        grpcCleanup.register(
                InProcessServerBuilder
                        .forName("my-test-case")
                        .directExecutor()
                        .addService(service)
                        .build().start()
        )

        def client = MonitoringGrpc.newFutureStub(channel)

        when:
        def response = client
                .withInterceptors(interceptor)
                .ping(MonitoringOuterClass.PingRequest.newBuilder().build())
                .get(1, TimeUnit.SECONDS)

        then:
        1 * holder.getAuth() >> null
        1 * holder.awaitAuth(_) >> { args ->
            new Thread({
                Thread.sleep(100)
                args[0].accept(auth)
            }).start()
        }
        1 * auth.accept(_)
        response != null
        service.calls == 1
    }


}
