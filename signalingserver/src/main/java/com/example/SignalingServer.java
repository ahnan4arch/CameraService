package com.example;

import com.example.message.ExchangeSDPRequest;
import com.example.message.ExchangeSDPResponse;
import com.example.message.KeepAliveRequest;
import com.example.message.Message;
import com.example.message.MessageType;
import com.example.message.SignalingMessage;
import com.example.udp.SocketEngine;
import com.example.udp.SocketMessageReceptionListener;
import com.example.utils.GenericSerializer;

import java.util.Scanner;

public class SignalingServer {

    public static void main(String [] args){


        SignalingServer signalingServer = new SignalingServer(9999, new BleBle());
        signalingServer.start();
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
        signalingServer.stop();
    }

    private SocketEngine socketEngine;
    private SignalingMessageProcessor signalingMessageProcessor;

    public SignalingServer(int port, SignalingMessageProcessor signalingMessageProcessor){

        socketEngine = new SocketEngine(port, new SocketMessageReceptionListener() {
            @Override
            public void onSocketMessageReceived(Message message_) {

                try {
                    final byte[] data = (byte[]) message_.getData();
                    SignalingMessage signalingMessage = GenericSerializer.deserialize(data, SignalingMessage.class);
                    processMessage(signalingMessage);
                }
                catch(Exception exc_){

                }
            }
        });
        this.signalingMessageProcessor = signalingMessageProcessor;
    }

    public void start(){

        socketEngine.start();
    }

    public void stop(){

        socketEngine.stop();
    }

    public void processMessage(SignalingMessage signalingMessage){

        final MessageType messageType = signalingMessage.getMessageType();
        switch(messageType){

            case KEEP_ALIVE_REQUEST:
                signalingMessageProcessor.onKeepAliveRequest((KeepAliveRequest)signalingMessage);
                break;
            case EXCHANGE_SDP_REQUEST:
                signalingMessageProcessor.onExchangeSDPRequest((ExchangeSDPRequest)signalingMessage);
                break;
            case EXCHANGE_SDP_RESPONSE:
                signalingMessageProcessor.onExchangeSDPResponse((ExchangeSDPResponse)signalingMessage);
                break;
        }
    }

}
