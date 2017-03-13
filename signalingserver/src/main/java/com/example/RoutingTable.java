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

    protected ConcurrentHashMap<ClientId, ClientRecord> clientRecords =
            new ConcurrentHashMap<ClientId, ClientRecord>();

    protected MessagePipeline outgoingMessagePipeline;

    public RoutingTable(MessagePipeline outgoingMessagePipeline){

        this.outgoingMessagePipeline = outgoingMessagePipeline;
    }

    @Override
    public void onTransmittedMessage(Message message_) {

        final InetSocketAddress senderAddress = (InetSocketAddress) message_.getAddress();
        final SignalingMessage signalingMessage = (SignalingMessage)message_.getData();
        switch(signalingMessage.getMessageType()){

            case KEEP_ALIVE_REQUEST:
                onKeepAliveRequest(senderAddress, (KeepAliveRequest)signalingMessage);
                break;
            case EXCHANGE_SDP_REQUEST:
                onExchangeSDPRequest(senderAddress, (ExchangeSDPRequest)signalingMessage);
                break;
            case EXCHANGE_SDP_RESPONSE:
                onExchangeSDPResponse(senderAddress, (ExchangeSDPResponse)signalingMessage);
                break;
        }
        //let every message be keepalive message
        keepAlive(senderAddress, signalingMessage);
    }

    private void keepAlive(InetSocketAddress senderAddress, SignalingMessage signalingMessage) {

        final ClientId senderId = signalingMessage.getSenderId();
        final ClientRecord senderRecord = new ClientRecord(senderAddress);
        clientRecords.put(senderId, senderRecord);
    }

    private void onKeepAliveRequest(
            InetSocketAddress senderAddress, KeepAliveRequest keepAliveRequest) {

    }

    private void onExchangeSDPRequest(
            InetSocketAddress senderAddress, ExchangeSDPRequest exchangeSDPRequest) {

        final ClientRecord clientRecord =
                clientRecords.get(exchangeSDPRequest.getReceiverId());

        if(clientRecord == null){

            ErrorResponse errorResponse =
                    exchangeSDPRequest.createErrorResponse(RECEIVER_RECORD_NOT_EXISTS);
            outgoingMessagePipeline.transmit(new Message(senderAddress, errorResponse));
        }
        else{

            InetSocketAddress receiverAddress = clientRecord.getInetSocketAddress();
            outgoingMessagePipeline.transmit(new Message(receiverAddress, exchangeSDPRequest));
        }
    }

    private void onExchangeSDPResponse(
            InetSocketAddress senderAddress, ExchangeSDPResponse exchangeSDPResponse) {

        final ClientRecord clientRecord =
                clientRecords.get(exchangeSDPResponse.getReceiverId());

        if(clientRecord == null){

            ErrorResponse errorResponse =
                    exchangeSDPResponse.createErrorResponse(RECEIVER_RECORD_NOT_EXISTS);
            outgoingMessagePipeline.transmit(new Message(senderAddress, errorResponse));
        }
        else{

            InetSocketAddress receiverAddress = clientRecord.getInetSocketAddress();
            outgoingMessagePipeline.transmit(new Message(receiverAddress, exchangeSDPResponse));
        }
    }
}
