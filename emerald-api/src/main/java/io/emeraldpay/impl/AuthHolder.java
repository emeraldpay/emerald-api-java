package io.emeraldpay.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Holds the authentication handler
 */
public class AuthHolder {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<Consumer<MetadataHandler>> authQueue = new ArrayList<>();

    private MetadataHandler auth;
    private Instant requestedAt;

    /**
     * Create an instance of the handler.
     *
     * @param auth current auth, can be null
     */
    public AuthHolder(MetadataHandler auth) {
        this.auth = auth;
    }

    /**
     * Wait for the authentication to be set and ready.
     * If it's already set and ready, the listener will be called immediately.
     *
     * @param listener a listener to be called when the authentication is ready
     */
    public void awaitAuth(Consumer<MetadataHandler> listener) {
        lock.writeLock().lock();
        try {
            if (auth != null && auth.isReady()) {
                listener.accept(auth);
            } else {
                authQueue.add(listener);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get current auth handler, if it's set and ready. If it's set but is not ready, it will automatically request a refresh.
     * If returns `null` subscribe for a callback with `awaitAuth`
     *
     * @return current auth handler, or null if it's not ready
     * @see #awaitAuth(Consumer)
     */
    public MetadataHandler getAuth() {
        lock.readLock().lock();
        try {
            MetadataHandler auth = this.auth;
            if (auth != null && auth.isReady()) {
                return auth;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            // simultaneous calls may trigger multiple refreshes, so check if it's already requested
            if (auth == null || requestedAt == null || requestedAt.isBefore(Instant.now().minusSeconds(60))) {
                requestedAt = Instant.now();
                auth.request(this);
            }
        } finally {
            lock.writeLock().unlock();
        }
        return null;
    }

    /**
     * Set the authentication handler and fire all listeners
     *
     * @param auth new auth handler
     */
    public void setAuth(MetadataHandler auth) {
        lock.writeLock().lock();
        try {
            this.auth = auth;
            if (auth.isReady()) {
                for (Consumer<MetadataHandler> listener : authQueue) {
                    try {
                        listener.accept(auth);
                    } catch (Exception e) {
                        System.err.println("Error in auth listener: " + e.getMessage());
                        // ignore any exceptions in the listener, it should not block the auth update
                    }
                }
                authQueue.clear();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
