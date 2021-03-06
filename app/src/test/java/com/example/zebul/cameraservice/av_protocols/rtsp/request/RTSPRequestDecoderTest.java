package com.example.zebul.cameraservice.av_protocols.rtsp.request;

import com.example.zebul.cameraservice.av_protocols.rtsp.Method;
import com.example.zebul.cameraservice.av_protocols.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_protocols.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_protocols.rtsp.error.RTSPError;

import org.junit.Assert;
import org.junit.Test;


public class RTSPRequestDecoderTest {

	private static final String SEP = RTSPProtocol.LINE_SEPARATOR;
	
	@Test
	public void test_when_text_contains_valid_request_then_text_is_decodeable_to_RTSPRequest() throws RTSPError {
		
		StringBuilder requestTextBuilder = new StringBuilder();
		requestTextBuilder.append("OPTIONS rtsp://192.168.1.106:6880 RTSP/1.0"+SEP);
		requestTextBuilder.append("CSeq: 2"+SEP);
		requestTextBuilder.append("User-Agent: LibVLC/2.2.2 (LIVE555 Streaming Media v2016.02.09)");
		
		String requestRepresentaionAsText = requestTextBuilder.toString();
		RTSPRequest request = RTSPRequestDecoder.decode(requestRepresentaionAsText);
		Assert.assertNotNull(request);
	}
	
	@Test
	public void test_when_text_contains_valid_request_then_text_is_decodeable2() throws RTSPError {
		
		StringBuilder requestTextBuilder = new StringBuilder();
		requestTextBuilder.append("OPTIONS rtsp://192.168.1.106:6880 RTSP/1.0"+SEP);
		requestTextBuilder.append("CSeq: 16"+SEP);
		requestTextBuilder.append("User-Agent: RealMedia Player Version 6.0.9.1235 (linux-2.0-libc6-i386-gcc2.95)"+SEP);
		requestTextBuilder.append("ClientChallenge: 9e26d33f2984236010ef6253fb1887f7"+SEP);
		requestTextBuilder.append("PlayerStarttime: [28/03/2003:22:50:23 00:00]"+SEP);
		requestTextBuilder.append("CompanyID: KnKV4M4I/B2FjJ1TToLycw=="+SEP);
		requestTextBuilder.append("GUID: 00000000-0000-0000-0000-000000000000"+SEP);
		requestTextBuilder.append("RegionData: 0"+SEP);
		requestTextBuilder.append("ClientID: Linux_2.4_6.0.9.1235_play32_RN01_EN_586"+SEP);
		
		String requestRepresentaionAsText = requestTextBuilder.toString();
		RTSPRequest request = RTSPRequestDecoder.decode(requestRepresentaionAsText);
		Assert.assertNotNull(request);
		
		Assert.assertEquals(Method.OPTIONS, request.getMethod());
		Assert.assertEquals(16, request.getHeader().getCSeq());
	}

	@Test
	public void test_when_request_contains_url_then_it_is_retrievable() throws RTSP4xxClientRequestError {
		
		final String EXAMPLE_SETUP_REQUEST =
		"SETUP rtsp://192.168.1.21:9999/trackID=1 RTSP/1.0"+SEP+
		"CSeq: 4"+SEP+
		"User-Agent: LibVLC/2.2.2 (LIVE555 Streaming Media v2016.02.09"+SEP+
		"Transport: RTP/AVP;unicast;client_port=49254-49255"+SEP+SEP;

		RTSPRequest request = RTSPRequestDecoder.decode(EXAMPLE_SETUP_REQUEST);
		Assert.assertNotNull(request);
		Assert.assertEquals(Method.SETUP, request.getMethod());

	}
	
	/*
	SETUP rtsp://192.168.1.106:6880/trackID=1 RTSP/1.0
	CSeq: 4
	User-Agent: LibVLC/2.2.2 (LIVE555 Streaming Media v2016.02.09)
	Transport: RTP/AVP;unicast;client_port=33070-33071
	*/
	
	/*
	PLAY rtsp://192.168.1.106:6880 RTSP/1.0
	CSeq: 5
	User-Agent: LibVLC/2.2.2 (LIVE555 Streaming Media v2016.02.09)
	SessionDescription: 1185d20035702ca
	Range: npt=0.000- 
	 */
	
}
