package com.example.zebul.cameraservice.av_processing.video.camera;

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
