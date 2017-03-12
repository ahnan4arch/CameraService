package com.example;

import com.example.message_pipe.incoming.IncomingMessageDecompressingPipe;
import com.example.message_pipe.incoming.IncomingMessageDeserializingPipe;
import com.example.message_pipe.outgoing.OutgoingMessageCompressingPipe;
import com.example.message_pipe.outgoing.OutgoingMessageSerializingPipe;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.signaling_message.KeepAliveRequest;
import com.example.signaling_message.Message;
import com.example.signaling_message.MessagePipe;
import com.example.signaling_message.MessagePipeline;
import com.example.signaling_message.MessagePipelineEndpoint;
import com.example.signaling_message.MessageType;
import com.example.signaling_message.SignalingMessage;
import com.example.udp.SocketEngine;
import com.example.udp.SocketMessageReceptionListener;
import com.example.utils.GenericSerializer;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class SignalingServer implements SocketMessageReceptionListener, MessagePipelineEndpoint {

    public static void main(String [] args){

        SignalingServer signalingServer = new SignalingServer(9999);
        signalingServer.start();
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
        signalingServer.stop();
    }

    protected SocketEngine socketEngine;
    protected RoutingTable routingTable;
    protected MessagePipeline incomingMessagePipeline;
    protected MessagePipeline outgoingMessagePipeline;

    public SignalingServer(int port){

        socketEngine = new SocketEngine(port, this);
        setUp(this);
    }

    protected SignalingServer(MessagePipelineEndpoint outgoingMessageEndpoint){

        setUp(outgoingMessageEndpoint);
    }

    protected void setUp(MessagePipelineEndpoint outgoingMessageEndpoint){

        incomingMessagePipeline = createIncomingMessagePipeline();
        outgoingMessagePipeline = createOutgoingMessagePipeline();
        routingTable = createRoutingTable(outgoingMessagePipeline);

        attachPipesToIncomingMessagePipeline();
        incomingMessagePipeline.setMessageEndpoint(routingTable);

        attachPipesToOutgoingMessagePipeline();
        outgoingMessagePipeline.setMessageEndpoint(outgoingMessageEndpoint);
    }

    private void start() {
        socketEngine.start();
    }

    private void stop() {
        socketEngine.stop();
    }

    @Override
    public void onSocketMessageReceived(Message message_) {

        // socket >>>---incoming msg pipeline--->>> routing table
        incomingMessagePipeline.transmit(message_);
    }

    @Override
    public void onTransmittedMessage(Message message_) {

        // socket <<<---outgoing msg pipeline---<<< routing table
        socketEngine.post(message_);
    }

    protected MessagePipeline createIncomingMessagePipeline() {

        return new MessagePipeline();
    }

    protected MessagePipeline createOutgoingMessagePipeline() {

        return new MessagePipeline();
    }

    protected RoutingTable createRoutingTable(MessagePipeline outgoingMessagePipeline) {

        return new RoutingTable(outgoingMessagePipeline);
    }

    protected void attachPipesToIncomingMessagePipeline(){

        for(MessagePipe messagePipe: createIncomingMessagePipes()){
            incomingMessagePipeline.addMessagePipe(messagePipe);
        }
    }

    protected void attachPipesToOutgoingMessagePipeline(){

        for(MessagePipe messagePipe: createOutgoingMessagePipes()){
            outgoingMessagePipeline.addMessagePipe(messagePipe);
        }
    }

    protected static MessagePipe[] createIncomingMessagePipes() {
        return new MessagePipe[]{
                new IncomingMessageDecompressingPipe(),
                new IncomingMessageDeserializingPipe()};
    }

    protected static MessagePipe[] createOutgoingMessagePipes() {
        return new MessagePipe[]{
                new OutgoingMessageSerializingPipe(),
                new OutgoingMessageCompressingPipe()};
    }
}
