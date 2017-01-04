package com.example.zebul.cameraservice.av_streaming.rtp.basic;

import com.example.zebul.cameraservice.av_streaming.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;

import java.util.Random;

/**
 * Created by zebul on 12/20/16.
 */

public abstract class RTPPacketizer {

    public static int MAXIMUM_TRANSMISSION_UNIT = 1500;//MTU
    protected int sequenceNumber;
    protected int SSRC;
    protected int maxPayloadLength = MAXIMUM_TRANSMISSION_UNIT/2;

    protected RTPPacketizer(int sequenceNumber, int SSRC){

        this.sequenceNumber = sequenceNumber;
        this.SSRC = SSRC;
    }

    protected RTPPacketizer(){

        this(0, new Random().nextInt());
    }

    public static int generateRandomInt(){

        return new Random().nextInt();
    }

    protected RTPHeader createRTPHeader(Timestamp timestamp, boolean isLast){

        boolean markerBit = isLast ? true : false;///???????????????
        byte payloadType = (byte) 96;
        RTPHeader rtpHeader = new RTPHeader(
                markerBit,
                payloadType,
                sequenceNumber++,
                (int)timestamp.getTimestampInMillis(),
                SSRC);
        return rtpHeader;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
    }

}
