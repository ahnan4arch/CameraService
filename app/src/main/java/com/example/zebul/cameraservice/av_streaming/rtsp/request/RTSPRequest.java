package com.example.zebul.cameraservice.av_streaming.rtsp.request;


import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.URI;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.RTSPMessage;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;

public class RTSPRequest extends RTSPMessage {
	
	private Method method;
	private URI requestUri;
	
	public RTSPRequest(Version version, Header header, Method method, URI requestUri) {
		
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

	public URI getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(URI requestUri) {
		this.requestUri = requestUri;
	}	
}
