package com.example.zebul.cameraservice.av_streaming.rtsp.message.header;

import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Transport;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.TransportDecoder;

import org.junit.Assert;
import org.junit.Test;


public class TransportDecoderTest {

	@Test
	public void test_1() {
		
		String transportAsText = "RTP/AVP;unicast;client_port=3456-3457;ssrc=DEADBEEF;mode=\"PLAY\";ttl=333";
		Transport transport = TransportDecoder.decode(transportAsText);
		Assert.assertEquals(Transport.TransportProtocol.RTP, transport.getTransportProtocol());
		Assert.assertEquals(Transport.Profile.AVP, transport.getProfile());
		Assert.assertEquals(Transport.LowerTransport.UNSPECIFIED, transport.getLowerTransport());
		Assert.assertEquals(3456, transport.getMinClientPort());
		Assert.assertEquals(3457, transport.getMaxClientPort());
		Assert.assertEquals(Transport.Mode.PLAY, transport.getMode());
		Assert.assertEquals(333, transport.getTimeToLive());
		Assert.assertEquals(0xDEADBEEF, transport.getSsrc());
	}
}
