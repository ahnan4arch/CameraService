package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

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
    private byte nalUnitType;

    public NALUnitHeader(boolean forbiddenZeroBit, byte nalReferenceIndicator, byte nalUnitType){
        this.forbiddenZeroBit = forbiddenZeroBit;
        this.nalReferenceIndicator = nalReferenceIndicator;
        this.nalUnitType = nalUnitType;
    }

    public byte toByte(){

        byte encodedNALUnit = 0;
        encodedNALUnit |= (byte) (forbiddenZeroBit ? 0b10000000 : 0b00000000);
        encodedNALUnit |= (byte) (nalReferenceIndicator <<5);
        encodedNALUnit |= (byte) nalUnitType;
        return encodedNALUnit;
    }

    public static NALUnitHeader fromByte(byte rtpPacketByte) {

        boolean F = ((rtpPacketByte&0b10000000)==0b10000000);
        byte NRI =  (byte)((rtpPacketByte>>5)&0x03);
        byte Type =  (byte)(rtpPacketByte&0x1F);
        NALUnitHeader nalUnitHeader = new NALUnitHeader(F, NRI, Type);
        return nalUnitHeader;
    }

    public boolean getForbiddenZeroBit(){

        return forbiddenZeroBit;
    }

    public byte getNalReferenceIndicator(){

        return nalReferenceIndicator;
    }

    public byte getNalUnitType(){

        return nalUnitType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NALUnitHeader that = (NALUnitHeader) o;

        if (forbiddenZeroBit != that.forbiddenZeroBit) return false;
        if (nalReferenceIndicator != that.nalReferenceIndicator) return false;
        return nalUnitType == that.nalUnitType;

    }

    @Override
    public int hashCode() {
        int result = (forbiddenZeroBit ? 1 : 0);
        result = 31 * result + (int) nalReferenceIndicator;
        result = 31 * result + (int) nalUnitType;
        return result;
    }
}
