package com.example.zebul.cameraservice.packet_producers.video.camera;

import com.example.zebul.cameraservice.av_streaming.VideoSettings;

/**
 * Created by zebul on 12/31/16.
 */

public class CameraSettings {

    private VideoSettings videoSettings;

    public CameraSettings(VideoSettings videoSettings){

        this.videoSettings = videoSettings;
    }

    public VideoSettings getVideoSettings() {
        return videoSettings;
    }
}
