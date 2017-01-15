package com.example.zebul.cameraservice.av_streaming.rtsp.request;

import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_streaming.rtsp.URI;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionEncoder;

public class RTSPRequestEncoder {

	private static final String SEP = RTSPProtocol.LINE_SEPARATOR;

	public static String encode(RTSPRequest request){

		final Method method = request.getMethod();
		final URI requestUri = request.getRequestUri();
		final Version version = request.getVersion();

		StringBuilder requestTextBuilder = new StringBuilder();
		String versionAsText = VersionEncoder.encode(version);
		String requestUriAsText = requestUri.toString();
		requestTextBuilder.append(method+" "+requestUriAsText+" "+versionAsText+SEP);

		final Header header = request.getHeader();
		requestTextBuilder.append("CSeq: "+header.getCSeq()+SEP);
		final HeaderFields headerFields = header.getHeaderFields();
		for(HeaderField headerField: headerFields){

			requestTextBuilder.append(headerField.getName()+": "+headerField.getValue()+SEP);
		}
		requestTextBuilder.append(SEP);
		return requestTextBuilder.toString();
	}
}
