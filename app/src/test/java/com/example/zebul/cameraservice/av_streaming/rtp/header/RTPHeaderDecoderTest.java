package com.example.zebul.cameraservice.av_streaming.rtp.header;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 10/24/16.
 */
public class RTPHeaderDecoderTest {

    @Test
    public void test_decode0() throws Exception {

        byte [] data = new byte[]{
                (byte)0x80, (byte)0xE0, (byte)0x24, (byte)0x04, (byte)0x0A, (byte)0xA9,
                (byte)0xAC, (byte)0x9E, (byte)0x0D, (byte)0xE2, (byte)0x6B, (byte)0x36};

        RTPHeader rtpHeader = RTPHeaderDecoder.decode(data);
        assertNotNull(rtpHeader);
    }

    @Test
    public void when_stream_is_taken_from_wireshark_then_decode_result_is_equal_to_wireshark_output_1() throws Exception {

        byte [] data = new byte[]{
                (byte)0x80, (byte)0xe0, (byte)0x6e, (byte)0xfe, (byte)0x04, (byte)0x4c, (byte)0x7a, (byte)0x04,
                (byte)0xeb, (byte)0xcf, (byte)0x56, (byte)0x03, (byte)0x00, (byte)0x10, (byte)0x09, (byte)0xb0};

        RTPHeader rtpHeader = RTPHeaderDecoder.decode(data);

        int SSRC = 0xEBCF5603;
        assertEquals(SSRC, rtpHeader.getSSRC());
        assertEquals(28414, rtpHeader.getSequenceNumber());
        assertEquals(72120836, rtpHeader.getTimestamp());
        assertEquals(96, rtpHeader.getPayloadType());
    }

    @Test
    public void when_stream_is_taken_from_wireshark_then_decode_result_is_equal_to_wireshark_output_2() throws Exception {

        byte [] data = new byte[]{
                (byte)0x80, (byte)0x60, (byte)0x01, (byte)0x60, (byte)0x08, (byte)0xc5, (byte)0xe1, (byte)0x41,
                (byte)0x4b, (byte)0x8b, (byte)0x2d, (byte)0xf3, (byte)0x7c, (byte)0x81, (byte)0xe2, (byte)0x23};

        RTPHeader rtpHeader = RTPHeaderDecoder.decode(data);

        assertEquals(0x4B8B2DF3, rtpHeader.getSSRC());
        assertEquals(352, rtpHeader.getSequenceNumber());
        assertEquals(147185985, rtpHeader.getTimestamp());
        assertEquals(96, rtpHeader.getPayloadType());
    }

    @Test
    public void test_seq() throws Exception {

        byte [] seq0 = new byte[]{
                (byte)0x80, (byte)0xe0, (byte)0x02, (byte)0x6e, (byte)0x00, (byte)0x38, (byte)0xf2, (byte)0x1f,
                (byte)0x28, (byte)0xc0, (byte)0xbb, (byte)0x36, (byte)0x41, (byte)0x41, (byte)0xe4, (byte)0x40};

        RTPHeader rtpHeader0 = RTPHeaderDecoder.decode(seq0);

        int SSRC0 = rtpHeader0.getSSRC();
        int sequenceNumber0 = rtpHeader0.getSequenceNumber();
        int timestamp0 = rtpHeader0.getTimestamp();
        byte payloadType0 = rtpHeader0.getPayloadType();

        byte [] seq1 = new byte[]{
                (byte)0x80, (byte)0xe0, (byte)0x02, (byte)0x6f, (byte)0x00, (byte)0x39, (byte)0x09, (byte)0x8f,
                (byte)0x28, (byte)0xc0, (byte)0xbb, (byte)0x36, (byte)0x41, (byte)0x41, (byte)0xe6, (byte)0x60};

        RTPHeader rtpHeader1 = RTPHeaderDecoder.decode(seq1);

        int SSRC1 = rtpHeader1.getSSRC();
        int sequenceNumber1 = rtpHeader1.getSequenceNumber();
        int timestamp1 = rtpHeader1.getTimestamp();
        byte payloadType1 = rtpHeader1.getPayloadType();

        int foo = 0;
        int bar = foo;
    }
}
