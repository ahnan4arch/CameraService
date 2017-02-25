package com.example.zebul.cameraservice.av_protocols.sdp;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SessionDescriptionTest {

	//According to RTSP spec: A session identifier MUST be chosen randomly and MUST be at least eight octets long
	@Test
	public void test_when_SessionId_is_generated_then_it_contains_at_least_8_octets() {
		
		for(int i=0; i<100; i++){
		
			String sessionId = SessionDescription.generateIdentifier();
			assertTrue(sessionId.length()>=8);
		}
	}

	@Test
	public void test_SessionDescription() {

		SessionDescription sessionDescription = new SessionDescription(5001);
        sessionDescription.name = "Some name";

		MediaDescription videoMediaDescription = new MediaDescription(MediaDescription.MediaType.Video, 0, "RTP/AVP", "96");
		videoMediaDescription.addAttribute(new Attribute("rtpmap","96 H264/90000"));
		videoMediaDescription.addAttribute(new Attribute("fmtp","96 packetization-mode=1;profile-level-id=42e00d;"));
		videoMediaDescription.addAttribute(new Attribute("control","trackID=1"));
		sessionDescription.addMediaDescription(videoMediaDescription);

		MediaDescription audioMediaDescription = new MediaDescription(MediaDescription.MediaType.Audio, 0, "RTP/AVP", "96");
		audioMediaDescription.addAttribute(new Attribute("rtpmap","96 mpeg4-generic/8000"));
		audioMediaDescription.addAttribute(new Attribute("fmtp","96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;"));
		audioMediaDescription.addAttribute(new Attribute("control","trackID=2"));
		sessionDescription.addMediaDescription(audioMediaDescription);

		String actualDescription = sessionDescription.getDescription();

		String expectedDescription =
			"v=0\r\n"+
			"o=- 0 0 IN IP4 127.0.0.1\r\n"+
			"s=Some name\r\n"+
			"i=N/A\r\n"+
			"c=IN IP4 0.0.0.0\r\n"+
			"t=0 0\r\n"+
			"m=video 0 RTP/AVP 96\r\n"+
			"a=rtpmap:96 H264/90000\r\n"+
			"a=fmtp:96 packetization-mode=1;profile-level-id=42e00d;\r\n"+
			"a=control:trackID=1\r\n"+
			"m=audio 0 RTP/AVP 96\r\n"+
			"a=rtpmap:96 mpeg4-generic/8000\r\n"+
			"a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;\r\n"+
			"a=control:trackID=2\r\n";

		assertEquals(expectedDescription, actualDescription);

		assertTrue(sessionDescription.mediaHasValueOfAttribute(MediaDescription.MediaType.Video, "control", "trackID=1"));
		assertTrue(sessionDescription.mediaHasValueOfAttribute(MediaDescription.MediaType.Audio, "control", "trackID=2"));
	}

}
