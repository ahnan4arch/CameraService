package com.example.message;

import java.io.Serializable;

/**
 * Created by zebul on 3/9/17.
 */

public abstract class SignalingMessage implements Serializable {

    protected MessageType messageType;
    protected ClientAddress senderAddress;

    public SignalingMessage(
            MessageType messageType, ClientAddress senderAddress){

        this.messageType = messageType;
        this.senderAddress = senderAddress;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public ClientAddress getSenderAddress() {
        return senderAddress;
    }
}
