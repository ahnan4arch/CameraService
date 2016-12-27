package com.example.zebul.cameraservice.av_streaming.rtp;

import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPayload;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AccessUnit;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by zebul on 11/24/16.
 */

public class RTPPacketTest {

    @Test
    public void test_when_rtpPacket_contains_notNull_ctor_args_then_toBytes_returns_notNull_array_bytes() {

        RTPHeader rtpHeader1 = new RTPHeader(true, (byte) 96, 28414, 72120836, 0x4B8B2DF3);

        AccessUnit accessUnit = new AccessUnit(new byte[]{0x01, 0x02});
        RTPPayload rtpPayload = new AACPayload(accessUnit);
        RTPPacket rtpPacket = new RTPPacket(rtpHeader1, rtpPayload);
        byte [] rtpPacketBytes = rtpPacket.toBytes();
        assertNotNull(rtpPacketBytes);
    }
}
