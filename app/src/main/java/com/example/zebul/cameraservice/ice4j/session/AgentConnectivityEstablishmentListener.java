package com.example.zebul.cameraservice.ice4j.session;

import com.example.signaling_message.ClientId;
import com.example.utils.LOGGER;
import com.example.zebul.cameraservice.ice4j.ClientConnectionResultListener;
import com.example.zebul.cameraservice.ice4j.ClientConnections;
import com.example.zebul.cameraservice.ice4j.ClientStreamConnection;

import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.CandidatePair;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.IceProcessingState;
import org.ice4j.socket.IceSocketWrapper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by bartek on 18.03.17.
 */

public class AgentConnectivityEstablishmentListener
        implements PropertyChangeListener {

    private ClientConnectionResultListener clientConnectionResultListener;
    private ClientId remoteClientId;

    public AgentConnectivityEstablishmentListener(
            ClientConnectionResultListener clientConnectionResultListener,
            ClientId remoteClientId){

        this.clientConnectionResultListener = clientConnectionResultListener;
        this.remoteClientId = remoteClientId;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

        boolean isAgent = propertyChangeEvent.getSource() instanceof Agent;
        if(!isAgent){
            return;
        }

        Agent agent = (Agent) propertyChangeEvent.getSource();
        if(!agent.getState().equals(IceProcessingState.TERMINATED)){
            return;
        }

        ClientConnections clientConnections = new ClientConnections();
        // Your agent is connected. Terminated means ready to communicate
        for (IceMediaStream stream: agent.getStreams()) {

            for(String streamName: ClientConnections.STREAM_NAMES){

                if(stream.getName().contains(streamName)){

                    Component rtpComponent = stream.getComponent(org.ice4j.ice.Component.RTP);
                    CandidatePair rtpPair = rtpComponent.getSelectedPair();
                    // We use IceSocketWrapper, but you can just use the UDP socket
                    // The advantage is that you can change the protocol from UDP to TCP easily
                    // Currently only UDP exists so you might not need to use the wrapper.
                    IceSocketWrapper wrapper  = rtpPair.getIceSocketWrapper();
                    // Get information about remote address for packet settings

                    TransportAddress ta = rtpPair.getRemoteCandidate().getTransportAddress();
                    InetAddress inetAddress = ta.getAddress();
                    int port = ta.getPort();

                    DatagramSocket socket = wrapper.getUDPSocket();
                    InetSocketAddress address = new InetSocketAddress(inetAddress, port);
                    ClientStreamConnection clientStreamConnection = new ClientStreamConnection(socket, address);
                    clientConnections.putClientStreamConnection(streamName, clientStreamConnection);
                }
            }
        }
        clientConnectionResultListener.onClientConnectionSuccess(remoteClientId, clientConnections);
    }
}
