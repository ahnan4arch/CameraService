package com.example.zebul.cameraservice.av_streaming.av_packet;

/**
 * Created by zebul on 11/15/16.
 */

public interface VideoDataPackConsumer {

    void consumeVideoDataPack(AVPackets videoDataPack)
            throws VideoDataPackConsumptionException;
}
