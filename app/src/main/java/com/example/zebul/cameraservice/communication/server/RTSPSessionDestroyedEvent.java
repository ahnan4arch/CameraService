package com.example.zebul.cameraservice.communication.server;

import java.net.SocketAddress;

/**
 * Created by zebul on 1/6/17.
 */

public class RTSPSessionDestroyedEvent extends RTSPSessionEvent{

    public RTSPSessionDestroyedEvent (SocketAddress remoteSocketAddress) {
        super(remoteSocketAddress);
    }
}
