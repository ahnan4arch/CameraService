package com.example.client;

import com.example.message.TransmissionException;
import com.example.server.SignalingServer;
import com.example.message.Message;
import com.example.message.MessagePipeline;
import com.example.message.MessagePipelineEndpoint;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.SignalingMessage;
import com.example.udp.SocketMessageReceptionListener;

/**
 * Created by zebul on 3/13/17.
 */

public class SignalingSession {

    protected SocketMessageReceptionListener outgoingMessageReceptionListener;

    protected MessagePipeline incomingMessagePipeline;
    protected MessagePipeline outgoingMessagePipeline;

    protected SignalingSessionState signalingSessionState = new SignalingSessionStateKeepAlive();
    protected ClientId localClientId;


    protected MessagePipelineEndpoint incomingMessagePipelineEndpoint = new MessagePipelineEndpoint(){

        @Override
        public void onTransmittedMessage(Message message) {
            signalingSessionState.onSignalingMessage(SignalingSession.this, message);
        }
    };

    protected MessagePipelineEndpoint outgoingMessagePipelineEndpoint = new MessagePipelineEndpoint(){

        @Override
        public void onTransmittedMessage(Message message) {
            outgoingMessageReceptionListener.onSocketMessageReceived(message);
        }
    };

    public SignalingSession(
            ClientId localClientId,
            SocketMessageReceptionListener outgoingMessageReceptionListener){

        this.localClientId = localClientId;
        this.outgoingMessageReceptionListener = outgoingMessageReceptionListener;

        incomingMessagePipeline = createIncomingMessagePipeline();
        outgoingMessagePipeline = createOutgoingMessagePipeline();

        SignalingServer.attachPipesToIncomingMessagePipeline(incomingMessagePipeline);
        SignalingServer.attachPipesToOutgoingMessagePipeline(outgoingMessagePipeline);

        incomingMessagePipeline.setMessageEndpoint(incomingMessagePipelineEndpoint);
        outgoingMessagePipeline.setMessageEndpoint(outgoingMessagePipelineEndpoint);
    }

    protected MessagePipeline createIncomingMessagePipeline() {

        return new MessagePipeline();
    }

    protected MessagePipeline createOutgoingMessagePipeline() {

        return new MessagePipeline();
    }

    void transmitViaIncomingMessagePipeline(Message message) throws TransmissionException {

        incomingMessagePipeline.transmit(message);
    }

    void transmitViaOutgoingMessagePipeline(Message message) throws TransmissionException {

        outgoingMessagePipeline.transmit(message);
    }

    public ClientId getLocalClientId() {
        return localClientId;
    }

    public void updateState() {

        signalingSessionState.update(this);
    }

    public void beginExchangeSDPWithRemoteClient(
            SDPProducer sdpProducer,
            SDPConsumer sdpConsumer,
            ClientId remoteClientId) {

        signalingSessionState.beginExchangeSDPWithRemoteClient(
                this, sdpProducer, sdpConsumer, remoteClientId);
    }

    public void transitionTo(SignalingSessionState newSignalingSessionState) {

        signalingSessionState = newSignalingSessionState;
    }
}
