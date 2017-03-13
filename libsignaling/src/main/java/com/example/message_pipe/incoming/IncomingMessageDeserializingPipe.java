package com.example.message_pipe.incoming;

import com.example.message.Message;
import com.example.message.MessagePipe;
import com.example.message.TransmissionException;
import com.example.signaling_message.SignalingMessage;
import com.example.utils.GenericSerializer;

/**
 * Created by zebul on 3/11/17.
 */

public class IncomingMessageDeserializingPipe implements MessagePipe {

    @Override
    public Message transmit(Message message_) throws TransmissionException {

        try {
            final byte[] data = (byte[]) message_.getData();
            final SignalingMessage signalingMessage =
                    GenericSerializer.deserialize(data, SignalingMessage.class);
            return new Message(message_.getAddress(), signalingMessage);
        }
        catch(Exception exc_){

            throw new TransmissionException(exc_);
        }
    }
}
