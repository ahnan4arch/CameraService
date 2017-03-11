package com.example.message;

/**
 * Created by zebul on 9/18/16.
 */

public interface MessagePipe {

    Message transmit(Message message_)throws TransmissionException;
}
