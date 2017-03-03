package com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.fragmentation_unit;

import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.H264Payload;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.H264PayloadType;

/**
 * Created by zebul on 11/26/16.
 */

public class FU_B_RTPPayload extends H264Payload {

    private byte [] nalUnitFragment;

    public FU_B_RTPPayload(byte [] nalUnitFragment) {
        super(H264PayloadType.FU_A);
        nalUnitFragment = nalUnitFragment;
    }

    @Override
    public int computeLenght() {
        return 1+nalUnitFragment.length;
    }

    @Override
    public void toBytes(byte[] rtpPacketBytes, int position) {

    }
}
