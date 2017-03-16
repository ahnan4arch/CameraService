package com.example.client;

import com.example.message.Message;
import com.example.signaling_message.ClientId;

/**
 * Created by zebul on 3/13/17.
 */

public class SignalingSessionState {

    void update(SignalingSession signalingSession){}

    void beginExchangeSDPWithRemoteClient(SignalingSession signalingSession,
                                          SDPProducer sdpProducer,
                                          SDPConsumer sdpConsumer,
                                          ClientId remoteClientId){}

    void onSignalingMessage(SignalingSession signalingSession,
                            Message message) {}
}
