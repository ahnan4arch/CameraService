package com.example.zebul.cameraservice.video_streaming.rtsp.message.header;

public class Header {

	private int CSeq;
	private HeaderFields headerFields;
	
	public Header(int CSeq, HeaderFields headerFields) {
		this.CSeq = CSeq;
		this.headerFields = headerFields;
	}

	public int getCSeq() {
		return CSeq;
	}

	public void setCSeq(int CSeq) {
		this.CSeq = CSeq;
	}

	public HeaderFields getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(HeaderFields headerFields) {
		this.headerFields = headerFields;
	}

}
