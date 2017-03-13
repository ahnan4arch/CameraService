package com.example.signaling_message;

/**
 * Created by zebul on 9/20/16.
 */
public class TransmissionException extends Exception{

    protected MessagePipe messagePipe;
    public TransmissionException(String message_){

        super(message_);
    }

    public TransmissionException(Exception exc_) {

        super(exc_);
    }

    public void setMessagePipe(MessagePipe messagePipe_){

        messagePipe = messagePipe_;
    }

    public MessagePipe getMessagePipe(){

        return messagePipe;
    }
}
