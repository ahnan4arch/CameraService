package com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.fragmentation_unit;

import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.H264Payload;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.H264PayloadType;

/**
 * Created by zebul on 11/26/16.
 */

public class FU_A_RTPPayload extends H264Payload {

    private FUIndicator fuIndicator;
    private FUHeader fuHeader;

    private byte[] fragmentData;

    public FU_A_RTPPayload(FUIndicator fuIndicator, FUHeader fuHeader, byte[] fragmentData){

        super(H264PayloadType.FU_A);
        this.fuIndicator = fuIndicator;
        this.fuHeader = fuHeader;
        this.fragmentData = fragmentData;
    }

    @Override
    public int computeLenght(){

        return FUIndicator.LENGTH+FUHeader.LENGTH+ fragmentData.length;
    }

    public void toBytes(byte[] rtpPacketBytes, int position){

        rtpPacketBytes[position++] = fuIndicator.toByte();
        rtpPacketBytes[position++] = fuHeader.toByte();
        System.arraycopy(fragmentData, 0,
                rtpPacketBytes, position, fragmentData.length);
    }

    public static FU_A_RTPPayload fromBytes(byte[] bytesOfRTPPacket, int position, int length) {

        FUIndicator fuIndicator = FUIndicator.fromByte(bytesOfRTPPacket[position++]);
        FUHeader fuHeader = FUHeader.fromByte(bytesOfRTPPacket[position++]);
        byte[] fragmentData = new byte[length-2];
        System.arraycopy(bytesOfRTPPacket, position,
                fragmentData, 0, fragmentData.length);

        return new FU_A_RTPPayload(fuIndicator, fuHeader, fragmentData);
    }

    public FUIndicator getFuIndicator() {
        return fuIndicator;
    }

    public FUHeader getFuHeader() {
        return fuHeader;
    }


    public int getFragmentDataLenght() {
        return fragmentData.length;
    }

    public byte [] getFragmentData() {
        return fragmentData;
    }
}
