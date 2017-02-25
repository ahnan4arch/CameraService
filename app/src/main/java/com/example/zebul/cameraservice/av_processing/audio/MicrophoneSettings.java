package com.example.zebul.cameraservice.av_processing.audio;

/**
 * Created by zebul on 12/31/16.
 */

public class MicrophoneSettings {

    private AudioSettings audioSettings;

    public MicrophoneSettings(AudioSettings audioSettings) {
        this.audioSettings = audioSettings;
    }

    public AudioSettings getAudioSettings() {
        return audioSettings;
    }
}
