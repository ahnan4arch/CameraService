package com.example.zebul.cameraservice.video_streaming.rtp;

import com.example.zebul.cameraservice.video_streaming.rtp.header.Timestamp;

/**
 * Created by zebul on 10/31/16.
 */

public class Clock {

    public static final long DEFAULT_CLOCK_RATE = 90000;
    private long startTimestamp = System.currentTimeMillis();;

    public Clock(){
    }

    public static long computeTimestamp(long presentationTimeInMilliseconds){

        return computeTimestamp(DEFAULT_CLOCK_RATE, presentationTimeInMilliseconds);
    }

    public static long computeTimestamp(long clockRate, long presentationTimeInMilliseconds){

        return (long)((presentationTimeInMilliseconds/1000.0f)*DEFAULT_CLOCK_RATE);
    }

    public void restart(){

        startTimestamp = System.currentTimeMillis();
    }

    public Timestamp getTimestamp(){

        long nowTimestamp = System.currentTimeMillis();
        long timestampInMillis = computeTimestamp(nowTimestamp-startTimestamp);
        return new Timestamp(timestampInMillis);
    }
}
