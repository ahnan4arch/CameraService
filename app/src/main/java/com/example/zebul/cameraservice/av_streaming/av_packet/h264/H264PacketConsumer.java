package com.example.zebul.cameraservice.av_streaming.av_packet.h264;

import com.example.zebul.cameraservice.av_streaming.av_packet.PacketConsumptionException;

/**
 * Created by zebul on 11/15/16.
 */

public interface H264PacketConsumer {

    void consumeVideoDataPack(H264Packets h264Packets)
            throws PacketConsumptionException;
}
