package com.example.zebul.cameraservice.video_streaming.rtp.payload.fragmentation_unit;

import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayload;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayloadType;

/**
 * Created by zebul on 11/26/16.
 */

public class FU_B_RTPPayload extends RTPPayload {

    private byte [] nalUnitFragment;

    public FU_B_RTPPayload(byte [] nalUnitFragment) {
        super(RTPPayloadType.FU_A);
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
