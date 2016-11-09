package com.example.zebul.cameraservice.communication.udp;

import com.example.zebul.cameraservice.message.Message;

/**
 * Created by zebul on 9/18/16.
 */
public interface SocketMessageReceptionListener {

    public void onSocketMessageReceived(Message message_);
}
