package com.example.signaling_message;

import java.io.Serializable;

/**
 * Created by zebul on 3/9/17.
 */

public abstract class SignalingMessage implements Serializable {

    protected MessageType messageType;
    protected ClientId senderId;

    public SignalingMessage(
            MessageType messageType, ClientId senderId){

        this.messageType = messageType;
        this.senderId = senderId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public ClientId getSenderId() {
        return senderId;
    }
}
