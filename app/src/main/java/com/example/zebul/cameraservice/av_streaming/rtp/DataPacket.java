package com.example.zebul.cameraservice.av_streaming.rtp;

import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.basic.DataUnit;

/**
 * Created by zebul on 11/15/16.
 */

public abstract class DataPacket<DATA_UNIT extends DataUnit> {

    protected DATA_UNIT dataUnit;
    protected Timestamp timestamp;

    public DataPacket(DATA_UNIT dataUnit, Timestamp timestamp){

        this.dataUnit = dataUnit;
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
