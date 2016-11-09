package com.example.zebul.cameraservice.communication.udp;

import java.io.IOException;
import java.net.DatagramSocket;

/**
 * Created by zebul on 9/19/16.
 */
public abstract class SocketCommunicator {

    protected DatagramSocket datagramSocket;
    public SocketCommunicator(DatagramSocket datagramSocket_){

        datagramSocket = datagramSocket_;
    }

    public abstract void communicate() throws IOException;
    public void shutdown(){}
}
