package com.example.signaling_message;

/**
 * Created by zebul on 3/10/17.
 */

public class ExchangeSDPResponse extends ExchangeSDPRequest{

    public ExchangeSDPResponse(
            ClientId senderAddress,
            ClientId receiverAddress,
            String ssessionDescription) {

        super(MessageType.EXCHANGE_SDP_RESPONSE, senderAddress, receiverAddress, ssessionDescription);
    }
}
