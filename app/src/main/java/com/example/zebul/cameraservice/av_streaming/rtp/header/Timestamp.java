package com.example.zebul.cameraservice.av_streaming.rtp.header;

/**
 * Created by zebul on 11/15/16.
 */

public class Timestamp {

    private long timestampInMillis;

    public Timestamp(long timestampInMillis){

        this.timestampInMillis = timestampInMillis;
    }

    public long getTimestampInMillis(){

        return timestampInMillis;
    }
}
