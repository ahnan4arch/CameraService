package com.example.zebul.cameraservice.av_streaming.rtsp.response;


import com.example.zebul.cameraservice.av_streaming.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionEncoder;

public class RTSPResponseEncoder {

	public static String encode(RTSPResponse response){
		
		StatusCode statusCode = response.getStatusCode();
		String statusLine = String.format("%s%s%d%s%s", 
				VersionEncoder.encode(response.getVersion()),
				RTSPProtocol.SP,
				statusCode.getCode(),
				RTSPProtocol.SP,
				statusCode.getDetails());
		
		StringBuilder responseTextBuilder = new StringBuilder();
		responseTextBuilder.append(statusLine+RTSPProtocol.LINE_SEPARATOR);
		
		String CSeqLine = String.format("CSeq: %d", response.getHeader().getCSeq());
		responseTextBuilder.append(CSeqLine+RTSPProtocol.LINE_SEPARATOR);
		
		Header header = response.getHeader();
		for(HeaderField headerField: header.getHeaderFields()){
			
			String headerLine = String.format("%s: %s", headerField.getKnownType(), headerField.getValue());
			responseTextBuilder.append(headerLine+RTSPProtocol.LINE_SEPARATOR);
		}
		
		Body body = response.getBody();
		String contentLengthLine = String.format("Content-Length: %d", body.getContentLengthInBytes());
		responseTextBuilder.append(contentLengthLine+RTSPProtocol.LINE_SEPARATOR);
		responseTextBuilder.append(RTSPProtocol.LINE_SEPARATOR);
		
		responseTextBuilder.append(body.getContent());
		//add line separator at the end of resopnse
		responseTextBuilder.append(RTSPProtocol.LINE_SEPARATOR);
		return responseTextBuilder.toString();
	}
}
