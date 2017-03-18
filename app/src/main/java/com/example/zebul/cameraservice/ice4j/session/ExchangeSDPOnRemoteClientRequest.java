package com.example.zebul.cameraservice.ice4j.session;

import com.example.message.Message;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.utils.LOGGER;
import com.example.utils.Timeout;
import com.example.utils.TimeoutStatus;
import com.example.zebul.cameraservice.ice4j.ClientConnectionResultListener;
import com.example.zebul.cameraservice.ice4j.ClientConnections;
import com.example.zebul.cameraservice.ice4j.SdpUtils;

import org.ice4j.ice.Agent;

import java.util.concurrent.TimeUnit;

import static com.example.zebul.cameraservice.ice4j.session.ExchangeSDPOnLocalClientRequest.obtainSDP;

/**
 * Created by bartek on 17.03.17.
 */

public class ExchangeSDPOnRemoteClientRequest extends SignalingSessionState {

    private static final String TAG = ExchangeSDPOnRemoteClientRequest.class.getSimpleName();
    private Timeout connectTimeout = new Timeout(30, TimeUnit.SECONDS, TimeoutStatus.RESET);
    private ClientId remoteClientId;
    private String remoteClientSdp;
    private boolean awaitsForConnection = false;

    public ExchangeSDPOnRemoteClientRequest(
            ClientId remoteClientId,
            String remoteClientSdp) {

        this.remoteClientId = remoteClientId;
        this.remoteClientSdp = remoteClientSdp;
    }

    @Override
    void update(SignalingSession signalingSession)
    {
        if(connectTimeout.isSet()){

            signalingSession.broadcastClientConnectionFailure(remoteClientId, new Exception("Timeout"));
            signalingSession.transitionTo(new SignalingSessionStateKeepAlive());
        }

        if(awaitsForConnection){

            return;
        }

        awaitsForConnection = true;

        try {
            final ClientId localClientId = signalingSession.getLocalClientId();
            LOGGER.Log(TAG, "client: "+localClientId+
                    ", received remote SDP");

            final Agent agent = signalingSession.getAgent();
            final String localClientSdp = obtainSDP(agent, 10000);

            final ExchangeSDPResponse exchangeSDPResponse =
                    new ExchangeSDPResponse(localClientId, remoteClientId, localClientSdp);
            signalingSession.transmitToRemoteServer(new Message(localClientId, exchangeSDPResponse));
            SdpUtils.parseSDP(agent, remoteClientSdp);
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
}
