package io.emeraldpay.impl;

import io.grpc.Attributes;
import io.grpc.ClientCall;
import io.grpc.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * A wrapper that waits for an actual ClientCall (delegate) and then forwards all operations to it.
 * All operation called before the delegate is set are stored and executed on the delegate later.
 */
public class DeferredClientCall<ReqT, RespT>
        extends ClientCall<ReqT, RespT>
        implements Consumer<ClientCall<ReqT, RespT>> {

    private ClientCall<ReqT, RespT> delegate;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final List<Consumer<ClientCall<ReqT, RespT>>> operations = new ArrayList<>();

    @Override
    public void start(Listener<RespT> responseListener, Metadata headers) {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                delegate.start(responseListener, headers);
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (delegate != null) {
                delegate.start(responseListener, headers);
                return;
            }
            operations.add(call -> call.start(responseListener, headers));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void request(int numMessages) {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                delegate.request(numMessages);
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (delegate != null) {
                delegate.request(numMessages);
                return;
            }
            operations.add(call -> call.request(numMessages));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void cancel(String message, Throwable cause) {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                delegate.cancel(message, cause);
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (delegate != null) {
                delegate.cancel(message, cause);
                return;
            }
            operations.add(call -> call.cancel(message, cause));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void halfClose() {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                delegate.halfClose();
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (delegate != null) {
                delegate.halfClose();
                return;
            }
            operations.add(ClientCall::halfClose);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void sendMessage(ReqT message) {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                delegate.sendMessage(message);
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (delegate != null) {
                delegate.sendMessage(message);
                return;
            }
            operations.add(call -> call.sendMessage(message));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setDelegate(ClientCall<ReqT, RespT> delegate) {
        lock.writeLock().lock();
        try {
            this.delegate = delegate;
            for (Consumer<ClientCall<ReqT, RespT>> operation : operations) {
                operation.accept(delegate);
            }
            operations.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void accept(ClientCall<ReqT, RespT> clientCall) {
        this.setDelegate(clientCall);
    }

    @Override
    public void setMessageCompression(boolean enabled) {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                delegate.setMessageCompression(enabled);
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if (delegate != null) {
                delegate.setMessageCompression(enabled);
                return;
            }
            operations.add(call -> call.setMessageCompression(enabled));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean isReady() {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                return delegate.isReady();
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Attributes getAttributes() {
        lock.readLock().lock();
        try {
            if (delegate != null) {
                return delegate.getAttributes();
            }
            return Attributes.EMPTY;
        } finally {
            lock.readLock().unlock();
        }
    }
}
