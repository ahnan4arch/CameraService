package com.example.client;

import com.example.message.Message;
import com.example.message.TransmissionException;
import com.example.server.SignalingServerTest;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.signaling_message.KeepAliveRequest;
import com.example.udp.SocketMessageReceptionListener;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by zebul on 3/14/17.
 */

public class SignalingSessionTest {

    class SocketMessageReceptionListenerFake implements SocketMessageReceptionListener{

        private Message message;
        @Override
        public void onSocketMessageReceived(Message message) {
            this.message = message;
        }

        public Message getOutgoingMessage() {
            return message;
        }
    }

    class SDPProducerFake implements SDPProducer{

        @Override
        public String produceSDP() {
            return "fakeSDP";
        }
    }

    class SDPConsumerFake implements SDPConsumer{

        private String sdp;

        public String getSdp() {
            return sdp;
        }

        @Override
        public void consumeSDP(String sdp) {

            this.sdp = sdp;
        }

        @Override
        public void onError(Exception exc) {

        }
    }

    private SocketMessageReceptionListenerFake socketMessageReceptionListenerFake;
    private SDPProducerFake sdpProducerFake;
    private SDPConsumerFake sdpConsumerFake;

    @Before
    public void setUp() throws Exception {

        socketMessageReceptionListenerFake = new SocketMessageReceptionListenerFake();
        sdpProducerFake = new SDPProducerFake();
        sdpConsumerFake = new SDPConsumerFake();
    }

    @Test
    public void test_when_session_is_created_then_on_update_ssession_transmits_KeepAliveRequest()
            throws TransmissionException {

        //given
        SignalingSession signalingSession = new SignalingSession(new ClientId("foo"),
                socketMessageReceptionListenerFake);

        //when
        signalingSession.updateState();

        //then
        final Message outgoingSignalingMessage =
                SignalingServerTest.TestableSignalingServer.convertByteMessageToSignalingMessage(
                        socketMessageReceptionListenerFake.getOutgoingMessage());

        final Object data = outgoingSignalingMessage.getData();
        assertTrue(KeepAliveRequest.class.isInstance(data));
    }

    @Test
    public void test_when_beginExchangeSDP_is_issued_then_on_update_session_transmits_ExchangeSDPRequest()
            throws TransmissionException {

        SignalingSession signalingSession = new SignalingSession(new ClientId("foo"),
                socketMessageReceptionListenerFake);

        //when
        ClientId remoteClientId = new ClientId("bar");
        signalingSession.beginExchangeSDPWithRemoteClient(sdpProducerFake, sdpConsumerFake, remoteClientId);
        signalingSession.updateState();

        //then
        final Message outgoingSignalingMessage =
                SignalingServerTest.TestableSignalingServer.convertByteMessageToSignalingMessage(
                        socketMessageReceptionListenerFake.getOutgoingMessage());

        final Object data = outgoingSignalingMessage.getData();
        assertTrue(ExchangeSDPRequest.class.isInstance(data));
    }

    @Test
    public void test_when_beginExchangeSDP_is_issued_then_on_reception_of_ExchangeSDPResponse_sdpConsumer_contains_SDP()
            throws TransmissionException {

        ClientId localClientId = new ClientId("foo");
        SignalingSession signalingSession = new SignalingSession(localClientId,
                socketMessageReceptionListenerFake);

        ClientId remoteClientId = new ClientId("bar");
        signalingSession.beginExchangeSDPWithRemoteClient(
                sdpProducerFake, sdpConsumerFake, remoteClientId);

        ExchangeSDPResponse exchangeSDPResponse = new ExchangeSDPResponse(
                remoteClientId, localClientId, "some other SDP");

        final Message incomingSignalingMessage = new Message("address", exchangeSDPResponse);
        final Message incomingByteMessage =
                SignalingServerTest.TestableSignalingServer.convertSignalingMessageToByteMessage(
                        incomingSignalingMessage);

        signalingSession.transmitViaIncomingMessagePipeline(incomingByteMessage);
        assertNotNull(sdpConsumerFake.getSdp());
    }


}
