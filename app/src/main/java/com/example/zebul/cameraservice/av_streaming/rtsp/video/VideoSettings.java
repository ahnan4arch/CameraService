package com.example.zebul.cameraservice.av_streaming.rtsp.video;

/**
 * Created by zebul on 12/23/16.
 */

public class VideoSettings {

    private Resolution resolution;
    private int bitRate;

    public VideoSettings(Resolution resolution, int bitRate){

        this.resolution = resolution;
        this.bitRate = bitRate;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public int getBitRate() {
        return bitRate;
    }
}
