package com.example.client;

import com.example.message.Message;

/**
 * Created by zebul on 3/13/17.
 */

public interface SocketMessageSender {

    void sendMessage(Message message);
}
