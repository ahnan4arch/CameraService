package com.example.signaling_message;

/**
 * Created by zebul on 9/18/16.
 */
public interface MessagePipelineEndpoint {

    public void onTransmittedMessage(Message message_);
}
