package com.example.zebul.cameraservice.ice4j;

import com.example.message.Message;
import com.example.message.TransmissionException;
import com.example.signaling_message.ClientId;
import com.example.udp.SocketEngine;
import com.example.udp.SocketMessageReceptionListener;
import com.example.zebul.cameraservice.ice4j.session.SignalingSession;

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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by bartek on 16.03.17.
 */

/*
LOCAL CLIENT                                                                                        REMOTE CLIENT
STATE                               API CALL                                                        STATE                               API CALL
SignalingSessionStateKeepAlive                                                                      SignalingSessionStateKeepAlive

                                    connectWithClient(remoteClient)
ExchangeSDPOnLocalClientRequest
                                    localSdp = obtainsSdp()
                                    transmit(ExchangeSDPRequest(localSdp))                  ---->                                       onExchangeSDPRequest(ExchangeSDPRequest(remoteSdp))
                                                                                                    ExchangeSDPOnRemoteClientRequest
                                                                                                                                        initAgent(agent)
                                                                                                                                        localSdp = buildLocalSdp();
                                                                                                                                        parseAgent(agent, remoteSdp)
                                    onExchangeSDPResponse(ExchangeSDPResponse(remoteSdp))   <----                                       transmit(ExchangeSDPResponse(localSdp))
                                    parseSDP(agent, remoteSDP);                                                                         agent.startConnectivityEstablishment()
                                    agent.startConnectivityEstablishment()                                                              ...
                                    ...                                                                                                    onConnectivityEstablishmentPropertChange()
                                    onConnectivityEstablishmentPropertChange()                      SignalingSessionStateKeepAlive
SignalingSessionStateKeepAlive
*/

public class IceSignalingClient {

    private SocketMessageReceptionListener incomingMessageListener =
            new SocketMessageReceptionListener() {

                @Override
                public void onSocketMessageReceived(Message message) {

                    try {
                        signalingSession.transmitToLocalClient(message);
                    } catch (TransmissionException e) {
                        e.printStackTrace();
                    }
                }
            };

    private SocketMessageReceptionListener outgoingMessageListener =
            new SocketMessageReceptionListener() {

                @Override
                public void onSocketMessageReceived(Message message) {

                    message.setAddress(serverSocketAddress);
                    socketEngine.post(message);
                }
            };

    private InetSocketAddress serverSocketAddress;
    private SocketEngine socketEngine;
    private ScheduledExecutorService scheduledExecutorService;
    private SignalingSession signalingSession;

    private List<ClientConnectionResultListener> clientConnectionResultListeners = new LinkedList<>();
    private Agent agent = null;
    private ClientId localClientId = null;

    public IceSignalingClient(InetSocketAddress serverSocketAddress, ClientId localClientId) {

        this.signalingSession = new SignalingSession(this, outgoingMessageListener);
        this.socketEngine = new SocketEngine(-1, incomingMessageListener);
        this.serverSocketAddress = serverSocketAddress;
        this.localClientId = localClientId;
    }

    public ClientId getLocalClientId() {
        return localClientId;
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

    public void connectWithClient(ClientId remoteClientId) {

        signalingSession.connectWithClient(remoteClientId);
    }

    public Agent getAgent(){

        if(agent == null){
            agent = new Agent();
        }
        return agent;
    }

    public void attachClientConnectionListener(
            ClientConnectionResultListener clientConnectionResultListener) {

        clientConnectionResultListeners.add(clientConnectionResultListener);
    }

    public void broadcastClientConnectionSuccess(
            ClientId remoteClientId,
            ClientConnections clientConnection) {

        for(ClientConnectionResultListener clientConnectionResultListener:
                clientConnectionResultListeners){

            clientConnectionResultListener.onClientConnectionSuccess(remoteClientId, clientConnection);
        }
    }

    public void broadcastClientConnectionFailure(
            ClientId remoteClientId,
            Exception exc) {

        for(ClientConnectionResultListener clientConnectionResultListener:
                clientConnectionResultListeners){

            clientConnectionResultListener.onClientConnectionFailure(remoteClientId, exc);
        }
    }
}
