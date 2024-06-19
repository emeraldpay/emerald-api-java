package io.emeraldpay.impl

import io.grpc.ClientCall
import io.grpc.Metadata
import spock.lang.Specification

class DeferredClientCallSpec extends Specification {

    def "Makes calls when delegate is provided"() {
        setup:
        def delegate = Mock(ClientCall.class)
        def call = new DeferredClientCall()
        call.accept(delegate)

        when: "calls start"
        call.start(Stub(ClientCall.Listener.class), new Metadata())

        then:
        1 * delegate.start(_, _)

        when: "calls request"
        call.request(1)

        then:
        1 * delegate.request(1)

        when: "calls cancel"
        call.cancel("reason", new RuntimeException())

        then:
        1 * delegate.cancel("reason", _)

        when: "calls halfClose"
        call.halfClose()

        then:
        1 * delegate.halfClose()

        when: "calls sendMessage"
        call.sendMessage("message")

        then:
        1 * delegate.sendMessage("message")

        when: "calls setMessageCompression"
        call.setMessageCompression(true)

        then:
        1 * delegate.setMessageCompression(true)

        when: "calls isReady"
        call.isReady()

        then:
        1 * delegate.isReady()

        when: "calls getAttributes"
        call.getAttributes()

        then:
        1 * delegate.getAttributes()
    }

    def "Is not ready with no delegate"() {
        setup:
        def call = new DeferredClientCall()

        expect:
        !call.isReady()
    }

    def "Awaits for delegate"() {
        setup:
        def delegate = Mock(ClientCall.class)
        def call = new DeferredClientCall()

        when: "calls start"
        call.delegate = null
        call.start(Stub(ClientCall.Listener.class), new Metadata())
        call.delegate = delegate

        then:
        1 * delegate.start(_, _)

        when: "calls request"
        call.delegate = null
        call.request(1)
        call.delegate = delegate

        then:
        1 * delegate.request(1)

        when: "calls cancel"
        call.delegate = null
        call.cancel("reason", new RuntimeException())
        call.delegate = delegate

        then:
        1 * delegate.cancel("reason", _)

        when: "calls halfClose"
        call.delegate = null
        call.halfClose()
        call.delegate = delegate

        then:
        1 * delegate.halfClose()

        when: "calls sendMessage"
        call.delegate = null
        call.sendMessage("message")
        call.delegate = delegate

        then:
        1 * delegate.sendMessage("message")

        when: "calls setMessageCompression"
        call.delegate = null
        call.setMessageCompression(true)
        call.delegate = delegate

        then:
        1 * delegate.setMessageCompression(true)
    }
}
