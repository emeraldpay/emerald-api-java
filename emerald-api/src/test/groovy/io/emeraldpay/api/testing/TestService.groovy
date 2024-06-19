package io.emeraldpay.api.testing

import io.emeraldpay.api.proto.MonitoringGrpc
import io.emeraldpay.api.proto.MonitoringOuterClass
import io.grpc.stub.StreamObserver

class TestService extends MonitoringGrpc.MonitoringImplBase {

    int calls = 0

    @Override
    void ping(MonitoringOuterClass.PingRequest request, StreamObserver<MonitoringOuterClass.PongResponse> responseObserver) {
        calls++
        responseObserver.onNext(MonitoringOuterClass.PongResponse.newBuilder().build())
        responseObserver.onCompleted()
    }
}