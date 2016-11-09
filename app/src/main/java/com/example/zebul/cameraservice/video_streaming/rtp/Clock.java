package com.example.zebul.cameraservice.video_streaming.rtp;

/**
 * Created by zebul on 10/31/16.
 */

public class Clock {

    public static final long DEFAULT_CLOCK_RATE = 90000;
    public long startTimestamp = 0;

    public static long computeTimestamp(long presentationTimeInMilliseconds){

        return computeTimestamp(DEFAULT_CLOCK_RATE, presentationTimeInMilliseconds);
    }

    public static long computeTimestamp(long clockRate, long presentationTimeInMilliseconds){

        return (long)((presentationTimeInMilliseconds/1000.0f)*DEFAULT_CLOCK_RATE);
    }

    public void restart(){

        startTimestamp = System.currentTimeMillis();
    }

    public long getTimestamp(){

        long nowTimestamp = System.currentTimeMillis();
        return computeTimestamp(nowTimestamp-startTimestamp);
    }
}
