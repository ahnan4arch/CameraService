package com.example.zebul.cameraservice.video_streaming.rtsp.request;


import com.example.zebul.cameraservice.video_streaming.rtsp.Method;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.RTSPMessage;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.video_streaming.rtsp.version.Version;

public class RTSPRequest extends RTSPMessage {
	
	private Method method;
	private String requestUri;
	
	public RTSPRequest(Version version, Header header, Method method, String requestUri) {
		
		super(version, header);
		this.method = method;
		this.requestUri = requestUri;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}	
}
