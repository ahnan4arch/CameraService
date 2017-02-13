package com.example.zebul.cameraservice.communication.server;

/**
 * Created by zebul on 1/5/17.
 */

public interface RTSPSessionEventListener {

    void onRTSPSessionCreatedEvent(RTSPSessionCreatedEvent rtspSessionCreatedEvent);
    void onRTSPSessionDestroyedEvent(RTSPSessionDestroyedEvent rtspSessionDestroyedEvent);
}
