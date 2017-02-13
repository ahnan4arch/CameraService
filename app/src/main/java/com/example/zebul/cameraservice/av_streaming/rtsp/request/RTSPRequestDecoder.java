package com.example.zebul.cameraservice.av_streaming.rtsp.request;

import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.URI;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionDecodeException;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionDecoder;

import java.net.MalformedURLException;
import java.util.Arrays;


public class RTSPRequestDecoder {

	/*
	Request		=		Request-Line			; Section 6.1
	 			*(      general-header			; Section 5
				|       request-header			; Section 6.2
				|       entity-header )			; Section 8.1
						CRLF
                        [ message-body ]		; Section 4.3 
	*/
	private static final int POS_OF_REQUEST_LINE = 0;
	
	private static final int POS_OF_METHOD_IN_REQUEST_LINE = 0;
	private static final int POS_OF_REQUEST_URI_IN_REQUEST_LINE = 1;
	private static final int POS_OF_RTSP_VERSION_IN_REQUEST_LINE = 2;
	
	public static RTSPRequest decode(String requestRepresentaionAsText)throws RTSP4xxClientRequestError {
		
		String[] requestRepresentaionAsTextLines = splitByLines(requestRepresentaionAsText);
		RequestLine requestLine = decodeRequestLine(requestRepresentaionAsTextLines);
		Header header = HeaderDecoder.decode(requestRepresentaionAsTextLines);
		return new RTSPRequest(requestLine.requestUri, requestLine.version, header, requestLine.method);
	}

	private static final String[] splitByLines(String requestRepresentaionAsText) 
			throws RTSP4xxClientRequestError{
		
		final int minLineNumber = HeaderDecoder.POS_OF_HEADER_LINE+1;
		String[] requestRepresentaionAsTextLines = requestRepresentaionAsText.split(
				RTSPProtocol.LINE_SEPARATOR);
		if(requestRepresentaionAsTextLines.length < minLineNumber){
			String errorMessage = String.format(
					"Request should contains at least %d lines", minLineNumber); 
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, errorMessage);
		}
		return requestRepresentaionAsTextLines;
	}
	
	private static class RequestLine{
		
		private Method method;
		private URI requestUri;
		private Version version;
	}
	
	// Request-Line = Method SP Request-URI SP RTSP-Version CRLF (page 21)
	static RequestLine decodeRequestLine(String [] requestRepresentaionAsTextLines) 
			throws RTSP4xxClientRequestError{
		
		String requestLineAsText = requestRepresentaionAsTextLines[POS_OF_REQUEST_LINE];
		final int minRequestLineTokensNumber = POS_OF_RTSP_VERSION_IN_REQUEST_LINE+1;
		String[] requestLineTokens = requestLineAsText.split(RTSPProtocol.SP);
		if(requestLineTokens.length < minRequestLineTokensNumber){
			String errorMessage = String.format(
					"Request line should contains %d parts: Method Request-URI RTSP-Version, separated by '%s'",
					minRequestLineTokensNumber, RTSPProtocol.SP); 
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, errorMessage);
		}
		
		String methodAsText = requestLineTokens[POS_OF_METHOD_IN_REQUEST_LINE];
		Method method = Method.valueOf(methodAsText);
		if(method == null){
			
			String errorMessage = String.format("Unknown method: %s", methodAsText);
			throw new RTSP4xxClientRequestError(StatusCode.METHOD_NOT_ALLOWED, errorMessage);
		}
		
		String versionAsText = requestLineTokens[POS_OF_RTSP_VERSION_IN_REQUEST_LINE];
		Version version = null;
		try {
			version = VersionDecoder.decode(versionAsText);
		} catch (VersionDecodeException error) {
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, error.getMessage());
		}
		
		RequestLine requestLine = new RequestLine();
		requestLine.method = method;
		try {
			requestLine.requestUri = URI.fromString(requestLineTokens[POS_OF_REQUEST_URI_IN_REQUEST_LINE]);
		} catch (MalformedURLException e) {
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, e.getMessage());
		}
		requestLine.version = version;
		return requestLine;
	}
}
