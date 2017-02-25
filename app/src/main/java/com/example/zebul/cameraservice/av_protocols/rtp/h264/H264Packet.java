package com.example.zebul.cameraservice.av_protocols.rtp.h264;

import com.example.zebul.cameraservice.av_protocols.rtp.basic.DataPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;

/**
 * Created by zebul on 12/20/16.
 */

public class H264Packet extends DataPacket<NALUnit> {

    public H264Packet(NALUnit nalUnit, Timestamp timestamp) {
        super(nalUnit, timestamp);
    }

    public NALUnit getNALUnit() {
        return dataUnit;
    }
}
