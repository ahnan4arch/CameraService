package com.example.zebul.cameraservice.communication.udp;

import com.example.zebul.cameraservice.message.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by zebul on 9/19/16.
 */
public class Receiver extends SocketCommunicator {

    private SocketMessageReceptionListener messageReceptionListener;
    public Receiver(
            DatagramSocket datagramSocket_,
            SocketMessageReceptionListener messageReceptionListener_) {

        super(datagramSocket_);
        messageReceptionListener = messageReceptionListener_;
    }

    @Override
    public void communicate() throws IOException {

        byte[] buffer = new byte[1024*64];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(datagramPacket);

        byte [] data = new byte[datagramPacket.getLength()];
        System.arraycopy(datagramPacket.getData(), 0, data, 0, datagramPacket.getLength());
        Message message = new Message(datagramPacket.getSocketAddress(), data);
        messageReceptionListener.onSocketMessageReceived(message);
    }
}
