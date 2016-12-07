package com.example.zebul.cameraservice.video_streaming.rtp.payload;

import com.example.zebul.cameraservice.video_streaming.rtp.header.RTPHeader;

/**
 * Created by zebul on 11/21/16.
 */

public abstract class RTPPayload {

    protected RTPPayloadType rtpPayloadType;

    public RTPPayload(RTPPayloadType rtpPayloadType){

        this.rtpPayloadType = rtpPayloadType;
    }

    public abstract int computeLenght();

    public byte[] toBytes() {

        byte [] rtpPayloadBytes = new byte[computeLenght()];
        toBytes(rtpPayloadBytes, 0);
        return rtpPayloadBytes;
    }
    public abstract void toBytes(byte[] rtpPacketBytes, int position);
}
