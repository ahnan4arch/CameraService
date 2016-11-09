package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

/**
 * Created by zebul on 10/27/16.

+---------------+
|0|1|2|3|4|5|6|7|
+-+-+-+-+-+-+-+-+
|F|NRI|  Type   |
+---------------+

*/


public class NALUnitHeaderDecoder {

    public static NALUnitHeader decode(byte encodedNALUnit) {

        boolean F = ((encodedNALUnit&0b10000000)==0b10000000);

        byte NRI =  (byte)((encodedNALUnit>>5)&0x03);

        byte Type =  (byte)(encodedNALUnit&0x1F);
        NALUnitHeader nalUnitHeader = new NALUnitHeader(F, NRI, Type);
        return nalUnitHeader;
    }
}
