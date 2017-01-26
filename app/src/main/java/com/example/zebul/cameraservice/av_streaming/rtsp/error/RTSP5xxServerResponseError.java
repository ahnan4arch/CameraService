package com.example.zebul.cameraservice.av_streaming.rtsp.error;

import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;

/**
 * Created by zebul on 1/18/17.
 */

public class RTSP5xxServerResponseError extends RTSPError {

    private static final long serialVersionUID = -634451493013722317L;

    public RTSP5xxServerResponseError(StatusCode statusCode, String errorMessage) {
        super(statusCode, errorMessage);
    }
}
