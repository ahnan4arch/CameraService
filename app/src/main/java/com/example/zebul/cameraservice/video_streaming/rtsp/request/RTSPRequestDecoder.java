package com.example.zebul.cameraservice.video_streaming.rtsp.request;

import com.example.zebul.cameraservice.video_streaming.rtsp.Method;
import com.example.zebul.cameraservice.video_streaming.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.video_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.video_streaming.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.video_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.video_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.video_streaming.rtsp.version.VersionDecodeException;
import com.example.zebul.cameraservice.video_streaming.rtsp.version.VersionDecoder;

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
	private static final int POS_OF_HEADER_LINE = 1;
	
	private static final int POS_OF_METHOD_IN_REQUEST_LINE = 0;
	private static final int POS_OF_REQUEST_URI_IN_REQUEST_LINE = 1;
	private static final int POS_OF_RTSP_VERSION_IN_REQUEST_LINE = 2;
	
	public static RTSPRequest decode(String requestRepresentaionAsText)throws RTSP4xxClientRequestError {
		
		String[] requestRepresentaionAsTextLines = splitByLines(requestRepresentaionAsText);
		RequestLine requestLine = decodeRequestLine(requestRepresentaionAsTextLines);
		Header header = decodeRequestHeader(requestRepresentaionAsTextLines);
		return new RTSPRequest(requestLine.version, header, requestLine.method, requestLine.requestUri);
	}

	private static final String[] splitByLines(String requestRepresentaionAsText) 
			throws RTSP4xxClientRequestError{
		
		final int minLineNumber = POS_OF_HEADER_LINE+1;
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
		private String requestUri;
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
					"Request line should contains %d tokens: Method Request-URI RTSP-Version, separated by '%s'", 
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
		requestLine.requestUri = requestLineTokens[POS_OF_REQUEST_URI_IN_REQUEST_LINE];
		requestLine.version = version;
		return requestLine;
	}
	
	private static Header decodeRequestHeader(String [] requestRepresentaionAsTextLines) 
			throws RTSP4xxClientRequestError {
		
		String CSeqAsText = requestRepresentaionAsTextLines[POS_OF_HEADER_LINE];
		HeaderField CSeqHeaderField  = null;
		
		try {
			CSeqHeaderField = decodeHeaderField(CSeqAsText);
		} catch (RTSP4xxClientRequestError error) {
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, 
					"Bad CSeq header field: "+CSeqAsText);
		} 
		
		if(!CSeqHeaderField.getName().contains("CSeq")){
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, 
					"Bad CSeq header name: "+CSeqHeaderField.getName());
		}
		
		int CSeq = 0;
		try{
			CSeq = Integer.parseInt(CSeqHeaderField.getValue());
		}
		catch (NumberFormatException error) {
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, 
					"Bad format of CSeq header value: "+CSeqHeaderField.getValue());
		}
		
		HeaderFields headerFields = new HeaderFields();
		for(int lineIndex = (POS_OF_HEADER_LINE+1); lineIndex<requestRepresentaionAsTextLines.length; lineIndex++){
			
			try{
				String headerFieldAsTextLine = requestRepresentaionAsTextLines[lineIndex];
				boolean emptyLine = headerFieldAsTextLine.length()==0;
				boolean lineSeparator = headerFieldAsTextLine.startsWith(RTSPProtocol.LINE_SEPARATOR); 
				if(!(emptyLine||lineSeparator)){
				
					HeaderField headerField = decodeHeaderField(headerFieldAsTextLine);
					headerFields.add(headerField);
				}
			}
			catch(RTSP4xxClientRequestError error_){
				
				error_.printStackTrace();
			}
		}
		
		Header header = new Header(CSeq, headerFields);
		return header;
	}
	
	private static HeaderField decodeHeaderField(String headerFieldAsTextLine) throws RTSP4xxClientRequestError{
		
		int notFound = -1; int begIndex = 0; int endIndex = headerFieldAsTextLine.length()-1;
		int [] forbiddenIndexValues = new int[]{notFound, begIndex, endIndex};
		
		int indexOfColon = headerFieldAsTextLine.indexOf(':');
		if(Arrays.asList(forbiddenIndexValues).contains(indexOfColon)){
			throw new RTSP4xxClientRequestError(StatusCode.BAD_REQUEST, "Bad header field: "+headerFieldAsTextLine);
		}
		
		String name = "";
		String value = "";
		if(indexOfColon > -1){
		
			name = headerFieldAsTextLine.substring(0, indexOfColon).trim();
			value = headerFieldAsTextLine.substring(indexOfColon+1, headerFieldAsTextLine.length()).trim();
		}
		else{
			
			int foo = 1;
			int bar = foo;
		}
		
		return new HeaderField(name, value); 
	}
}
