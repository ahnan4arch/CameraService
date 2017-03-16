package com.example.udp;

import com.example.message.Message;

/**
 * Created by zebul on 9/18/16.
 */
public interface SocketMessageReceptionListener {

    void onSocketMessageReceived(Message message);
}
