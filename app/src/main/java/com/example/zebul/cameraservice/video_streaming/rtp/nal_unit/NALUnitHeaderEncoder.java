package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

/**
 * Created by zebul on 10/27/16.

+---------------+
|0|1|2|3|4|5|6|7|
+-+-+-+-+-+-+-+-+
|F|NRI|  Type   |
+---------------+

*/


public class NALUnitHeaderEncoder {

    public static byte encode(NALUnitHeader nalUnitHeader) {

        byte encodedNALUnit = 0;
        encodedNALUnit |= (byte) (nalUnitHeader.getForbiddenZeroBit() ? 0b10000000 : 0b00000000);
        encodedNALUnit |= (byte) (nalUnitHeader.getNALReferenceIndicator()<<5);
        encodedNALUnit |= (byte) nalUnitHeader.getNALUnitType();
        return encodedNALUnit;
    }
}
