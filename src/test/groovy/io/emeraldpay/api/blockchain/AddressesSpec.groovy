package io.emeraldpay.api.blockchain


import io.emeraldpay.api.proto.Common
import spock.lang.Specification

class AddressesSpec extends Specification {

    def "creates single"() {
        when:
        def act = Addresses.singleAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn1L")
        then:
        act == Common.SingleAddress.newBuilder()
                .setAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn1L")
                .build()
    }

    def "creates multi"() {
        when:
        def act = Addresses.multiAddress(
                [
                        "1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn11",
                        "1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn12"
                ] as String[]
        )
        then:
        act == Common.MultiAddress.newBuilder()
                .addAddresses(
                        Addresses.singleAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn11")
                )
                .addAddresses(
                        Addresses.singleAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn12")
                )
                .build()
    }

    def "create any with single provided"() {
        when:
        def act = Addresses.anyAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn1L")
        then:
        act == Common.AnyAddress.newBuilder().setAddressSingle(
                Common.SingleAddress.newBuilder()
                        .setAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn1L")
                        .build()
        ).build()
    }

    def "create any with two provided"() {
        when:
        def act = Addresses.anyAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn11", "1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn12")
        then:
        act == Common.AnyAddress.newBuilder().setAddressMulti(
                Common.MultiAddress.newBuilder()
                        .addAddresses(
                                Common.SingleAddress.newBuilder().setAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn11")
                        )
                        .addAddresses(
                                Common.SingleAddress.newBuilder().setAddress("1UE99cDGRGB2xMEGhB9ASmLBsF1HEtBn12")
                        )
                        .build()
        ).build()
    }
}
