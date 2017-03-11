package com.example;

import com.example.message_pipe.incoming.IncomingMessageDecompressingPipe;
import com.example.message_pipe.incoming.IncomingMessageDeserializingPipe;
import com.example.message_pipe.outgoing.OutgoingMessageCompresingPipe;
import com.example.message_pipe.outgoing.OutgoingMessageSerializingPipe;
import com.example.signaling_message.ClientId;
import com.example.signaling_message.ExchangeSDPRequest;
import com.example.signaling_message.ExchangeSDPResponse;
import com.example.signaling_message.Message;
import com.example.signaling_message.MessagePipeline;
import com.example.signaling_message.MessagePipelineEndpoint;
import com.example.signaling_message.TransmissionException;

import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by zebul on 3/10/17.
 */

public class RoutingTableTest {

    @Test
    public void test2() throws TransmissionException {

        MessagePipeline incomingMessagePipeline = new MessagePipeline();
        MessagePipeline outgoingMessagePipeline = new MessagePipeline();

        incomingMessagePipeline.addMessagePipe(new IncomingMessageDecompressingPipe());
        incomingMessagePipeline.addMessagePipe(new IncomingMessageDeserializingPipe());
        RoutingTable routingTable = new RoutingTable(outgoingMessagePipeline);
        incomingMessagePipeline.setMessageEndpoint(routingTable);

        outgoingMessagePipeline.addMessagePipe(new OutgoingMessageSerializingPipe());
        outgoingMessagePipeline.addMessagePipe(new OutgoingMessageCompresingPipe());
        final AtomicReference<Message> outgoingMessageReference = new AtomicReference<>();
        outgoingMessagePipeline.setMessageEndpoint(new MessagePipelineEndpoint() {
            @Override
            public void onTransmittedMessage(Message message_) {

                outgoingMessageReference.set(message_);
            }
        });

        Message incomingMessage = createExchangeSDPRequestMessage();
        incomingMessagePipeline.transmit(incomingMessage);

        final Message outgoingMessage = outgoingMessageReference.get();
        assertNotNull(outgoingMessage);
        assertEquals(2, 2);
    }

    private Message createExchangeSDPRequestMessage() throws TransmissionException {

        ClientId senderId = new ClientId("foo");
        ClientId receiverId = new ClientId("bar");
        String ssessionDescription = "Session ...";
        ExchangeSDPRequest exchangeSDPRequest =
                new ExchangeSDPRequest(senderId, receiverId, ssessionDescription);

        Message messageToSerialize =
                new Message(new InetSocketAddress("127.0.0.1", 5678), exchangeSDPRequest);

        Message messageToCompress =
                new OutgoingMessageSerializingPipe().transmit(messageToSerialize);

        return new OutgoingMessageCompresingPipe().transmit(messageToCompress);
    }
}
