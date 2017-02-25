package com.example.zebul.cameraservice.av_protocols.rtp;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timestamp timestamp = (Timestamp) o;

        return timestampInMillis == timestamp.timestampInMillis;

    }

    @Override
    public int hashCode() {
        return (int) (timestampInMillis ^ (timestampInMillis >>> 32));
    }
}
