package com.example.zebul.cameraservice.av_processing;

/**
 * Created by zebul on 12/23/16.
 */

public interface PacketProcessingExceptionListener {

    void onPacketProductionException(PacketProcessingException exc);
}
