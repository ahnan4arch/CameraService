package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by zebul on 10/27/16.
 */
public class NALUnitHeaderEncoderTest {

    @Test
    public void when_received_from_vlc_then_ble() throws Exception {

        byte NALReferenceIndicator = 2;
        byte NALUnitType = 15;
        NALUnitHeader inputNalUnitHeader = new NALUnitHeader(false, (byte)NALReferenceIndicator, NALUnitType);
        byte encodedNalUnit = NALUnitHeaderEncoder.encode(inputNalUnitHeader);
        assertTrue(0<encodedNalUnit);

        NALUnitHeader outputNalUnitHeader = NALUnitHeaderDecoder.decode(encodedNalUnit);
        assertEquals(inputNalUnitHeader, outputNalUnitHeader);
    }
}
