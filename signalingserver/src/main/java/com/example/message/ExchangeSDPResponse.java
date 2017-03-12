package com.example.message;

/**
 * Created by zebul on 3/10/17.
 */

public class ExchangeSDPResponse extends ExchangeSDPRequest{

    public ExchangeSDPResponse(
            ClientAddress senderAddress,
            ClientAddress receiverAddress,
            String ssessionDescription) {

        super(MessageType.EXCHANGE_SDP_RESPONSE, senderAddress, receiverAddress, ssessionDescription);
    }
}
