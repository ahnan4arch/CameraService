package com.example.signaling_message;

/**
 * Created by zebul on 3/10/17.
 */

public class KeepAliveRequest extends SignalingMessage{

    public KeepAliveRequest(ClientId senderId) {
        super(MessageType.KEEP_ALIVE_REQUEST, senderId);
    }
}
