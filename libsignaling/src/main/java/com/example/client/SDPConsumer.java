package com.example.client;

/**
 * Created by zebul on 3/14/17.
 */

public interface SDPConsumer {

    void consumeSDP(String sdp);
    void onError(Exception exc);
}
