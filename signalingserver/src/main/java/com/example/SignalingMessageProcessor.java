package com.example;

import com.example.message.ExchangeSDPRequest;
import com.example.message.ExchangeSDPResponse;
import com.example.message.KeepAliveRequest;

/**
 * Created by zebul on 3/10/17.
 */

public interface SignalingMessageProcessor {

    void onKeepAliveRequest(KeepAliveRequest keepAliveRequest);
    void onExchangeSDPRequest(ExchangeSDPRequest exchangeSDPRequest);
    void onExchangeSDPResponse(ExchangeSDPResponse exchangeSDPResponse);
}
