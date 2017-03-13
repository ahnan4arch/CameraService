package com.example.client;

import com.example.server.SignalingServer;
import com.example.message.Message;
import com.example.message.MessagePipeline;
import com.example.message.MessagePipelineEndpoint;
import com.example.udp.SocketMessageReceptionListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by zebul on 3/13/17.
 */

public class SignalingClient implements SocketMessageReceptionListener, MessagePipelineEndpoint {

    protected SocketMessageSender socketMessageSender;
    protected MessagePipeline incomingMessagePipeline;
    protected MessagePipeline outgoingMessagePipeline;
    protected ScheduledExecutorService scheduledExecutorService;

    public SignalingClient(SocketMessageSender socketMessageSender){

        this.socketMessageSender = socketMessageSender;
        setUp(this);
    }

    public void start(){

        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture scheduledFuture =
        scheduledExecutorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        transmitSignalingMessage();
                    }
                }, 1, 1, TimeUnit.SECONDS);
    }

    public void stop(){

        scheduledExecutorService.shutdown();
    }

    protected void setUp(MessagePipelineEndpoint outgoingMessageEndpoint){

        incomingMessagePipeline = createIncomingMessagePipeline();
        outgoingMessagePipeline = createOutgoingMessagePipeline();

        SignalingServer.attachPipesToIncomingMessagePipeline(incomingMessagePipeline);
        SignalingServer.attachPipesToOutgoingMessagePipeline(outgoingMessagePipeline);
        outgoingMessagePipeline.setMessageEndpoint(outgoingMessageEndpoint);
    }

    protected MessagePipeline createIncomingMessagePipeline() {

        return new MessagePipeline();
    }

    protected MessagePipeline createOutgoingMessagePipeline() {

        return new MessagePipeline();
    }

    @Override
    public void onSocketMessageReceived(Message message) {

        incomingMessagePipeline.transmit(message);
    }

    @Override
    public void onTransmittedMessage(Message message) {

        socketMessageSender.sendMessage(message);
    }

    private void transmitSignalingMessage() {


        //outgoingMessagePipeline
    }
}
