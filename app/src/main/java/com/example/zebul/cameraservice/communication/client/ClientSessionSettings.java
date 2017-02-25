package com.example.zebul.cameraservice.communication.client;

import com.example.zebul.cameraservice.av_protocols.rtsp.URI;

/**
 * Created by zebul on 2/4/17.
 */

public class ClientSessionSettings {

    String UserAgent;
    URI RequestUri;

    int VideoMinPort;
    int VideoMaxPort;

    int AudioMinPort;
    int AudioMaxPort;

    public ClientSessionSettings(
            String userAgent, URI requestUri,
            int VideoMinPort, int VideoMaxPort,
            int AudioMinPort, int AudioMaxPort){

        this.UserAgent = userAgent;
        this.RequestUri = requestUri;
        this.VideoMinPort = VideoMinPort;
        this.VideoMaxPort = VideoMaxPort;
        this.AudioMinPort = AudioMinPort;
        this.AudioMaxPort = AudioMaxPort;
    }
}
