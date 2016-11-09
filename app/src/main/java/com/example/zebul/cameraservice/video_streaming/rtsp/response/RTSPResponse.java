package com.example.zebul.cameraservice.video_streaming.rtsp.response;

import com.example.zebul.cameraservice.video_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.RTSPMessage;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.body.Body;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.video_streaming.rtsp.version.Version;

public class RTSPResponse extends RTSPMessage {

	private StatusCode statusCode;
	private Body body;
	
	public RTSPResponse(StatusCode statusCode, Version version, Header header) {
		this(statusCode, version, header, new Body(""));
	}
	
	public RTSPResponse(StatusCode statusCode, Version version, Header header, Body body) {
		super(version, header);
		this.statusCode = statusCode;
		this.body = body;
	}
	
	public RTSPResponse(StatusCode statusCode, Version version, int CSeq) {
		super(version, createEmptyHeader(CSeq));
		this.statusCode = statusCode;
		this.body = createEmptyBody();
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}
	
	public Body getBody() {
		return body;
	}
	
	private static Header createEmptyHeader(int CSeq) {
		
		return new Header(CSeq, new HeaderFields());
	}

	private static Body createEmptyBody() {
		
		return new Body("");
	}
}
