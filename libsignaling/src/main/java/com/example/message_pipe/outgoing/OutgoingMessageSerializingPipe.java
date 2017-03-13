package com.example.message_pipe.outgoing;

import com.example.message.Message;
import com.example.message.MessagePipe;
import com.example.message.TransmissionException;
import com.example.signaling_message.SignalingMessage;
import com.example.utils.GenericSerializer;

import java.io.IOException;

/**
 * Created by zebul on 3/11/17.
 */

public class OutgoingMessageSerializingPipe implements MessagePipe {

    @Override
    public Message transmit(Message message_) throws TransmissionException {

        try {
            final SignalingMessage signalingMessage = (SignalingMessage) message_.getData();
            final byte[] data = GenericSerializer.serialize(signalingMessage);
            return new Message(message_.getAddress(), data);
        } catch (IOException exc) {
            throw new TransmissionException(exc);
        }
    }
}
