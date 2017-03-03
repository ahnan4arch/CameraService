package com.example.zebul.cameraservice.av_protocols.rtp.aac;

import com.example.zebul.cameraservice.av_protocols.rtp.basic.DataPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;

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