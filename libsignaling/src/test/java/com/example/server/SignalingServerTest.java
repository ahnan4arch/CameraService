package com.example.server;

import com.example.server.ClientRecord;
import com.example.server.RoutingTable;
import com.example.server.SignalingServer;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ErrorCode;
import com.example.signaling_message.ErrorResponse;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.Message;
import com.example.signaling_message.MessagePipe;
import com.example.signaling_message.MessagePipeline;
import com.example.signaling_message.MessagePipelineEndpoint;
import com.example.signaling_message.SignalingMessage;
import com.example.signaling_message.TransmissionException;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by zebul on 3/10/17.
 */

public class SignalingServerTest {

    private ClientId senderId = new ClientId("foo");
    private ClientId receiverId = new ClientId("bar");
    private InetSocketAddress senderSocketAddress = new InetSocketAddress("127.0.0.1", 5678);
    private InetSocketAddress receiverSocketAddress = new InetSocketAddress("127.0.0.1", 8678);

    class TestableSignalingServer extends SignalingServer {

        TestableSignalingServer(MessagePipelineEndpoint outgoingMessageEndpoint){

            super(outgoingMessageEndpoint);
        }

        @Override
        protected RoutingTable createRoutingTable(MessagePipeline outgoingMessagePipeline) {

            return new TestableRoutingTable(outgoingMessagePipeline);
        }

        Message convertByteMessageToSignalingMessage(Message message) throws TransmissionException {

            return convertMessage(message, createIncomingMessagePipes());
        }

        Message convertSignalingMessageToByteMessage(Message message) throws TransmissionException {

            return convertMessage(message, createOutgoingMessagePipes());
        }

        Message convertMessage(Message message, MessagePipe[] convertingPipes)
                throws TransmissionException {

            for(MessagePipe messagePipe: convertingPipes){
                message = messagePipe.transmit(message);
            }
            return message;
        }

        TestableRoutingTable getTestableRoutingTable() {
            return (TestableRoutingTable) routingTable;
        }

        MessagePipeline getIncomingMessagePipeline() {
            return incomingMessagePipeline;
        }
    }

    class TestableRoutingTable extends RoutingTable{

        public TestableRoutingTable(MessagePipeline outgoingMessagePipeline) {
            super(outgoingMessagePipeline);
        }
        void addClientRecord(ClientId clientId, ClientRecord clientRecord){

            clientRecords.put(clientId, clientRecord);
        }

        boolean containsClientRecord(ClientId clientId) {
            return clientRecords.containsKey(clientId);
        }
    }

    @Test
    public void test_when_routing_table_does_not_contain_client_with_receiver_id_then_on_ExchangeSDPRequest_routing_table_returns_ErrorResponse_with_RECEIVER_RECORD_NOT_EXISTS_code()
            throws TransmissionException {

        //given
        final AtomicReference<Message> outgoingMessageReference = new AtomicReference<>();
        TestableSignalingServer signalingServer = new TestableSignalingServer(
                new MessagePipelineEndpoint() {
            @Override
            public void onTransmittedMessage(Message message_) {

                outgoingMessageReference.set(message_);
            }
        });

        TestableRoutingTable routingTable = signalingServer.getTestableRoutingTable();
        MessagePipeline incomingMessagePipeline = signalingServer.getIncomingMessagePipeline();

        //when
        assertTrue(routingTable.containsClientRecord(receiverId)==false);

        Message incomingMessage = signalingServer.convertSignalingMessageToByteMessage(createExchangeSDPRequestMessage());
        incomingMessagePipeline.transmit(incomingMessage);

        final Message outgoingMessage = signalingServer.convertByteMessageToSignalingMessage(outgoingMessageReference.get());
        assertNotNull(outgoingMessage);

        //then
        SignalingMessage signalingMessage = (SignalingMessage) outgoingMessage.getData();
        assertTrue(ErrorResponse.class.isInstance(signalingMessage));

        ErrorResponse errorResponse = (ErrorResponse) signalingMessage;
        assertEquals(ErrorCode.RECEIVER_RECORD_NOT_EXISTS, errorResponse.getErrorCode());
    }

    @Test
    public void test_when_routing_table_contains_client_with_receiver_id_then_on_ExchangeSDPRequest_routing_table_transmits_ExchangeSDPRequest_with_receiver_address()
            throws TransmissionException {

        //given
        final AtomicReference<Message> outgoingMessageReference = new AtomicReference<>();
        TestableSignalingServer signalingServer = new TestableSignalingServer(
                new MessagePipelineEndpoint() {
                    @Override
                    public void onTransmittedMessage(Message message_) {

                        outgoingMessageReference.set(message_);
                    }
                });
        TestableRoutingTable routingTable = signalingServer.getTestableRoutingTable();
        MessagePipeline incomingMessagePipeline = signalingServer.getIncomingMessagePipeline();

        //when
        routingTable.addClientRecord(receiverId, new ClientRecord(receiverSocketAddress));

        Message incomingMessage = signalingServer.convertSignalingMessageToByteMessage(createExchangeSDPRequestMessage());
        incomingMessagePipeline.transmit(incomingMessage);

        final Message outgoingMessage = signalingServer.convertByteMessageToSignalingMessage(outgoingMessageReference.get());
        assertNotNull(outgoingMessage);

        //then
        SignalingMessage signalingMessage = (SignalingMessage) outgoingMessage.getData();
        assertTrue(ExchangeSDPRequest.class.isInstance(signalingMessage));

        final InetSocketAddress outgoingMessageAddress = (InetSocketAddress) outgoingMessage.getAddress();
        assertEquals(receiverSocketAddress, outgoingMessageAddress);
    }

    private Message createExchangeSDPRequestMessage() throws TransmissionException {

        String sessionDescriptionAsText =
                "v=0\r\n"+
                "o=- 15867114619150279987 15867114619150279987 IN IP4 zebul-NV78\r\n"+
                "s=Unnamed\r\n"+
                "i=N/A\r\n"+
                "c=IN IP4 0.0.0.0\r\n"+
                "t=0 0\r\n"+
                "a=tool:vlc 2.2.2\r\n"+
                "a=recvonly\r\n"+
                "a=type:broadcast\r\n"+
                "a=charset:UTF-8\r\n"+
                "a=control:rtsp://192.168.1.106:8554/s1\r\n"+

                "m=audio 0 RTP/AVP 96\r\n"+
                "b=RR:0\r\n"+
                "a=rtpmap:96 mpeg4-generic/44100/2\r\n"+
                "a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;\r\n"+
                "a=control:rtsp://192.168.1.106:8554/s1/trackID=4\r\n"+

                "m=video 0 RTP/AVP 96\r\n"+
                "b=RR:0\r\n"+
                "a=rtpmap:96 H264/90000\r\n"+
                "a=fmtp:96 packetization-mode=1;profile-level-id=42e00d;sprop-parameter-sets=J0LgDakYKD9gDUGAQa23oC8B6XvfAQ==,KM4JiA==;\r\n"+
                "a=control:rtsp://192.168.1.106:8554/s1/trackID=5\r\n";

        ExchangeSDPRequest exchangeSDPRequest =
                new ExchangeSDPRequest(senderId, receiverId, sessionDescriptionAsText);

        return new Message(senderSocketAddress, exchangeSDPRequest);
    }
}
