package com.example.zebul.cameraservice.av_streaming.rtp.basic;

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

}
