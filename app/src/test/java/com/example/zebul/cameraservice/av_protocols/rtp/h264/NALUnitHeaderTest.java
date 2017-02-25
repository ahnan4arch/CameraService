package com.example.zebul.cameraservice.av_protocols.rtp.h264;

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
        NALUnitType nalUnitType = NALUnitType.SubsetSequenceParameterSet;
        NALUnitHeader inputNalUnitHeader = new NALUnitHeader(false, (byte)nalReferenceIndicator, nalUnitType);

        byte encodedNalUnit = inputNalUnitHeader.toByte();
        assertTrue(0<encodedNalUnit);

        NALUnitHeader outputNalUnitHeader = NALUnitHeader.fromByte(encodedNalUnit);
        assertEquals(inputNalUnitHeader, outputNalUnitHeader);
    }
}
