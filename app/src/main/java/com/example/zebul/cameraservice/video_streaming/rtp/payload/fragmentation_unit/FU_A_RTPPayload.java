package com.example.zebul.cameraservice.video_streaming.rtp.payload.fragmentation_unit;

import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitHeaderEncoder;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayload;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayloadType;

/**
 * Created by zebul on 11/26/16.
 */

public class FU_A_RTPPayload extends RTPPayload {

    private FUIndicator fuIndicator;
    private FUHeader fuHeader;
    private  byte[] fragmentData;

    public FU_A_RTPPayload(FUIndicator fuIndicator, FUHeader fuHeader, byte[] fragmentData){

        super(RTPPayloadType.FU_A);
        this.fuIndicator = fuIndicator;
        this.fuHeader = fuHeader;
        this.fragmentData = fragmentData;
    }

    @Override
    public int computeLenght(){

        return FUIndicator.LENGTH+FUHeader.LENGTH+ fragmentData.length;
    }

    public void toBytes(byte[] rtpPacketBytes, int position){

        rtpPacketBytes[position++] = NALUnitHeaderEncoder.encode(fuIndicator);
        rtpPacketBytes[position++] = fuHeader.toByte();
        System.arraycopy(fragmentData, 0,
                rtpPacketBytes, position, fragmentData.length);
    }
}
