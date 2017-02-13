package com.example.zebul.cameraservice.av_streaming.rtp.basic;

import java.util.Arrays;

/**
 * Created by zebul on 12/20/16.
 */

public abstract class DataUnit {

    protected byte[] data;

    protected DataUnit(byte[] data){

        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataUnit dataUnit = (DataUnit) o;

        return Arrays.equals(data, dataUnit.data);
    }

}
