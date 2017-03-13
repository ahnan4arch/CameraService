package com.example.signaling_message;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 9/18/16.
 */
public class MessagePipeline {

    private List<MessagePipe> messagePipeline = new LinkedList<>();
    private MessagePipelineEndpoint messageEndpoint;

    public void addMessagePipe(MessagePipe messagePipe_){

        messagePipeline.add(messagePipe_);
    }

    public void setMessageEndpoint(MessagePipelineEndpoint messageEndpoint_) {

        messageEndpoint = messageEndpoint_;
    }

    public void transmit(Message message_) {

        for(MessagePipe messagePipe: messagePipeline){

            try{

                message_ = messagePipe.transmit(message_);
            }
            catch(TransmissionException exc_){

                exc_.setMessagePipe(messagePipe);
                return;
            }
        }
        messageEndpoint.onTransmittedMessage(message_);
    }
}
