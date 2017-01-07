package com.example.zebul.cameraservice.communication.tcp;

import java.net.SocketAddress;

/**
 * Created by zebul on 1/6/17.
 */

public class RTSPSessionEvent {

    private SocketAddress remoteSocketAddress;

    public RTSPSessionEvent(SocketAddress remoteSocketAddress) {
        this.remoteSocketAddress = remoteSocketAddress;
    }

    public SocketAddress getRemoteSocketAddress() {

        return remoteSocketAddress;
    }
}
