package com.example.zebul.cameraservice.packet_producers.video;

import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.packet_producers.PacketConsumptionException;

/**
 * Created by zebul on 11/15/16.
 */

public interface H264PacketConsumer {

    void consumeVideoDataPack(H264Packets h264Packets)
            throws PacketConsumptionException;
}
