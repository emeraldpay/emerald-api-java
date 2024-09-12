package io.emeraldpay.impl

import io.emeraldpay.api.proto.AuthGrpc
import io.emeraldpay.api.proto.AuthOuterClass
import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.testing.GrpcCleanupRule
import spock.lang.Specification

import java.time.Instant
import java.time.temporal.ChronoUnit

class TokenCredentialsSpec extends Specification {

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

    def "isNotReady by default"() {
        setup:
        def credentials = new TokenCredentials("secret_token", AuthGrpc.newBlockingStub(channel))

        when:
        def ready = credentials.isReady()

        then:
        !ready
    }

    def "Makes an authentication"() {
        setup:
        def ttl = Instant.now().plusSeconds(300).truncatedTo(ChronoUnit.SECONDS)
        def service = new AuthService([
                AuthOuterClass.AuthResponse.newBuilder()
                        .setStatus(0)
                        .setAccessToken("jwt_001")
                        .setRefreshToken("refresh_token")
                        .setExpiresAt(ttl.toEpochMilli())
                        .build()
        ]);
        grpcCleanup.register(
                InProcessServerBuilder
                        .forName("my-test-case")
                        .directExecutor()
                        .addService(service)
                        .build().start()
        )

        def credentials = new TokenCredentials("secret_token", AuthGrpc.newBlockingStub(channel))

        when:
        credentials.authSync()
        def jwt = credentials.getJwt()
        def calls = service.requests

        then:
        jwt != null
        jwt.jwt == "jwt_001"
        jwt.refreshToken == "refresh_token"
        jwt.expireAt == ttl
        calls.size() == 1
        with (calls[0] as AuthOuterClass.AuthRequest) {
            authSecret == "secret_token"
        }
        credentials.isReady()
    }

    def "Refreshes the authentication"() {
        setup:
        def ttl = Instant.now().plusSeconds(300).truncatedTo(ChronoUnit.SECONDS)
        def service = new AuthService([
                AuthOuterClass.AuthResponse.newBuilder()
                        .setStatus(0)
                        .setAccessToken("jwt_001")
                        .setRefreshToken("refresh_001")
                        .setExpiresAt(ttl.toEpochMilli())
                        .build(),
                AuthOuterClass.AuthResponse.newBuilder()
                        .setStatus(0)
                        .setAccessToken("jwt_002")
                        .setRefreshToken("refresh_002")
                        .setExpiresAt(ttl.toEpochMilli())
                        .build()

        ]);
        grpcCleanup.register(
                InProcessServerBuilder
                        .forName("my-test-case")
                        .directExecutor()
                        .addService(service)
                        .build().start()
        )

        def credentials = new TokenCredentials("secret_token", AuthGrpc.newBlockingStub(channel))

        when:
        credentials.authSync()
        credentials.refreshSync()
        def jwt = credentials.getJwt()
        def calls = service.requests

        then:
        jwt != null
        jwt.jwt == "jwt_002"
        jwt.refreshToken == "refresh_002"
        jwt.expireAt == ttl
        calls.size() == 2
        with (calls[0] as AuthOuterClass.AuthRequest) {
            authSecret == "secret_token"
        }
        with (calls[1] as AuthOuterClass.RefreshRequest) {
            refreshToken == "refresh_001"
        }
        credentials.isReady()
    }

    class AuthService extends AuthGrpc.AuthImplBase {

        private int i = 0;

        List<Object> requests = []
        List<AuthOuterClass.AuthResponse> responses = []

        AuthService(List<AuthOuterClass.AuthResponse> responses) {
            this.responses = responses
        }

        @Override
        void authenticate(AuthOuterClass.AuthRequest request, StreamObserver<AuthOuterClass.AuthResponse> responseObserver) {
            requests << request
            responseObserver.onNext(responses[i++])
            responseObserver.onCompleted()
        }

        @Override
        void refresh(AuthOuterClass.RefreshRequest request, StreamObserver<AuthOuterClass.AuthResponse> responseObserver) {
            requests << request
            responseObserver.onNext(responses[i++])
            responseObserver.onCompleted()
        }
    }
}
