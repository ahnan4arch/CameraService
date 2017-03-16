package com.example.client;

import com.example.message.Message;
import com.example.message.TransmissionException;
import com.example.signaling_message.ClientId;
import com.example.udp.SocketEngine;
import com.example.udp.SocketMessageReceptionListener;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by zebul on 3/14/17.
 */

public class SignalingClient {

    protected SocketMessageReceptionListener incomingMessageListener =
            new SocketMessageReceptionListener() {

        @Override
        public void onSocketMessageReceived(Message message) {

            try {
                signalingSession.transmitViaIncomingMessagePipeline(message);
            } catch (TransmissionException e) {
                e.printStackTrace();
            }
        }
    };

    protected SocketMessageReceptionListener outgoingMessageListener =
            new SocketMessageReceptionListener() {

        @Override
        public void onSocketMessageReceived(Message message) {

            message.setAddress(serverSocketAddress);
            socketEngine.post(message);
        }
    };

    protected InetSocketAddress serverSocketAddress = new InetSocketAddress("122.168.1.106", 9999);
    protected SocketEngine socketEngine = new SocketEngine(13445, incomingMessageListener);
    protected ScheduledExecutorService scheduledExecutorService;
    protected SignalingSession signalingSession;

    public SignalingClient(ClientId localClientId){

        signalingSession = new SignalingSession(localClientId, outgoingMessageListener);
    }

    public void start(){

        socketEngine.start();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                signalingSession.updateState();
                            }
                        }, 1, 1, TimeUnit.SECONDS);
    }

    public void stop(){

        socketEngine.stop();
        scheduledExecutorService.shutdown();
    }

    public void establishAVConnectionWithClient(
            ClientId remoteClientId,
            AVConnectionResultReceiver avConnectionResultReceiver){

    }

}
