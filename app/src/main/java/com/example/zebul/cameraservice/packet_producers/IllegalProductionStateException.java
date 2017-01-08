package com.example.zebul.cameraservice.packet_producers;

/**
 * Created by zebul on 1/7/17.
 */

public class IllegalProductionStateException extends PacketProductionException{

    public IllegalProductionStateException(String message) {
        super(message);
    }
}
