package com.example.zebul.cameraservice.communication.client;

import com.example.zebul.cameraservice.av_streaming.rtsp.URI;

/**
 * Created by zebul on 2/4/17.
 */

public class ClientSessionSettings {

    String UserAgent;
    URI RequestUri;
    int MinPort;
    int MaxPort;
    public ClientSessionSettings(String userAgent, URI requestUri, int minPort, int maxPort){

        this.UserAgent = userAgent;
        this.RequestUri = requestUri;
        this.MinPort = minPort;
        this.MaxPort = maxPort;
    }
}
