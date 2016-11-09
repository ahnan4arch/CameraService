package com.example.zebul.cameraservice.message.pipes;

import com.example.zebul.cameraservice.message.TransmissionException;

/**
 * Created by zebul on 9/20/16.
 */
public class UnknownRequestException extends TransmissionException{

    public UnknownRequestException(String requestName_) {
        super("Unknown request name: "+requestName_);
    }
}
