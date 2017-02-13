package com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.single_time;

import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.H264Payload;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.H264PayloadType;

/**
 * Created by zebul on 11/23/16.
 */

public class SingleNALUnit_RTPPayload extends H264Payload {

    private NALUnit nalUnit;
    public SingleNALUnit_RTPPayload(NALUnit nalUnit){

        super(H264PayloadType.NALUnit);
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

    public static SingleNALUnit_RTPPayload fromBytes(byte[] rtpPacketBytes, int position, int length){

        byte [] nalUnitData = new byte[NALUnit.START_CODES.length+length];
        System.arraycopy(NALUnit.START_CODES, 0, nalUnitData, 0, NALUnit.START_CODES.length);
        System.arraycopy(rtpPacketBytes, position, nalUnitData, NALUnit.START_CODES.length, length);
        NALUnit nalUnit = new NALUnit(nalUnitData);
        return new SingleNALUnit_RTPPayload(nalUnit);
    }

    public NALUnit getNalUnit() {
        return nalUnit;
    }
}
