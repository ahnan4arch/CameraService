package com.example.zebul.cameraservice.av_streaming.rtsp.response;

import com.example.zebul.cameraservice.av_streaming.rtsp.RTSPProtocol;

import org.junit.Test;

import static org.junit.Assert.fail;

public class RTSPResponseEncoderTest {

	private static final String SEP = RTSPProtocol.LINE_SEPARATOR;
	
	@Test
	public void test_encoded_response_contains_at_least_one_message_length_indicator() {
		fail("Not yet implemented");
	}
	

	public void test_describe_response(){
		
		StringBuilder requestTextBuilder = new StringBuilder();
		
		requestTextBuilder.append("RTSP/1.0 200 OK"+SEP);
		requestTextBuilder.append("Server: MajorKernelPanic RTSP Server"+SEP);
		requestTextBuilder.append("Cseq: 3"+SEP);
		requestTextBuilder.append("Content-Length: 264"+SEP);
		requestTextBuilder.append("Content-Base: 192.168.1.4:1234/"+SEP);
		requestTextBuilder.append("Content-Type: application/sdp"+SEP);
		requestTextBuilder.append(""+SEP);
		requestTextBuilder.append("v=0"+SEP);
		requestTextBuilder.append("o=- 0 0 IN IP4 192.168.1.4"+SEP);
		requestTextBuilder.append("s=Unnamed"+SEP);
		requestTextBuilder.append("i=N/A"+SEP);
		requestTextBuilder.append("c=IN IP4 192.168.1.106"+SEP);
		requestTextBuilder.append("t=0 0"+SEP);
		requestTextBuilder.append("a=recvonly"+SEP);
		requestTextBuilder.append("m=video 5006 RTP/AVP 96"+SEP);
		requestTextBuilder.append("a=rtpmap:96 H264/90000"+SEP);
		requestTextBuilder.append("a=fmtp:96 packetization-mode=1;profile-level-id=42801f;sprop-parameter-sets=Z0KAH+kCg/I=,aM4G8g==;"+SEP);
		requestTextBuilder.append("a=control:trackID=1"+SEP);
		requestTextBuilder.append(SEP);	
	}
	
	public void test_setup_response(){
		
		StringBuilder requestTextBuilder = new StringBuilder();
		requestTextBuilder.append("RTSP/1.0 200 OK"+SEP);
		requestTextBuilder.append("Server: MajorKernelPanic RTSP Server"+SEP);
		requestTextBuilder.append("Cseq: 4"+SEP);
		requestTextBuilder.append("Content-Length: 0"+SEP);
		requestTextBuilder.append("Transport: RTP/AVP/UDP;unicast;destination=192.168.1.106;client_port=5006-5007;server_port=45995-40564;ssrc=dc07d0ec;mode=play"+SEP);
		requestTextBuilder.append("Session: 1185d20035702ca"+SEP);
		requestTextBuilder.append("Cache-Control: no-cache"+SEP);
		requestTextBuilder.append(SEP);	
	}
}
