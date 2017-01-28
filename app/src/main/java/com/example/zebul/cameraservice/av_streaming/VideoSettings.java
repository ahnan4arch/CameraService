package com.example.zebul.cameraservice.av_streaming;

/**
 * Created by zebul on 12/23/16.
 */

public class VideoSettings {

    public static final int DEFAULT_FRAME_RATE = 30;// 30fps

    private Resolution resolution;
    private int bitRate;
    private int frameRate = DEFAULT_FRAME_RATE;

    public VideoSettings(Resolution resolution, int bitRate, int frameRate){

        this.resolution = resolution;
        this.bitRate = bitRate;
        this.frameRate = frameRate;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public int getBitRate() {
        return bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }
}
