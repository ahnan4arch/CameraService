package com.example.zebul.cameraservice.video_streaming.video_data;

/**
 * Created by zebul on 11/15/16.
 */

public interface AVPacketProducer {

    AVPackets produceAVPackets()
            throws AVPacketProductionException;
}
