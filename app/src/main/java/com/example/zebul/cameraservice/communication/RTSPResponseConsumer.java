package com.example.zebul.cameraservice.communication;

import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP5xxServerResponseError;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponse;

/**
 * Created by zebul on 1/28/17.
 */

public interface RTSPResponseConsumer {

    void consumeRTSPResponse(RTSPResponse response);
}
