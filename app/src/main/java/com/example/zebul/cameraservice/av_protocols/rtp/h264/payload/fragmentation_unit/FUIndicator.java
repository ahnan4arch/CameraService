package com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.fragmentation_unit;

import com.example.zebul.cameraservice.av_protocols.rtp.h264.NALUnitHeader;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.NALUnitType;

/**
 * Created by zebul on 11/27/16.
 */

public class FUIndicator extends NALUnitHeader {

    public FUIndicator(boolean forbiddenZeroBit, byte nalReferenceIndicator) {
        super(forbiddenZeroBit, nalReferenceIndicator, NALUnitType.FU_A);
    }

    public static FUIndicator fromByte(byte rtpPacketByte) {

        NALUnitHeader nalUnitHeader = NALUnitHeader.fromByte(rtpPacketByte);
        return new FUIndicator(nalUnitHeader.getForbiddenZeroBit(),
                nalUnitHeader.getNalReferenceIndicator());
    }
}
