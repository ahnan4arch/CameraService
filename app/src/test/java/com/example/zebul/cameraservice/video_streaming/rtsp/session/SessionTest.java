package com.example.zebul.cameraservice.video_streaming.rtsp.session;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SessionTest {

	//According to RTSP spec: A session identifier MUST be chosen randomly and MUST be at least eight octets long
	@Test
	public void test_when_SessionId_is_generated_then_it_contains_at_least_8_octets() {
		
		for(int i=0; i<100; i++){
		
			String sessionId = Session.generateIdentifier();
			assertTrue(sessionId.length()>=8);
		}
	}

}
