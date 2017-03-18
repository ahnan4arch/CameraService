package com.example.zebul.cameraservice.ice4j.session;

import com.example.message.TransmissionException;
import com.example.server.SignalingServer;
import com.example.message.Message;
import com.example.message.MessagePipeline;
import com.example.message.MessagePipelineEndpoint;
import com.example.signaling_message.ClientId;
import com.example.udp.SocketMessageReceptionListener;
import com.example.zebul.cameraservice.ice4j.ClientConnections;
import com.example.zebul.cameraservice.ice4j.IceSignalingClient;

import org.ice4j.ice.Agent;

/**
 * Created by zebul on 3/13/17.
 */

public class SignalingSession {

    protected SocketMessageReceptionListener outgoingMessageReceptionListener;

    protected MessagePipeline incomingMessagePipeline;
    protected MessagePipeline outgoingMessagePipeline;

    protected SignalingSessionState signalingSessionState = new SignalingSessionStateKeepAlive();
    protected IceSignalingClient signalingClient;

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
            IceSignalingClient signalingClient,
            SocketMessageReceptionListener outgoingMessageReceptionListener){

        this.signalingClient = signalingClient;
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

    public void transmitToLocalClient(Message message) throws TransmissionException {

        incomingMessagePipeline.transmit(message);
    }

    public void transmitToRemoteServer(Message message) throws TransmissionException {

        outgoingMessagePipeline.transmit(message);
    }

    public void updateState() {

        signalingSessionState.update(this);
    }

    public void connectWithClient(ClientId remoteClientId) {

        signalingSessionState.connectWith(
                this, remoteClientId);
    }

    public void transitionTo(SignalingSessionState newSignalingSessionState) {

        signalingSessionState = newSignalingSessionState;
    }

    public ClientId getLocalClientId() {
        return signalingClient.getLocalClientId();
    }

    public Agent getAgent() {
        return signalingClient.getAgent();
    }

    void broadcastClientConnectionSuccess(
            ClientId remoteClientId,
            ClientConnections avConnectionBundle) {

        signalingClient.broadcastClientConnectionSuccess(
                remoteClientId, avConnectionBundle);
    }

    void broadcastClientConnectionFailure(
            ClientId remoteClientId,
            Exception exc) {

        signalingClient.broadcastClientConnectionFailure(
                remoteClientId, exc);
    }
}
