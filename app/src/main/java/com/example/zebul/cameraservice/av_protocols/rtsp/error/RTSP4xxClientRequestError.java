package com.example.zebul.cameraservice.av_protocols.rtsp.error;


import com.example.zebul.cameraservice.av_protocols.rtsp.StatusCode;

public class RTSP4xxClientRequestError extends RTSPError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -679651493013726026L;

	public RTSP4xxClientRequestError(StatusCode statusCode, String errorMessage) {
		super(statusCode, errorMessage);
	}

}
