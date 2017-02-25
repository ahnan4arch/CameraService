package com.example.zebul.cameraservice.av_processing.video;

import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.av_processing.PacketProcessingException;

/**
 * Created by zebul on 11/15/16.
 */

public interface H264PacketProducer {

    H264Packets produceH264Packets()
            throws PacketProcessingException;
}
