package com.example.zebul.cameraservice.av_streaming.rtsp.version;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VersionDecoderTest {

	@Test
	public void test_when_version_text_is_valid_then_Version_is_can_be_decoded() throws VersionDecodeException {
		
		String versionAsText = "RTSP/1.0";
		Version version = VersionDecoder.decode(versionAsText);
		assertNotNull(version);
		assertEquals(1, version.getMajorVersion());
		assertEquals(0, version.getMinorVersion());
	}
}
