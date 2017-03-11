package com.example.udp;

import com.example.signaling_message.Message;

/**
 * Created by zebul on 9/18/16.
 */
public interface SocketMessageReceptionListener {

    public void onSocketMessageReceived(Message message_);
}
