package com.example.server;

import com.example.message.TransmissionException;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ErrorResponse;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.signaling_message.KeepAliveRequest;
import com.example.message.Message;
import com.example.message.MessagePipeline;
import com.example.message.MessagePipelineEndpoint;
import com.example.signaling_message.SignalingMessage;
import com.example.utils.LOGGER;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.signaling_message.ErrorCode.RECEIVER_RECORD_NOT_EXISTS;

/**
 * Created by zebul on 3/10/17.
 */

public class RoutingTable implements MessagePipelineEndpoint {

    private static final String TAG = RoutingTable.class.getSimpleName();
    protected ConcurrentHashMap<ClientId, ClientRecord> clientRecords =
            new ConcurrentHashMap<ClientId, ClientRecord>();

    protected MessagePipeline outgoingMessagePipeline;

    public RoutingTable(MessagePipeline outgoingMessagePipeline){

        this.outgoingMessagePipeline = outgoingMessagePipeline;
    }

    @Override
    public void onTransmittedMessage(Message message_) {

        try{
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

        } catch (TransmissionException e) {
            e.printStackTrace();
        }
    }

    private void keepAlive(InetSocketAddress senderAddress, SignalingMessage signalingMessage) {

        final ClientId senderId = signalingMessage.getSenderId();
        final ClientRecord senderRecord = new ClientRecord(senderAddress);
        clientRecords.put(senderId, senderRecord);
    }

    private void onKeepAliveRequest(
            InetSocketAddress senderAddress, KeepAliveRequest keepAliveRequest) {
        //keepAlive(senderAddress, keepAliveRequest);

        final ClientId senderId = keepAliveRequest.getSenderId();
        LOGGER.Log(TAG, "onKeepAliveRequest received from: "+senderId.toString());
    }

    private void onExchangeSDPRequest(
            InetSocketAddress senderAddress, ExchangeSDPRequest exchangeSDPRequest)
            throws TransmissionException {

        final ClientId senderId = exchangeSDPRequest.getSenderId();
        LOGGER.Log(TAG, "onExchangeSDPRequest received from: "+senderId);

        final ClientId receiverId = exchangeSDPRequest.getReceiverId();
        final ClientRecord clientRecord =
                clientRecords.get(receiverId);

        if(clientRecord == null){

            LOGGER.Log(TAG, "onExchangeSDPRequest sender: "+senderId+
                    ", receiver: "+receiverId+" NOT found");

            ErrorResponse errorResponse =
                    exchangeSDPRequest.createErrorResponse(RECEIVER_RECORD_NOT_EXISTS);
            outgoingMessagePipeline.transmit(new Message(senderAddress, errorResponse));
        }
        else{

            LOGGER.Log(TAG, "onExchangeSDPRequest sender: "+senderId+
                    ", receiver: "+receiverId+" found");

            InetSocketAddress receiverAddress = clientRecord.getInetSocketAddress();
            outgoingMessagePipeline.transmit(new Message(receiverAddress, exchangeSDPRequest));
        }
    }

    private void onExchangeSDPResponse(
            InetSocketAddress senderAddress, ExchangeSDPResponse exchangeSDPResponse)
            throws TransmissionException {

        final ClientId senderId = exchangeSDPResponse.getSenderId();
        LOGGER.Log(TAG, "onExchangeSDPResponse received from: "+senderId);

        final ClientId receiverId = exchangeSDPResponse.getReceiverId();
        final ClientRecord clientRecord = clientRecords.get(receiverId);

        if(clientRecord == null){

            LOGGER.Log(TAG, "onExchangeSDPResponse sender: "+senderId+
                    ", receiver: "+receiverId+" NOT found");
            ErrorResponse errorResponse =
                    exchangeSDPResponse.createErrorResponse(RECEIVER_RECORD_NOT_EXISTS);
            outgoingMessagePipeline.transmit(new Message(senderAddress, errorResponse));
        }
        else{

            LOGGER.Log(TAG, "onExchangeSDPResponse sender: "+senderId+
                    ", receiver: "+receiverId+" found");
            InetSocketAddress receiverAddress = clientRecord.getInetSocketAddress();
            outgoingMessagePipeline.transmit(new Message(receiverAddress, exchangeSDPResponse));
        }
    }
}
