package com.example.zebul.cameraservice.av_protocols.rtp;

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

        int length = RTPHeader.LENGTH + rtpPayload.computeLenght();
        byte [] rtpPacketBytes = new byte[length];
        rtpHeader.toBytes(rtpPacketBytes, 0);
        rtpPayload.toBytes(rtpPacketBytes, RTPHeader.LENGTH);
        return rtpPacketBytes;
    }

    public RTPHeader getRtpHeader() {
        return rtpHeader;
    }

    public RTPPayload getRtpPayload() {
        return rtpPayload;
    }

}
