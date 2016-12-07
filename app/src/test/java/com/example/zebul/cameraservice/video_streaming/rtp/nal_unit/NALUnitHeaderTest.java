package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by zebul on 12/7/16.
 */

public class NALUnitHeaderTest {

    @Test
    public void test_whether_fromByte_reverses_toByte() throws Exception {

        byte nalReferenceIndicator = 2;
        byte nalUnitType = 15;
        NALUnitHeader inputNalUnitHeader = new NALUnitHeader(false, (byte)nalReferenceIndicator, nalUnitType);

        byte encodedNalUnit = inputNalUnitHeader.toByte();
        assertTrue(0<encodedNalUnit);

        NALUnitHeader outputNalUnitHeader = NALUnitHeader.fromByte(encodedNalUnit);
        assertEquals(inputNalUnitHeader, outputNalUnitHeader);
    }
}
