package com.example.zebul.cameraservice.ice4j.session;

import com.example.message.Message;
import com.example.signaling_message.ClientId;

/**
 * Created by zebul on 3/13/17.
 */

public class SignalingSessionState {

    void update(SignalingSession signalingSession){}

    void connectWith(SignalingSession signalingSession,
                     ClientId remoteClientId){}

    void onSignalingMessage(SignalingSession signalingSession,
                            Message message) {}
}
