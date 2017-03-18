package com.example.zebul.cameraservice.ice4j;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by bartek on 18.03.17.
 */

public class ClientStreamConnection {

    DatagramSocket datagramSocket;
    InetSocketAddress remoteClientAddress;

    public ClientStreamConnection(DatagramSocket datagramSocket,
                                  InetSocketAddress remoteClientAddress) {
        this.datagramSocket = datagramSocket;
        this.remoteClientAddress = remoteClientAddress;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public InetSocketAddress getRemoteClientAddress() {
        return remoteClientAddress;
    }
}
