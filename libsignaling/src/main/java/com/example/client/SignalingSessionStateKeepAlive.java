package com.example.client;

import com.example.message.Message;
import com.example.message.TransmissionException;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.KeepAliveRequest;
import com.example.utils.Timeout;
import com.example.utils.TimeoutStatus;

import java.util.concurrent.TimeUnit;

/**
 * Created by zebul on 3/14/17.
 */

public class SignalingSessionStateKeepAlive extends SignalingSessionState{

    private Timeout retransmitInterval = new Timeout(30, TimeUnit.SECONDS, TimeoutStatus.SET);

    public void update(SignalingSession signalingSession)
    {
        if(retransmitInterval.isSet()){

            try{

                final ClientId senderId = signalingSession.getLocalClientId();
                final KeepAliveRequest keepAliveRequest = new KeepAliveRequest(senderId);
                signalingSession.transmitViaOutgoingMessagePipeline(new Message(senderId, keepAliveRequest));
            }
            catch(TransmissionException exc){
                exc.printStackTrace();
            }
            retransmitInterval.reset();
        }
    }

    @Override
    public void beginExchangeSDPWithRemoteClient(
            SignalingSession signalingSession,
            SDPProducer sdpProducer,
            SDPConsumer sdpConsumer,
            ClientId remoteClientId) {

        SignalingSessionStateExchangeSDP exchangeSDP = new SignalingSessionStateExchangeSDP(
            sdpProducer,
            sdpConsumer,
            remoteClientId);

        signalingSession.transitionTo(exchangeSDP);
    }
}
