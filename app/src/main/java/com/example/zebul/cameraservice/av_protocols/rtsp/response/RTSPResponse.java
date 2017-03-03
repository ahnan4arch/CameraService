package com.example.zebul.cameraservice.av_protocols.rtsp.response;

import com.example.zebul.cameraservice.av_protocols.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.RTSPMessage;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_protocols.rtsp.version.Version;

public class RTSPResponse extends RTSPMessage {

	private StatusCode statusCode;
	private Body body;
	
	public RTSPResponse(StatusCode statusCode, Version version, Header header) {
		this(statusCode, version, header, new Body(null));
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

		HeaderFields headerFields = new HeaderFields();
		headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq));
		return new Header(headerFields);
	}

	private static Body createEmptyBody() {
		
		return new Body(null);
	}
}
