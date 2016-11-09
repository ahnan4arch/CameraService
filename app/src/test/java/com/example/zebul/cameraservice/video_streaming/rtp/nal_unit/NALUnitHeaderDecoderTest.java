package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by zebul on 10/27/16.
 */
public class NALUnitHeaderDecoderTest {

    @Test
    public void when_received_from_vlc_then_ble() throws Exception {

        byte [] data = new byte[]{
            (byte)0x80, (byte)0x60, (byte)0x6C, (byte)0x9A, (byte)0x05, (byte)0xE3, (byte)0x83, (byte)0x1B,
            (byte)0x77, (byte)0xF2, (byte)0x7C, (byte)0x40, (byte)0x7C, (byte)0x81, (byte)0xE2, (byte)0xE1};

        int offset = 12;
        NALUnitHeader nalUnitHeader = NALUnitHeaderDecoder.decode(data[offset]);
        assertNotNull(nalUnitHeader);
    }

    @Test
    public void when_received_from_spydroid_then_ble() throws Exception {

        byte [] data = new byte[]{
                (byte)0x80, (byte)0x60, (byte)0x6C, (byte)0x9A, (byte)0x05, (byte)0xE3, (byte)0x83, (byte)0x1B,
                (byte)0x77, (byte)0xF2, (byte)0x7C, (byte)0x40, (byte)0x7C, (byte)0x81, (byte)0xE2, (byte)0xE1};

        int offset = 12;
        NALUnitHeader nalUnitHeader = NALUnitHeaderDecoder.decode(data[offset]);
        assertNotNull(nalUnitHeader);
    }

    @Test
    public void test1() throws Exception {

        NALUnitHeader nalUnitHeader = NALUnitHeaderDecoder.decode((byte)0x21);
        assertNotNull(nalUnitHeader);
    }
}
