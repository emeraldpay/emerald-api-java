package io.emeraldpay.impl;

import io.grpc.ClientCall;
import io.grpc.Metadata;

import java.util.function.Consumer;

/**
 * A wrapper around a ClientCall that adds authentication headers from provided MetadataHandler.
 * Used by an interceptor when the client is already authenticated. Otherwise, see `DeferredClientCall`
 *
 * @see DeferredClientCall
 */
public class AuthenticatedClientCall<ReqT, RespT> extends ClientCall<ReqT, RespT> {

    private final ClientCall<ReqT, RespT> delegate;
    private MetadataHandler auth;

    public AuthenticatedClientCall(ClientCall<ReqT, RespT> delegate, MetadataHandler auth) {
        this.delegate = delegate;
        this.auth = auth;
    }

    public void setAuth(MetadataHandler auth) {
        this.auth = auth;
    }

    @Override
    public void start(Listener<RespT> responseListener, Metadata headers) {
        auth.accept(headers);
        delegate.start(responseListener, headers);
    }

    @Override
    public void request(int numMessages) {
        delegate.request(numMessages);
    }

    @Override
    public void cancel(String message, Throwable cause) {
        delegate.cancel(message, cause);
    }

    @Override
    public void halfClose() {
        delegate.halfClose();
    }

    @Override
    public void sendMessage(ReqT message) {
        delegate.sendMessage(message);
    }
}
