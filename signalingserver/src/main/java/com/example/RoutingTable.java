package com.example;

import com.example.signaling_message.ClientId;
import com.example.signaling_message.ErrorResponse;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.signaling_message.KeepAliveRequest;
import com.example.signaling_message.Message;
import com.example.signaling_message.MessagePipeline;
import com.example.signaling_message.MessagePipelineEndpoint;
import com.example.signaling_message.SignalingMessage;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.signaling_message.ErrorCode.RECEIVER_RECORD_NOT_EXISTS;

/**
 * Created by zebul on 3/10/17.
 */

public class RoutingTable implements MessagePipelineEndpoint {

    protected ConcurrentHashMap<ClientId, ClientRecord> clientAddresses =
            new ConcurrentHashMap<ClientId, ClientRecord>();

    private MessagePipeline outgoingMessagePipeline;
    public RoutingTable(MessagePipeline outgoingMessagePipeline){

        this.outgoingMessagePipeline = outgoingMessagePipeline;
    }

    @Override
    public void onTransmittedMessage(Message message_) {

        final InetSocketAddress address = (InetSocketAddress) message_.getAddress();
        final SignalingMessage signalingMessage = (SignalingMessage)message_.getData();
        switch(signalingMessage.getMessageType()){

            case KEEP_ALIVE_REQUEST:
                onKeepAliveRequest(address, (KeepAliveRequest)signalingMessage);
                break;
            case EXCHANGE_SDP_REQUEST:
                onExchangeSDPRequest(address, (ExchangeSDPRequest)signalingMessage);
                break;
            case EXCHANGE_SDP_RESPONSE:
                onExchangeSDPResponse(address, (ExchangeSDPResponse)signalingMessage);
                break;
        }
    }

    private void onKeepAliveRequest(
            InetSocketAddress senderAddress, KeepAliveRequest keepAliveRequest) {

    }

    private void onExchangeSDPRequest(
            InetSocketAddress senderAddress, ExchangeSDPRequest exchangeSDPRequest) {

        final ClientRecord clientRecord =
                clientAddresses.get(exchangeSDPRequest.getReceiverId());

        if(clientRecord == null){

            ErrorResponse errorResponse =
                    exchangeSDPRequest.createErrorResponse(RECEIVER_RECORD_NOT_EXISTS);
            outgoingMessagePipeline.transmit(new Message(senderAddress, errorResponse));
        }
        else{

        }
    }

    private void onExchangeSDPResponse(
            InetSocketAddress senderAddress, ExchangeSDPResponse exchangeSDPResponse) {

    }
}
