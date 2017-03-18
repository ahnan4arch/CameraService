package com.example.zebul.cameraservice.ice4j;

import com.example.signaling_message.ClientId;

/**
 * Created by bartek on 16.03.17.
 */

public interface ClientConnectionResultListener {

    void onClientConnectionSuccess(ClientId remoteClientId, ClientConnections clientConnections);
    void onClientConnectionFailure(ClientId remoteClientId, Exception exc);
}
