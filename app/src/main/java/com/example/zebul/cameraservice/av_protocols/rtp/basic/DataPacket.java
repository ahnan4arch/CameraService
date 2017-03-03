package com.example.zebul.cameraservice.av_protocols.rtp.basic;

import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataPacket<?> that = (DataPacket<?>) o;

        if (!dataUnit.equals(that.dataUnit)) return false;
        return timestamp.equals(that.timestamp);

    }
}
