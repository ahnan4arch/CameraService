package com.example.zebul.cameraservice.video_streaming.rtp;

import com.example.zebul.cameraservice.video_streaming.rtp.header.RTPHeader;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by zebul on 11/24/16.
 */

public class RTPPacketTest {

    @Test
    public void test1() {

        RTPHeader rtpHeader1 = new RTPHeader(true, (byte) 96, 28414, 72120836, 0x4B8B2DF3);
        RTPPacket rtpPacket = new RTPPacket(rtpHeader1, null);
        byte [] rtpPacketBytes = rtpPacket.toBytes();
        assertNotNull(rtpPacketBytes);
    }
}
