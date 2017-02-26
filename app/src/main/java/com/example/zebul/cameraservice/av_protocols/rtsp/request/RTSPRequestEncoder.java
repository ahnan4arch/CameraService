package com.example.zebul.cameraservice.av_protocols.rtsp.request;

import com.example.zebul.cameraservice.av_protocols.rtsp.Method;
import com.example.zebul.cameraservice.av_protocols.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_protocols.rtsp.URI;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_protocols.rtsp.version.Version;
import com.example.zebul.cameraservice.av_protocols.rtsp.version.VersionEncoder;

public class RTSPRequestEncoder {

	private static final String SEP = RTSPProtocol.LINE_SEPARATOR;

	private RTSPRequestEncoder(){

	}

	public static String encode(RTSPRequest request){

		final Method method = request.getMethod();
		final URI requestUri = request.getRequestUri();
		final Version version = request.getVersion();

		final StringBuilder requestTextBuilder = new StringBuilder();
		final String versionAsText = VersionEncoder.encode(version);
		final String requestUriAsText = requestUri.toString();
		requestTextBuilder.append(method+" "+requestUriAsText+" "+versionAsText+SEP);

		final Header header = request.getHeader();
		final HeaderFields headerFields = header.getHeaderFields();
		for(HeaderField headerField: headerFields){

			requestTextBuilder.append(headerField.toString()+SEP);
		}
		requestTextBuilder.append(SEP);
		return requestTextBuilder.toString();
	}
}
