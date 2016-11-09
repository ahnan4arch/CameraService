package com.example.zebul.cameraservice.video_streaming.rtsp.version;

import org.junit.Assert;
import org.junit.Test;


public class VersionEncoderTest {

	@Test
	public void test() {
		
		String versionAsText = VersionEncoder.encode(new Version(1,1));
		Assert.assertEquals("RTSP/1.1", versionAsText);
	}

}
