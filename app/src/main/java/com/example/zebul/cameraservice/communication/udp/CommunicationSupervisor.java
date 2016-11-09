package com.example.zebul.cameraservice.communication.udp;

/**
 * Created by zebul on 9/19/16.
 */
interface CommunicationSupervisor {

    boolean keepCommunication();
    void onCommunicatorFailure();
}
