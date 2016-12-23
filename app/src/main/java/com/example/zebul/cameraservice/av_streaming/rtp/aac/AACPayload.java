package com.example.zebul.cameraservice.av_streaming.rtp.aac;

import com.example.zebul.cameraservice.av_streaming.rtp.RTPPayload;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;

/**
 * Created by zebul on 12/21/16.
 */

public class AACPayload implements RTPPayload{

    private final AccessUnit accessUnit;

    public AACPayload(AccessUnit accessUnit) {

        this.accessUnit = accessUnit;
    }

    @Override
    public void toBytes(byte[] rtpPacketBytes, int position) {

        byte [] accessUnitData = accessUnit.getData();

        int offset = position;
        rtpPacketBytes[offset++] = 0;
        rtpPacketBytes[offset++] = 0x10;
        rtpPacketBytes[offset++] = (byte) (((int) accessUnitData.length & 0x1FE0) >> 5);
        rtpPacketBytes[offset++] = (byte) (((int) accessUnitData.length & 0x001F) << 3);

        System.arraycopy(accessUnitData, 0, rtpPacketBytes, offset, accessUnitData.length);
    }

    @Override
    public int computeLenght() {
        return accessUnit.getData().length+4;
    }
}
