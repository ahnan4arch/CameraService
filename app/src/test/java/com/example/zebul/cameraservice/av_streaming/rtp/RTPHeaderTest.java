package com.example.zebul.cameraservice.av_streaming.rtp;

import com.example.zebul.cameraservice.av_streaming.rtp.RTPHeader;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 11/25/16.
 */

public class RTPHeaderTest {

    @Test
    public void compare_derizalization_with_wireshark_data__sample1() throws Exception {

        byte [] dataFromWireshark = new byte[]{
                (byte)0x80, (byte)0xe0, (byte)0x6e, (byte)0xfe, (byte)0x04, (byte)0x4c, (byte)0x7a, (byte)0x04,
                (byte)0xeb, (byte)0xcf, (byte)0x56, (byte)0x03, (byte)0x00, (byte)0x10, (byte)0x09, (byte)0xb0};

        RTPHeader rtpHeader = RTPHeader.fromBytes(dataFromWireshark);

        int wiresharkSSRC = 0xEBCF5603;
        assertEquals(wiresharkSSRC, rtpHeader.getSSRC());

        int wiresharkSequenceNumber = 28414;
        assertEquals(wiresharkSequenceNumber, rtpHeader.getSequenceNumber());

        int wiresharkTimestamp = 72120836;
        assertEquals(wiresharkTimestamp, rtpHeader.getTimestamp());

        int wiresharkPayloadType = 96;
        assertEquals(wiresharkPayloadType, rtpHeader.getPayloadType());
    }

    @Test
    public void compare_derizalization_with_wireshark_data__sample2() throws Exception {

        byte [] dataFromWireshark = new byte[]{
                (byte)0x80, (byte)0x60, (byte)0x01, (byte)0x60, (byte)0x08, (byte)0xc5, (byte)0xe1, (byte)0x41,
                (byte)0x4b, (byte)0x8b, (byte)0x2d, (byte)0xf3, (byte)0x7c, (byte)0x81, (byte)0xe2, (byte)0x23};

        RTPHeader rtpHeader = RTPHeader.fromBytes(dataFromWireshark);

        int wiresharkSSRC = 0x4B8B2DF3;
        assertEquals(wiresharkSSRC, rtpHeader.getSSRC());

        int wiresharkSequenceNumber = 352;
        assertEquals(wiresharkSequenceNumber, rtpHeader.getSequenceNumber());

        int wiresharkTimestamp = 147185985;
        assertEquals(wiresharkTimestamp, rtpHeader.getTimestamp());

        int wiresharkPayloadType = 96;
        assertEquals(wiresharkPayloadType, rtpHeader.getPayloadType());
    }

    @Test
    public void compare_serizalization_with_wireshark_data__sample1() throws Exception {

        boolean wiresharkMarkerBit = true;
        byte wiresharkPayloadType = (byte) 96;
        int wiresharkSequenceNumber = 28414;
        int wiresharkTimestamp = 72120836;
        int wiresharkSSRC = 0x4B8B2DF3;
        RTPHeader rtpHeader1 = new RTPHeader(
                wiresharkMarkerBit,
                wiresharkPayloadType,
                wiresharkSequenceNumber,
                wiresharkTimestamp,
                wiresharkSSRC);

        byte[] expectedEncodedRTPHeader = new byte[]{
                (byte) 0x80, (byte) 0xE0, (byte) 0x6E, (byte) 0xFE, (byte) 0x04, (byte) 0x4C,
                (byte) 0x7A, (byte) 0x04, (byte) 0x4B, (byte) 0x8B, (byte) 0x2D, (byte) 0xF3};

        byte[] resultEncodedRTPHeader = rtpHeader1.toBytes();
        assertArrayEquals(expectedEncodedRTPHeader, resultEncodedRTPHeader);

        RTPHeader rtpHeader2 = RTPHeader.fromBytes(resultEncodedRTPHeader);
        assertEquals(rtpHeader1, rtpHeader2);
    }



}
