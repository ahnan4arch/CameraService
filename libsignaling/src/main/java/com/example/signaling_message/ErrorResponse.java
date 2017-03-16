package com.example.signaling_message;

/**
 * Created by zebul on 3/11/17.
 */

public class ErrorResponse extends SignalingMessage{

    private final ClientId receiverId;
    private ErrorCode errorCode;

    public ErrorResponse(ClientId senderId, ClientId receiverId, ErrorCode errorCode) {
        super(MessageType.ERROR_RESPONSE, senderId);
        this.receiverId = receiverId;
        this.errorCode = errorCode;
    }

    public ClientId getReceiverId() {
        return receiverId;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
