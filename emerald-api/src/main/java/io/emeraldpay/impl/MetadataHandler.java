package io.emeraldpay.impl;

import io.grpc.Metadata;

import java.util.function.Consumer;

/**
 * A handler for request headers (Metadata). Supposed to add authentication headers.
 */
public interface MetadataHandler extends Consumer<Metadata> {

    /**
     *
     * @return true if the handler has all the necessary data to be used
     */
    boolean isReady();

    /**
     * Request a refresh of the data. This is used when the handler is not ready yet.
     * MUST work asynchronously.
     *
     * @param caller where to return the new instance (if needed)
     */
    void request(AuthHolder caller);

    /**
     * Does nothing and always ready
     */
    class Empty implements MetadataHandler {

        @Override
        public void accept(Metadata metadata) {
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void request(AuthHolder caller) {
        }

    }
}
