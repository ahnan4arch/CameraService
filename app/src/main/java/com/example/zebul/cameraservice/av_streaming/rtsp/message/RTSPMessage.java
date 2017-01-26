package com.example.zebul.cameraservice.av_streaming.rtsp.message;


import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;

public class RTSPMessage {

	private Version version;
	private Header header;
	
	public RTSPMessage(Version version, Header header) {
		this.version = version;
		this.header = header;
	}
	
	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header RTSPRequestHeader) {
		this.header = RTSPRequestHeader;
	}

	public HeaderField findHeaderField(HeaderField.KnownName headerFieldKnownName) {
		return header.findHeaderField(headerFieldKnownName);
	}

	public String getHeaderFieldValue(HeaderField.KnownName knownName) {
		return header.getHeaderFieldValue(knownName);
	}
}
