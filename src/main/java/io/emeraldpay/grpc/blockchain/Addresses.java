package io.emeraldpay.grpc.blockchain;

import io.emeraldpay.api.proto.Common;

import java.util.List;

public class Addresses {

    public static Common.AnyAddress anyAddress(String... address) {
        if (address == null || address.length == 0) {
            return Common.AnyAddress.newBuilder().build();
        }
        if (address.length == 1) {
            return singleAddressAsAny(address[0]);
        }
        return multiAddressAsAny(address);
    }

    // ----

    public static Common.AnyAddress anyAddress(Common.SingleAddress address) {
        return Common.AnyAddress.newBuilder()
                .setAddressSingle(address)
                .build();
    }

    public static Common.AnyAddress anyAddress(Common.MultiAddress address) {
        return Common.AnyAddress.newBuilder()
                .setAddressMulti(address)
                .build();
    }

    public static Common.AnyAddress anyAddress(Common.XpubAddress address) {
        return Common.AnyAddress.newBuilder()
                .setAddressXpub(address)
                .build();
    }

    public static Common.AnyAddress anyAddress(Common.ReferenceAddress address) {
        return Common.AnyAddress.newBuilder()
                .setAddressRef(address)
                .build();
    }

    // ----

    public static Common.AnyAddress singleAddressAsAny(String address) {
        return anyAddress(singleAddress(address));
    }

    public static Common.SingleAddress singleAddress(String address) {
        return Common.SingleAddress.newBuilder()
                .setAddress(address)
                .build();
    }

    public static Common.AnyAddress multiAddressAsAny(List<String> addresses) {
        return anyAddress(multiAddress(addresses));
    }

    public static Common.AnyAddress multiAddressAsAny(String[] addresses) {
        return anyAddress(multiAddress(addresses));
    }

    public static Common.MultiAddress multiAddress(List<String> addresses) {
        return multiAddress(addresses.toArray(new String[0]));
    }

    public static Common.MultiAddress multiAddress(String[] addresses) {
        Common.MultiAddress.Builder result = Common.MultiAddress.newBuilder();
        for (String address : addresses) {
            result.addAddresses(singleAddress(address));
        }
        return result.build();
    }

}
