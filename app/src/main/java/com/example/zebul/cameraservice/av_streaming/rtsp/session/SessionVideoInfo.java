package com.example.zebul.cameraservice.av_streaming.rtsp.session;

import com.example.zebul.cameraservice.av_streaming.rtsp.video.VideoSettings;

/**
 * Created by zebul on 12/27/16.
 */

public class SessionVideoInfo {

    private int videoTrackId;
    private VideoSettings videoSettings;

    public SessionVideoInfo(int videoTrackId, VideoSettings videoSettings){

        this.videoTrackId = videoTrackId;
        this.videoSettings = videoSettings;
    }

    public int getVideoTrackId() {
        return videoTrackId;
    }

    public VideoSettings getVideoSettings() {
        return videoSettings;
    }
}
