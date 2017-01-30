package com.example.zebul.cameraservice.av_streaming.rtsp.message.header;

import static com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField.KnownName.CSeq;

public class Header {

	private HeaderFields headerFields;

	public Header(int CSeq) {

		headerFields = new HeaderFields();
		setCSeq(CSeq);
	}

	public Header(HeaderFields headerFields) {
		this.headerFields = headerFields;
	}

	public int getCSeq() {

		HeaderField headerField = headerFields.find(CSeq);
		if(headerField == null){
			return -1;
		}
		try{

			return Integer.parseInt(headerField.getValue());
		}
		catch(NumberFormatException exc){

			return -1;
		}
	}

	public void setCSeq(int cSeq){

		HeaderField headerField = headerFields.find(CSeq);
		if(headerField != null){
			headerField.setValue(cSeq);
		}
		else{
			headerField = new HeaderField(CSeq, cSeq);
			headerFields.add(headerField);
		}
	}

	public void setHeaderFields(HeaderFields headerFields) {
		this.headerFields = headerFields;
	}

	public HeaderField findHeaderField(HeaderField.KnownName headerFieldKnownName) {
		return headerFields.find(headerFieldKnownName);
	}

	public String getHeaderFieldValue(HeaderField.KnownName headerFieldKnownName) {
		HeaderField headerField = headerFields.find(headerFieldKnownName);
		if(headerField == null){
			return null;
		}
		return headerField.getValue();
	}

	public HeaderFields getHeaderFields() {
		return headerFields;
	}

	public int getNumberOfFields() {
		return headerFields.getNumberOfFields();
	}

	public void addHeaderField(HeaderField headerField) {

		headerFields.add(headerField);
	}
}
