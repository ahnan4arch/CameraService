package com.example.zebul.cameraservice.av_processing.audio;

/**
 * Created by zebul on 12/23/16.
 */

public class AudioSettings {

    public static final AudioSettings DEFAULT = new AudioSettings(8000, 32000);

    private int samplingRate;
    private int bitRate;

    public AudioSettings(int samplingRate, int bitRate){

        this.samplingRate = samplingRate;
        this.bitRate = bitRate;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }
}
