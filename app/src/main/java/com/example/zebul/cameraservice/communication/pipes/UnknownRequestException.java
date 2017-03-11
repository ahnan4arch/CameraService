package com.example.zebul.cameraservice.communication.pipes;

import com.example.message.TransmissionException;

/**
 * Created by zebul on 9/20/16.
 */
public class UnknownRequestException extends TransmissionException{

    public UnknownRequestException(String requestName_) {
        super("Unknown request name: "+requestName_);
    }
}
