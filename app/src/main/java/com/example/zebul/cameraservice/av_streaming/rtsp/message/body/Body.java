package com.example.zebul.cameraservice.av_streaming.rtsp.message.body;

import com.example.zebul.cameraservice.av_streaming.sdp.SessionDescription;

public class Body {

	private String content;
	private SessionDescription sessionDescription;

	public Body(String content){
	
		this.content = content;
	}

	public String getContent() {
		
		return content;
	}

	public int getContentLengthInBytes() {
		
		return content.length();
	}

	public SessionDescription getSessionDescription() {
		return sessionDescription;
	}
}
