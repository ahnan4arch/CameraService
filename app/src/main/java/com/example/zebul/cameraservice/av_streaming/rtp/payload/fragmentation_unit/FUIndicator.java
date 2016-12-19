package com.example.zebul.cameraservice.av_streaming.rtp.payload.fragmentation_unit;

import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnitHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnitType;

/**
 * Created by zebul on 11/27/16.
 */

public class FUIndicator extends NALUnitHeader {

    public FUIndicator(boolean forbiddenZeroBit, byte nalReferenceIndicator) {
        super(forbiddenZeroBit, nalReferenceIndicator, NALUnitType.FU_A);
    }
}
