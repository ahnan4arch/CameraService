package com.example.zebul.cameraservice.av_streaming.rtsp.message.body;

public class Body {

	private String content;
	
	public Body(String content){
	
		this.content = content;
	}

	public String getContent() {
		
		return content;
	}

	public int getContentLengthInBytes() {
		
		return content.length();
	}
}
