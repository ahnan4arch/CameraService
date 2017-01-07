package com.example.zebul.cameraservice.packet_producers.audio;

import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacket;

/**
 * Created by zebul on 12/23/16.
 */

public interface AACPacketListener {

    void onAACPacket(AACPacket aacPacket);
}
