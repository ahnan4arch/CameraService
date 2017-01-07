package com.example.zebul.cameraservice.communication.tcp;

import java.net.SocketAddress;

/**
 * Created by zebul on 1/6/17.
 */

public class RTSPSessionCreatedEvent extends RTSPSessionEvent{

    public RTSPSessionCreatedEvent(SocketAddress remoteSocketAddress) {
        super(remoteSocketAddress);
    }
}
