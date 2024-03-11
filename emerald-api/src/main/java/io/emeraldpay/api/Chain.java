/*
 * Copyright (c) 2016-2019 ETCDEV GmbH, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.emeraldpay.api;

public enum Chain {

    UNSPECIFIED(0, "UNSPECIFIED", "Unknown"),

    BITCOIN(1, "BTC", "Bitcoin"),
    // GRIN(2, "GRIN", "Grin"),

    // Networks with tokens
    ETHEREUM(100, "ETH", "Ethereum"),
    ETHEREUM_CLASSIC(101, "ETC", "Ethereum Classic"),
    FANTOM(102, "FTM", "Fantom"),

    //LIGHTNING(1001, "BTC_LN", "Bitcoin Lightning"),
    MATIC(1002, "MATIC", "Polygon Matic"),
    RSK(1003, "RSK", "Bitcoin RSK"),

    // Testnets
    TESTNET_MORDEN(10001, "MORDEN", "Morden Testnet"),
    TESTNET_KOVAN(10002, "KOVAN", "Kovan Testnet"),
    TESTNET_BITCOIN(10003, "TESTNET_BITCOIN", "Bitcoin Testnet"),
    // TESTNET_FLOONET(10004, "FLOONET", "Floonet Testnet"),
    TESTNET_GOERLI(10005, "GOERLI", "Goerli Testnet"),
    TESTNET_ROPSTEN(10006, "ROPSTEN", "Ropsten Testnet"),
    TESTNET_RINKEBY(10007, "RINKEBY", "Rinkeby Testnet"),
    TESTNET_HOLESKY(10008, "HOLESKY", "Holesky Testnet"),
    TESTNET_SEPOLIA(10009, "SEPOLIA", "Sepolia Testnet");

    private final int id;
    private final String code;
    private final String fullname;

    Chain(int id, String code, String fullname) {
        this.id = id;
        this.code = code;
        this.fullname = fullname;
    }

    /**
     * Get chain by its id. If id is not found, UNSPECIFIED is returned
     *
     * @param id chain id
     * @return chain
     */
    public static Chain byId(int id) {
        for (Chain chain: Chain.values()) {
            if (chain.id == id) {
                return chain;
            }
        }
        return UNSPECIFIED;
    }

    /**
     * Get chain by code. Code is case-insensitive. If code is not found, UNSPECIFIED is returned
     *
     * @param code chain code
     * @return chain
     */
    public static Chain byCode(String code) {
        code = code.toUpperCase();
        for (Chain chain: Chain.values()) {
            if (chain.code.equals(code) || chain.name().equals(code)) {
                return chain;
            }
        }
        return UNSPECIFIED;
    }

    public int getId() {
        return id;
    }

    public String getChainCode() {
        return code;
    }

    public String getChainName() {
        return fullname;
    }
}
