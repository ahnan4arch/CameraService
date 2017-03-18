package com.example.zebul.cameraservice.ice4j.session;

import com.example.message.Message;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ErrorResponse;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.signaling_message.SignalingMessage;
import com.example.utils.LOGGER;
import com.example.utils.Timeout;
import com.example.utils.TimeoutStatus;
import com.example.zebul.cameraservice.ice4j.ClientConnectionResultListener;
import com.example.zebul.cameraservice.ice4j.ClientConnections;
import com.example.zebul.cameraservice.ice4j.ClientStreamConnection;
import com.example.zebul.cameraservice.ice4j.SDPProductionException;
import com.example.zebul.cameraservice.ice4j.SdpBuilder;
import com.example.zebul.cameraservice.ice4j.SdpUtils;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.CandidatePair;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.IceProcessingState;
import org.ice4j.ice.harvest.StunCandidateHarvester;
import org.ice4j.socket.IceSocketWrapper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zebul on 3/14/17.
 */

public class ExchangeSDPOnLocalClientRequest extends SignalingSessionState {

    private static final String TAG = ExchangeSDPOnLocalClientRequest.class.getSimpleName();
    private static final int MAX_NUMBER_OF_ATTEMPTS = 3;
    private Timeout retransmitInterval = new Timeout(3, TimeUnit.SECONDS, TimeoutStatus.SET);

    private ClientId remoteClientId;

    private int numberOfAttempts = 0;
    private String sdp = null;

    public ExchangeSDPOnLocalClientRequest(
            ClientId remoteClientId)
    {
        this.remoteClientId = remoteClientId;
    }

    @Override
    public void update(SignalingSession signalingSession) {

        try {

            if (numberOfAttempts > MAX_NUMBER_OF_ATTEMPTS) {

                throw new Exception("Exceeded number of attempts of transmissions SDP requests");
            }

            if (retransmitInterval.isSet()) {

                final ClientId localClientId = signalingSession.getLocalClientId();
                if(sdp == null){
                    sdp = obtainSDP(signalingSession.getAgent(), 5000);
                }
                final ExchangeSDPRequest exchangeSDPRequest = new ExchangeSDPRequest(
                        localClientId, remoteClientId, sdp);
                final Message message = new Message(localClientId, exchangeSDPRequest);
                signalingSession.transmitToRemoteServer(message);
                numberOfAttempts++;
                retransmitInterval.reset();
            }
        }
        catch(Exception exc){

            signalingSession.broadcastClientConnectionFailure(remoteClientId, exc);
            signalingSession.transitionTo(new SignalingSessionStateKeepAlive());
        }
    }

    @Override
    public void onSignalingMessage(
            SignalingSession signalingSession,
            Message message){

        SignalingMessage signalingMessage = (SignalingMessage) message.getData();
        if(!signalingMessage.comesFrom(remoteClientId)){//process only messages from current remote client
            return;
        }

        switch(signalingMessage.getMessageType()){

            case ERROR_RESPONSE:
                onErrorResponse(signalingSession, (ErrorResponse)signalingMessage);
                break;
            case KEEP_ALIVE_REQUEST:
                break;
            case EXCHANGE_SDP_REQUEST:
                break;
            case EXCHANGE_SDP_RESPONSE:
                onExchangeSDPResponse(signalingSession, (ExchangeSDPResponse)signalingMessage);
                break;
        }
    }

    private void onErrorResponse(
            SignalingSession signalingSession,
            ErrorResponse errorResponse){

        final ClientId localClientId = signalingSession.getLocalClientId();
        LOGGER.Log(TAG, "onErrorResponse client: "+localClientId+", received error: "+errorResponse.getErrorCode());

        if(isCritical(errorResponse)){

            Exception exc= new Exception(errorResponse.getErrorCode().toString());
            signalingSession.broadcastClientConnectionFailure(remoteClientId, exc);
            signalingSession.transitionTo(new SignalingSessionStateKeepAlive());
        }
    }

    private boolean isCritical(ErrorResponse errorResponse) {
        return false;
    }

    private void onExchangeSDPResponse(
            SignalingSession signalingSession,
            ExchangeSDPResponse exchangeSDPResponse){

        try {
            final ClientId localClientId = signalingSession.getLocalClientId();
            LOGGER.Log(TAG, "onExchangeSDPResponse client: "+localClientId+
                    ", received exchangeSDPResponse from: "+remoteClientId);

            final Agent agent = signalingSession.getAgent();
            String remoteClientSDP = exchangeSDPResponse.getSessionDescription();
            SdpUtils.parseSDP(agent, remoteClientSDP);
            AgentConnectivityEstablishmentListener agentConnectivityEstablishmentListener =
                    createAgentConnectivityEstablishmentListener(signalingSession);
            agent.addStateChangeListener(agentConnectivityEstablishmentListener); // We will define this class soon
            agent.startConnectivityEstablishment(); // This will do all the work for you to connect
        } catch (Exception exc) {
            signalingSession.broadcastClientConnectionFailure(remoteClientId, exc);
        }
    }

    private AgentConnectivityEstablishmentListener createAgentConnectivityEstablishmentListener(
            final SignalingSession signalingSession
    ){

        return new AgentConnectivityEstablishmentListener(
                new ClientConnectionResultListener() {
                    @Override
                    public void onClientConnectionSuccess(
                            ClientId remoteClientId, ClientConnections clientConnections) {

                        final ClientId localClientId = signalingSession.getLocalClientId();
                        LOGGER.Log(TAG, "propertyChanged for client: "+localClientId);

                        signalingSession.broadcastClientConnectionSuccess(remoteClientId, clientConnections);
                        signalingSession.transitionTo(new SignalingSessionStateKeepAlive());
                    }

                    @Override
                    public void onClientConnectionFailure(
                            ClientId remoteClientId, Exception exc) {

                    }
                },
                remoteClientId);
    }

    static void initAgent(Agent agent, int streamStartPort) throws UnknownHostException {

        String[] hostnames = new String[] {"jitsi.org", "numb.viagenie.ca", "stun.ekiga.net"};

        for(String hostname: hostnames) {
            TransportAddress address = new TransportAddress(InetAddress.getByName(hostname), 3478, Transport.UDP);
            agent.addCandidateHarvester(new StunCandidateHarvester(address));
        }

        for(String streamName: ClientConnections.STREAM_NAMES){

            try {
                IceMediaStream stream = agent.createMediaStream(streamName);
                agent.createComponent(stream,  Transport.UDP, streamStartPort, streamStartPort, streamStartPort+100);
            } catch (IOException exc) {
                exc.printStackTrace();
            }
            streamStartPort += 1000;
        }
    }

    static String obtainSDP(Agent agent, int port) throws SDPProductionException {


        try {
            initAgent(agent, port);
            SdpBuilder sdpBuilder = new SdpBuilder();
            String localPeerSdpMessage = sdpBuilder.buildSDPDescription(agent);
            return localPeerSdpMessage;
        }
        catch (Throwable throwable) {
            throw new SDPProductionException(throwable);
        }
    }
}
