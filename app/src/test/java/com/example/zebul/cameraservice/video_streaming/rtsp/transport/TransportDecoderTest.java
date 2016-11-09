package com.example.zebul.cameraservice.video_streaming.rtsp.transport;

import org.junit.Assert;
import org.junit.Test;


public class TransportDecoderTest {

	@Test
	public void test_1() {
		
		String transportAsText = "RTP/AVP;unicast;client_port=3456-3457;mode=\"PLAY\";ttl=333";
		Transport transport = TransportDecoder.decode(transportAsText);
		Assert.assertEquals(Transport.TransportProtocol.RTP, transport.getTransportProtocol());
		Assert.assertEquals(Transport.Profile.AVP, transport.getProfile());
		Assert.assertEquals(Transport.LowerTransport.UNSPECIFIED, transport.getLowerTransport());
		Assert.assertEquals(3456, transport.getMinClientPort());
		Assert.assertEquals(3457, transport.getMaxClientPort());
		Assert.assertEquals(Transport.Mode.PLAY, transport.getMode());
		Assert.assertEquals(333, transport.getTimeToLive());
	}
}
