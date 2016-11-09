package com.example.zebul.cameraservice.message;

import com.example.zebul.cameraservice.message.Message;

/**
 * Created by zebul on 9/18/16.
 */

public interface MessagePipe {

    Message transmit(Message message_)throws TransmissionException;
}
