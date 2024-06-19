package io.emeraldpay.impl

import spock.lang.Specification

class AuthHolderSpec extends Specification {

    def "Returns auth if ready"() {
        setup:
        def auth = Mock(MetadataHandler.class)
        def holder = new AuthHolder(auth)
        when:
        def result = holder.getAuth()
        then:
        1 * auth.ready >> true
        result == auth
    }

    def "Request an update if not ready"() {
        setup:
        def auth = Mock(MetadataHandler.class)
        def holder = new AuthHolder(auth)

        when: "first call"
        def result = holder.getAuth()
        then: "request an update"
        1 * auth.ready >> false
        1 * auth.request(holder)
        result == null

        when: "second call"
        def result2 = holder.getAuth()

        then: "do nothing and wait"
        1 * auth.ready >> false
        0 * auth.request(_)
        result2 == null
    }

    def "Awaiting fires immediately if ready"() {
        setup:
        def auth = Mock(MetadataHandler.class)
        def holder = new AuthHolder(auth)
        when:
        def result = null
        holder.awaitAuth {
            result = it
        }
        then:
        1 * auth.ready >> true
        result == auth
    }

    def "Awaiting fires after setting an auth"() {
        setup:
        def auth1 = Mock(MetadataHandler.class)
        def auth2 = Mock(MetadataHandler.class)
        def holder = new AuthHolder(auth1)
        def results = []

        when:
        holder.awaitAuth {
            results << it
        }
        then:
        1 * auth1.ready >> false
        results == []

        when:
        holder.setAuth(auth2)
        then:
        1 * auth2.ready >> true
        results == [auth2]
    }

}
