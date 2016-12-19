package com.example.zebul.cameraservice.av_streaming.av_packet;

import com.example.zebul.cameraservice.av_streaming.rtp.header.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnit;

/**
 * Created by zebul on 11/15/16.
 */

public class AVPacket {

    private NALUnit nalUnit;
    private Timestamp timestamp;

    public AVPacket(NALUnit nalUnit, Timestamp timestamp){

        this.nalUnit = nalUnit;
        this.timestamp = timestamp;
    }

    public NALUnit getNALUnit() {
        return nalUnit;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
