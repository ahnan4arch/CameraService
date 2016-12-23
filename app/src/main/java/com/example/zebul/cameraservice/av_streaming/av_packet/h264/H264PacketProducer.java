package com.example.zebul.cameraservice.av_streaming.av_packet.h264;

import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionException;

/**
 * Created by zebul on 11/15/16.
 */

public interface H264PacketProducer {

    H264Packets produceH264Packets()
            throws PacketProductionException;
}
