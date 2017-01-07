package com.example.zebul.cameraservice.av_streaming.rtp.aac;

import com.example.zebul.cameraservice.av_streaming.rtp.DataPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AccessUnit;

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