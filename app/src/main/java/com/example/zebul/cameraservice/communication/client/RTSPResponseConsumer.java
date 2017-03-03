package com.example.zebul.cameraservice.communication.client;

import com.example.zebul.cameraservice.av_protocols.rtsp.response.RTSPResponse;

/**
 * Created by zebul on 1/28/17.
 */

public interface RTSPResponseConsumer {

    void consumeRTSPResponse(RTSPResponse response);
}
