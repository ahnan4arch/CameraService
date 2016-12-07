package com.example.zebul.cameraservice.video_streaming.rtp.payload.single_time;

import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnit;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayload;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayloadType;

/**
 * Created by zebul on 11/23/16.
 */

public class STAP_A_RTPPayload extends RTPPayload {

    private NALUnit nalUnit;
    public STAP_A_RTPPayload(NALUnit nalUnit){

        super(RTPPayloadType.NALUnit);
        this.nalUnit = nalUnit;
    }

    @Override
    public int computeLenght(){

        return (nalUnit.getData().length-NALUnit.START_CODES.length);
    }

    public void toBytes(byte[] rtpPacketBytes, int position){

        byte [] nalUnitData = nalUnit.getData();
        System.arraycopy(nalUnitData, NALUnit.START_CODES.length,
                rtpPacketBytes, position, nalUnitData.length-NALUnit.START_CODES.length);
    }
}
