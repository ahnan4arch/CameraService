package com.example.udp;

import com.example.signaling_message.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 9/19/16.
 */
public class Sender extends SocketCommunicator {

    private Event event = new Event();
    private List<Message> messagesToSend = Collections.synchronizedList(new LinkedList<Message>());
    private boolean active = true;

    public Sender(DatagramSocket datagramSocket_) {

        super(datagramSocket_);
    }

    @Override
    public void communicate() throws IOException {

        while (!messagesToSend.isEmpty()) {

            Message message = messagesToSend.remove(0);
            InetSocketAddress socketAddress = (InetSocketAddress) message.getAddress();
            byte [] data = (byte[]) message.getData();
            int port = socketAddress.getPort();

            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, socketAddress);
            datagramSocket.send(datagramPacket);
        }
        waitForDataToSendAvailable();
    }

    @Override
    public void shutdown() {

        active = false;
        event.signal();
    }

    private void notifyDataToSendAvailable() {

        event.signal();
    }

    private void waitForDataToSendAvailable() {

        if(active){
            event.waitForSignalInfinite();
        }
    }

    public void post(Message message_) {

        messagesToSend.add(message_);
        notifyDataToSendAvailable();
    }

}
