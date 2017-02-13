package com.example.zebul.cameraservice.communication.client;

/**
 * Created by zebul on 2/4/17.
 */

public interface RTPSessionLifecycleListener {

    void onRTPSetupAudioSession();
    void onRTPSetupVideoSession();
    void onRTPPlay();
    void onRTPTearDownSession();
}
