package com.example;

import java.net.InetSocketAddress;

/**
 * Created by zebul on 3/11/17.
 */

public class ClientRecord {

    private InetSocketAddress inetSocketAddress;

    public ClientRecord(InetSocketAddress inetSocketAddress) {

        this.inetSocketAddress = inetSocketAddress;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }


}
