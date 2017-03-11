package com.example;

import com.example.message.ExchangeSDPRequest;
import com.example.message.ExchangeSDPResponse;
import com.example.message.KeepAliveRequest;

/**
 * Created by zebul on 3/10/17.
 */

public class BleBle implements SignalingMessageProcessor {

    /*
    protected ConcurrentHashMap<EndpointData, EndpointRecord> endpointRecords =
            new ConcurrentHashMap<EndpointData, EndpointRecord>();
    */

    @Override
    public void onKeepAliveRequest(KeepAliveRequest keepAliveRequest) {

    }

    @Override
    public void onExchangeSDPRequest(ExchangeSDPRequest exchangeSDPRequest) {

    }

    @Override
    public void onExchangeSDPResponse(ExchangeSDPResponse exchangeSDPResponse) {

    }
}
