package com.example.message;

/**
 * Created by zebul on 3/10/17.
 */

public class ExchangeSDPRequest extends SignalingMessage {

    private ClientAddress receiverAddress;
    private String ssessionDescription;

    public ExchangeSDPRequest(
            ClientAddress senderAddress,
            ClientAddress receiverAddress,
            String ssessionDescription) {

        this(MessageType.EXCHANGE_SDP_REQUEST, senderAddress, receiverAddress, ssessionDescription);
    }

    protected ExchangeSDPRequest(
            MessageType messageType,
            ClientAddress senderAddress,
            ClientAddress receiverAddress,
            String ssessionDescription) {

        super(messageType, senderAddress);
        this.receiverAddress = receiverAddress;
        this.ssessionDescription = ssessionDescription;
    }

    public String getSsessionDescription() {
        return ssessionDescription;
    }

    public ClientAddress getReceiverAddress() {
        return receiverAddress;
    }
}
