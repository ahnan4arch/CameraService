package com.example.zebul.cameraservice.av_streaming.rtsp.error;


import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;

public class RTSP4xxClientRequestError extends RTSPError {

	/**
	 * 
	 */
	private static final long serialVersionUID = -679651493013726026L;

	public RTSP4xxClientRequestError(StatusCode statusCode, String errorMessage) {
		super(statusCode, errorMessage);
	}

}
