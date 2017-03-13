package com.example;

import com.example.message.Message;
import com.example.message.MessagePipeline;
import com.example.signaling_message.SignalingMessage;
import com.example.udp.SocketMessageReceptionListener;
import com.example.utils.GenericSerializer;

import java.net.InetSocketAddress;

/**
 * Created by zebul on 3/11/17.
 */

public class IncomingMessageReceiver implements SocketMessageReceptionListener {

    private MessagePipeline incomingMessagePipeline;

    public IncomingMessageReceiver(MessagePipeline incomingMessagePipeline){

        this.incomingMessagePipeline = incomingMessagePipeline;
    }

    @Override
    public void onSocketMessageReceived(Message message_) {

        try {
            incomingMessagePipeline.transmit(message_);
        }
        catch(Exception exc_){

            exc_.printStackTrace();
        }
    }
}
