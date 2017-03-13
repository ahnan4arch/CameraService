package com.example.signaling_message;

/**
 * Created by zebul on 3/10/17.
 */

public class ExchangeSDPRequest extends SignalingMessage {

    private ClientId receiverId;
    private String ssessionDescription;

    public ExchangeSDPRequest(
            ClientId senderId,
            ClientId receiverId,
            String ssessionDescription) {

        this(MessageType.EXCHANGE_SDP_REQUEST, senderId, receiverId, ssessionDescription);
    }

    protected ExchangeSDPRequest(
            MessageType messageType,
            ClientId senderId,
            ClientId receiverId,
            String ssessionDescription) {

        super(messageType, senderId);
        this.receiverId = receiverId;
        this.ssessionDescription = ssessionDescription;
    }

    public String getSsessionDescription() {
        return ssessionDescription;
    }

    public ClientId getReceiverId() {
        return receiverId;
    }

    public ErrorResponse createErrorResponse(ErrorCode errorCode) {
        return new ErrorResponse(senderId, receiverId, errorCode);
    }
}
