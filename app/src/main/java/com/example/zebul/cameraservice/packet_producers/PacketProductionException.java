package com.example.zebul.cameraservice.packet_producers;

/**
 * Created by zebul on 11/16/16.
 */

public class PacketProductionException extends Exception{

    public PacketProductionException(Throwable cause){

        super(cause);
    }

    public PacketProductionException(String message){

        super(message);
    }
}
