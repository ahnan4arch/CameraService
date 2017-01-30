package com.example.zebul.cameraservice.communication;

import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;

/**
 * Created by zebul on 1/28/17.
 */

public interface RTSPRequestProducer {

    RTSPRequest produceRTSPRequest();
}
