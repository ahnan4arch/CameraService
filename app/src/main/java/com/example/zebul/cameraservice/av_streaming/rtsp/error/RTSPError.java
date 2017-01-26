package com.example.zebul.cameraservice.av_streaming.rtsp.error;

import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;

public class RTSPError extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4940686183222460219L;
	private StatusCode statusCode;
	
	public RTSPError(StatusCode statusCode, String errorMessage){
		
		super(errorMessage);
		this.statusCode = statusCode;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}
}
