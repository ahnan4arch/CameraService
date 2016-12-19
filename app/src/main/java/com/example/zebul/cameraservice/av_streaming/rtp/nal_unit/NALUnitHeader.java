package com.example.zebul.cameraservice.av_streaming.rtp.nal_unit;

/**
 * Created by zebul on 10/27/16.
 +---------------+
 |0|1|2|3|4|5|6|7|
 +-+-+-+-+-+-+-+-+
 |F|NRI|  Type   |
 +---------------+
 */

public class NALUnitHeader {

    public static final int LENGTH = 1;
    private boolean forbiddenZeroBit;
    private byte nalReferenceIndicator;
    private NALUnitType nalUnitType;

    public NALUnitHeader(boolean forbiddenZeroBit,
                         byte nalReferenceIndicator,
                         NALUnitType nalUnitType){

        this.forbiddenZeroBit = forbiddenZeroBit;
        this.nalReferenceIndicator = nalReferenceIndicator;
        this.nalUnitType = nalUnitType;
    }

    public byte toByte(){

        byte encodedNALUnit = 0;
        encodedNALUnit |= (byte) (forbiddenZeroBit ? 0b10000000 : 0b00000000);
        encodedNALUnit |= (byte) (nalReferenceIndicator <<5);
        encodedNALUnit |= nalUnitType.toByte();
        return encodedNALUnit;
    }

    public static NALUnitHeader fromByte(byte rtpPacketByte) {

        boolean F = ((rtpPacketByte&0b10000000)==0b10000000);
        byte NRI =  (byte)((rtpPacketByte>>5)&0x03);
        byte Type =  (byte)(rtpPacketByte&0x1F);

        NALUnitType nalUnitType = NALUnitType.NAL_UNIT_TYPES[Type];
        NALUnitHeader nalUnitHeader = new NALUnitHeader(F, NRI, nalUnitType);
        return nalUnitHeader;
    }

    public boolean getForbiddenZeroBit(){

        return forbiddenZeroBit;
    }

    public byte getNalReferenceIndicator(){

        return nalReferenceIndicator;
    }

    public NALUnitType getNalUnitType(){

        return nalUnitType;
    }

}
