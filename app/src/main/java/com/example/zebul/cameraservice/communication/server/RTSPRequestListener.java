package com.example.zebul.cameraservice.communication.server;

import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponse;

/**
 * Created by zebul on 1/1/17.
 */

public interface RTSPRequestListener {

    RTSPResponse onDescribe     (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onAnnounce     (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onGetParameter (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onOptions      (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onPause        (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onPlay         (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onRecord       (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onRedirect     (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onSetup        (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onSetParameter (RTSPRequest request)throws RTSP4xxClientRequestError;
    RTSPResponse onTeardown     (RTSPRequest request)throws RTSP4xxClientRequestError;
}
