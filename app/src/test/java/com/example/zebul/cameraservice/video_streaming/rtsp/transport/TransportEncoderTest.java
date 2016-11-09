package com.example.zebul.cameraservice.video_streaming.rtsp.transport;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TransportEncoderTest {

	@Test
	public void test_1() {
		
		Transport transport = new Transport();
		transport.setTransportProtocol(Transport.TransportProtocol.RTP);
		transport.setProfile(Transport.Profile.AVP);
		transport.setLowerTransport(Transport.LowerTransport.UDP);
		transport.setTransmissionType(Transport.TransmissionType.unicast);
		transport.setClientPortRange(1111, 2222);
		transport.setServerPortRange(3333, 4444);
		transport.setMode(Transport.Mode.PLAY);
		String transportAsText = TransportEncoder.encode(transport);
		assertTrue(transportAsText.contains("RTP/AVP/UDP"));
		assertTrue(transportAsText.contains("unicast"));
		assertTrue(transportAsText.contains("client_port=1111-2222"));
		assertTrue(transportAsText.contains("server_port=3333-4444"));
		assertTrue(transportAsText.contains("PLAY"));
	}
}
