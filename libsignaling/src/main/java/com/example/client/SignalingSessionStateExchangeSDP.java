package com.example.client;

import com.example.message.Message;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ErrorResponse;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.signaling_message.SignalingMessage;
import com.example.utils.Timeout;
import com.example.utils.TimeoutStatus;

import java.util.concurrent.TimeUnit;

/**
 * Created by zebul on 3/14/17.
 */

public class SignalingSessionStateExchangeSDP extends SignalingSessionState{

    private static final int MAX_NUMBER_OF_ATTEMPTS = 3;
    private Timeout retransmitInterval = new Timeout(3, TimeUnit.SECONDS, TimeoutStatus.SET);

    private SDPProducer sdpProducer;
    private SDPConsumer sdpConsumer;
    private ClientId remoteClientId;

    private int numberOfAttempts = 0;

    public SignalingSessionStateExchangeSDP(
            SDPProducer sdpProducer,
            SDPConsumer sdpConsumer,
            ClientId remoteClientId)
    {
        this.sdpProducer = sdpProducer;
        this.sdpConsumer = sdpConsumer;
        this.remoteClientId = remoteClientId;
    }

    @Override
    public void update(SignalingSession signalingSession) {

        try {

            if (numberOfAttempts > MAX_NUMBER_OF_ATTEMPTS) {

                throw new Exception("Exceeded number of attempts of transmissions SDP requests");
            }

            if (retransmitInterval.isSet()) {

                final ClientId senderId = signalingSession.getLocalClientId();
                final String sdp = sdpProducer.produceSDP();
                final ExchangeSDPRequest exchangeSDPRequest = new ExchangeSDPRequest(
                        senderId, remoteClientId, sdp);
                final Message message = new Message(senderId, exchangeSDPRequest);
                signalingSession.transmitViaOutgoingMessagePipeline(message);
                numberOfAttempts++;
                retransmitInterval.reset();
            }
        }
        catch(Exception exc){

            sdpConsumer.onError(exc);
            signalingSession.transitionTo(new SignalingSessionStateKeepAlive());
        }
    }

    @Override
    public void onSignalingMessage(SignalingSession signalingSession, Message message){

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

        sdpConsumer.onError(new Exception(errorResponse.getErrorCode().toString()));
        signalingSession.transitionTo(new SignalingSessionStateKeepAlive());
    }

    private void onExchangeSDPResponse(
            SignalingSession signalingSession,
            ExchangeSDPResponse exchangeSDPResponse){

        sdpConsumer.consumeSDP(exchangeSDPResponse.getSessionDescription());
        signalingSession.transitionTo(new SignalingSessionStateKeepAlive());
    }
}
