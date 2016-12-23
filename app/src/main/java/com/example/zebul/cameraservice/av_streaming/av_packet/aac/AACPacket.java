package com.example.zebul.cameraservice.av_streaming.av_packet.aac;

import com.example.zebul.cameraservice.av_streaming.av_packet.basic.DataPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AccessUnit;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;

/**
 * Created by zebul on 12/21/16.
 */

public class AACPacket extends DataPacket<AccessUnit> {

    public AACPacket(AccessUnit accessUnit, Timestamp timestamp) {
        super(accessUnit, timestamp);
    }

    public AccessUnit getAccessUnit() {
        return dataUnit;
    }
}