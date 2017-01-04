package com.example.zebul.cameraservice.av_streaming.av_packet.h264;

import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacket;

/**
 * Created by zebul on 12/31/16.
 */

public interface H264PacketListener {

    void onH264Packet(H264Packet h264Packet);
}
