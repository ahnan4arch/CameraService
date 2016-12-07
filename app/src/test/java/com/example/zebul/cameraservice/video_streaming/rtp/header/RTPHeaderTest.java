package com.example.zebul.cameraservice.video_streaming.rtp.header;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 11/25/16.
 */

public class RTPHeaderTest {

    @Test
    public void when_stream_is_taken_from_wireshark_then_decode_result_is_equal_to_wireshark_output_2() throws Exception {

        RTPHeader rtpHeader1 = new RTPHeader(true, (byte) 96, 28414, 72120836, 0x4B8B2DF3);

        byte[] expectedEncodedRTPHeader = new byte[]{
                (byte) 0x80, (byte) 0xE0, (byte) 0x6E, (byte) 0xFE, (byte) 0x04, (byte) 0x4C,
                (byte) 0x7A, (byte) 0x04, (byte) 0x4B, (byte) 0x8B, (byte) 0x2D, (byte) 0xF3};

        byte[] resultEncodedRTPHeader1 = rtpHeader1.toBytes();
        assertArrayEquals(expectedEncodedRTPHeader, resultEncodedRTPHeader1);

        RTPHeader rtpHeader2 = new RTPHeader();
        rtpHeader2.fromBytes(resultEncodedRTPHeader1);
        assertEquals(rtpHeader1, rtpHeader2);
    }

}
