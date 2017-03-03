package com.example.zebul.cameraservice.av_processing.audio;

import com.example.zebul.cameraservice.av_protocols.rtp.aac.AACPacket;

/**
 * Created by zebul on 12/23/16.
 */

public interface AACPacketConsumer {

    void consumeAACPacket(AACPacket aacPacket);
}
