package io.emeraldpay.impl;

import io.grpc.*;

public class AuthInterceptor implements ClientInterceptor {

    private final AuthHolder authHolder;

    public AuthInterceptor(AuthHolder authHolder) {
        this.authHolder = authHolder;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        MetadataHandler auth = authHolder.getAuth();
        if (auth != null) {
            // if auth is ready, just use it as is
            return new AuthenticatedClientCall<>(next.newCall(method, callOptions), auth);
        } else {
            // has to wait for auth to be ready
            DeferredClientCall<ReqT, RespT> deferred = new DeferredClientCall<>();
            authHolder.awaitAuth(metadataConsumer -> {
                AuthenticatedClientCall<ReqT, RespT> authenticated = new AuthenticatedClientCall<>(next.newCall(method, callOptions), metadataConsumer);
                deferred.accept(authenticated);
            });
            return deferred;
        }
    }

}
