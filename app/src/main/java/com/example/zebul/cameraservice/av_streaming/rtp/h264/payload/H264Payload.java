package com.example.zebul.cameraservice.av_streaming.rtp.h264.payload;

import com.example.zebul.cameraservice.av_streaming.rtp.RTPPayload;

/**
 * Created by zebul on 11/21/16.
 */

public abstract class H264Payload implements RTPPayload {

    protected H264PayloadType h264PayloadType;

    public H264Payload(H264PayloadType rtpPayloadType){

        this.h264PayloadType = rtpPayloadType;
    }

    public abstract int computeLenght();

    public byte[] toBytes() {

        byte [] rtpPayloadBytes = new byte[computeLenght()];
        toBytes(rtpPayloadBytes, 0);
        return rtpPayloadBytes;
    }

    public H264PayloadType getH264PayloadType() {
        return h264PayloadType;
    }
}
