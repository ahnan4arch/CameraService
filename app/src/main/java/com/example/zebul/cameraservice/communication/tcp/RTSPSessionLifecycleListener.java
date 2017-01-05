package com.example.zebul.cameraservice.communication.tcp;

/**
 * Created by zebul on 1/5/17.
 */

public interface RTSPSessionLifecycleListener {

    void onRTSPSessionCreated();
    void onRTSPSessionDestroyed();
}
