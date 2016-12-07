package com.example.zebul.cameraservice.video_streaming.rtp;

import com.example.zebul.cameraservice.video_streaming.rtp.header.RTPHeader;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayload;

/**
 * Created by zebul on 11/21/16.
 */

public class RTPPacket {

    private RTPHeader rtpHeader;
    private RTPPayload rtpPayload;

    public RTPPacket(
            RTPHeader rtpHeader,
            RTPPayload rtpPayload){

        this.rtpHeader = rtpHeader;
        this.rtpPayload= rtpPayload;
    }

    public byte [] toBytes(){

        int length = RTPHeader.LENGTH;
        length += rtpPayload.computeLenght();
        byte [] rtpPacketBytes = new byte[length];
        rtpHeader.toBytes(rtpPacketBytes, 0);
        rtpPayload.toBytes(rtpPacketBytes, RTPHeader.LENGTH);
        return rtpPacketBytes;
    }
}
