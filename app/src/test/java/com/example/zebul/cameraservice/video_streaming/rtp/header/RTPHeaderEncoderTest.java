package com.example.zebul.cameraservice.video_streaming.rtp.header;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 10/24/16.
 */
public class RTPHeaderEncoderTest {

    @Test
    public void when_stream_is_taken_from_wireshark_then_decode_result_is_equal_to_wireshark_output_1() throws Exception {

        RTPHeader rtpHeader = new RTPHeader(true, (byte)96, 28414, 72120836, 0xEBCF5603);

        byte [] expectedEncodedRTPHeader = new byte[]{
                (byte)0x80, (byte)0xe0, (byte)0x6e, (byte)0xfe, (byte)0x04, (byte)0x4c, (byte)0x7a, (byte)0x04,
                (byte)0xeb, (byte)0xcf, (byte)0x56, (byte)0x03, (byte)0x00, (byte)0x10, (byte)0x09, (byte)0xb0};

        byte [] resultEncodedRTPHeader = RTPHeaderEncoder.encode(rtpHeader);
        assertArrayEquals(expectedEncodedRTPHeader, resultEncodedRTPHeader);

    }

    @Test
    public void when_stream_is_taken_from_wireshark_then_decode_result_is_equal_to_wireshark_output_2() throws Exception {

        RTPHeader rtpHeader = new RTPHeader(true, (byte)96, 28414, 72120836, 0x4B8B2DF3);

        byte [] expectedEncodedRTPHeader = new byte[]{
                (byte)0x80, (byte)0x60, (byte)0x01, (byte)0x60, (byte)0x08, (byte)0xc5, (byte)0xe1, (byte)0x41,
                (byte)0x4b, (byte)0x8b, (byte)0x2d, (byte)0xf3, (byte)0x7c, (byte)0x81, (byte)0xe2, (byte)0x23};

        byte [] resultEncodedRTPHeader = RTPHeaderEncoder.encode(rtpHeader);
        assertArrayEquals(expectedEncodedRTPHeader, resultEncodedRTPHeader);
    }

    //80 60 6c 9a 05 e3 83 1b 77 f2 7c 40 7c 81 e2 e1
}
