package com.example.zebul.cameraservice.av_streaming.rtsp.message.body;

import com.example.zebul.cameraservice.av_streaming.sdp.SessionDescription;

public class Body {

	private SessionDescription sessionDescription;

	public Body(SessionDescription sessionDescription){
	
		this.sessionDescription = sessionDescription;
	}

	public SessionDescription getSessionDescription() {
		return sessionDescription;
	}
}
