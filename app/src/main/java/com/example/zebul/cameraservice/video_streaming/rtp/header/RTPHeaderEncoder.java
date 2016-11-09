package com.example.zebul.cameraservice.video_streaming.rtp.header;

/**
 * Created by zebul on 10/24/16.
 */
public class RTPHeaderEncoder {

    public static byte[] encode(RTPHeader rtpHeader) {

        byte [] encodedRTPHeader = new byte[RTPHeader.LENGTH];

        int offset = 0;
        encodedRTPHeader[offset] = (byte)0x80;

        boolean M = rtpHeader.getMarkerBit();
        encodedRTPHeader[++offset] = (byte)(M ? 0x80 : 0x00);
        byte PT = rtpHeader.getPayloadType();
        encodedRTPHeader[offset] |=(byte)(PT&0x7F);

        int sequenceNumber = rtpHeader.getSequenceNumber();
        encodedRTPHeader[++offset] = (byte)(sequenceNumber>>8);
        encodedRTPHeader[++offset] = (byte)(sequenceNumber>>0);

        int timestamp = rtpHeader.getTimestamp();
        encodedRTPHeader[++offset] = (byte)(timestamp>>24);
        encodedRTPHeader[++offset] = (byte)(timestamp>>16);
        encodedRTPHeader[++offset] = (byte)(timestamp>>8);
        encodedRTPHeader[++offset] = (byte)(timestamp>>0);

        int SSRC = rtpHeader.getSSRC();
        encodedRTPHeader[++offset] = (byte)(SSRC>>24);
        encodedRTPHeader[++offset] = (byte)(SSRC>>16);
        encodedRTPHeader[++offset] = (byte)(SSRC>>8);
        encodedRTPHeader[++offset] = (byte)(SSRC>>0);

        return encodedRTPHeader;
    }
}
