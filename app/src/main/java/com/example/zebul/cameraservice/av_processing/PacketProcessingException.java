package com.example.zebul.cameraservice.av_processing;

/**
 * Created by zebul on 11/16/16.
 */

public class PacketProcessingException extends Exception{

    public PacketProcessingException(Throwable cause){

        super(cause);
    }

    public PacketProcessingException(String message){

        super(message);
    }
}
