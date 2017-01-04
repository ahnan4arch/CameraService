package com.example.zebul.cameraservice.av_streaming.rtsp.session;

import com.example.zebul.cameraservice.av_streaming.rtsp.audio.AudioSettings;

/**
 * Created by zebul on 12/27/16.
 */

public class SessionAudioInfo {

    private int audioTrackId;
    private AudioSettings audioSettings;

    public SessionAudioInfo(int audioTrackId, AudioSettings audioSettings){

        this.audioTrackId = audioTrackId;
        this.audioSettings = audioSettings;
    }

    public int getAudioTrackId() {
        return audioTrackId;
    }

    public AudioSettings getAudioSettings() {
        return audioSettings;
    }
}
