package com.example.zebul.cameraservice.av_processing.video;

import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packet;

/**
 * Created by zebul on 12/31/16.
 */

public interface H264PacketConsumer {

    void consumeH264Packet(H264Packet h264Packet);
}
