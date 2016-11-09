package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

/**
 * Created by zebul on 10/27/16.
 */
public class NALUnitHeader {

    private boolean forbiddenZeroBit;
    private byte NALReferenceIndicator;
    private byte NALUnitType;

    public NALUnitHeader(boolean forbiddenZeroBit, byte NALReferenceIndicator, byte NALUnitType){
        this.forbiddenZeroBit = forbiddenZeroBit;
        this.NALReferenceIndicator = NALReferenceIndicator;
        this.NALUnitType = NALUnitType;
    }

    public boolean getForbiddenZeroBit(){

        return forbiddenZeroBit;
    }

    public byte getNALReferenceIndicator(){

        return NALReferenceIndicator;
    }

    public byte getNALUnitType(){

        return NALUnitType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NALUnitHeader that = (NALUnitHeader) o;

        if (forbiddenZeroBit != that.forbiddenZeroBit) return false;
        if (NALReferenceIndicator != that.NALReferenceIndicator) return false;
        return NALUnitType == that.NALUnitType;

    }

    @Override
    public int hashCode() {
        int result = (forbiddenZeroBit ? 1 : 0);
        result = 31 * result + (int) NALReferenceIndicator;
        result = 31 * result + (int) NALUnitType;
        return result;
    }
}
