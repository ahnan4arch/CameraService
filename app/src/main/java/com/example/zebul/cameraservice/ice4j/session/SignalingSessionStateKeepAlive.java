package com.example.zebul.cameraservice.ice4j.session;

import com.example.message.Message;
import com.example.message.TransmissionException;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.KeepAliveRequest;
import com.example.signaling_message.SignalingMessage;
import com.example.utils.Timeout;
import com.example.utils.TimeoutStatus;

import java.util.concurrent.TimeUnit;

/**
 * Created by zebul on 3/14/17.
 */

public class SignalingSessionStateKeepAlive extends SignalingSessionState {

    private Timeout retransmitInterval = new Timeout(30, TimeUnit.SECONDS, TimeoutStatus.SET);

    public void update(SignalingSession signalingSession) {
        if (retransmitInterval.isSet()) {

            transmitKeepAliveRequest(signalingSession);
            retransmitInterval.reset();
        }
    }

    private void transmitKeepAliveRequest(SignalingSession signalingSession) {

        try {
            final ClientId senderId = signalingSession.getLocalClientId();
            final KeepAliveRequest keepAliveRequest = new KeepAliveRequest(senderId);
            signalingSession.transmitToRemoteServer(new Message(senderId, keepAliveRequest));
        } catch (TransmissionException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void connectWith(
            SignalingSession signalingSession,
            ClientId remoteClientId) {

        ExchangeSDPOnLocalClientRequest exchangeSDP = new ExchangeSDPOnLocalClientRequest(remoteClientId);
        signalingSession.transitionTo(exchangeSDP);
    }


    @Override
    public void onSignalingMessage(
            SignalingSession signalingSession,
            Message message) {

        SignalingMessage signalingMessage = (SignalingMessage) message.getData();
        switch (signalingMessage.getMessageType()) {

            case ERROR_RESPONSE:
                break;
            case KEEP_ALIVE_REQUEST:
                break;
            case EXCHANGE_SDP_REQUEST:
                onExchangeSDPRequest(signalingSession, (ExchangeSDPRequest) signalingMessage);
                break;
            case EXCHANGE_SDP_RESPONSE:
                break;
        }
    }

    private void onExchangeSDPRequest(
            SignalingSession signalingSession,
            ExchangeSDPRequest exchangeSDPRequest) {

        final ClientId remoteClientId = exchangeSDPRequest.getSenderId();
        final String remoteClientSDP = exchangeSDPRequest.getSessionDescription();
        ExchangeSDPOnRemoteClientRequest exchangeSDPOnRemoteClientRequest =
                new ExchangeSDPOnRemoteClientRequest(remoteClientId, remoteClientSDP);
        signalingSession.transitionTo(exchangeSDPOnRemoteClientRequest);
    }
}