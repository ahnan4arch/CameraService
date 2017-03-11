package com.example.message;

/**
 * Created by zebul on 3/10/17.
 */

public class KeepAliveRequest extends SignalingMessage{

    public KeepAliveRequest(ClientAddress senderAddress) {
        super(MessageType.KEEP_ALIVE_REQUEST, senderAddress);
    }
}
