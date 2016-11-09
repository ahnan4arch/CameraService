package com.example.zebul.cameraservice.video_streaming.rtp.header;

/**
 * Created by zebul on 10/24/16.
 */
public class RTPHeader {

    public static final int LENGTH = 12;
    private boolean markerBit;
    private byte payloadType;
    private int sequenceNumber;
    private int timestamp;
    private int SSRC;

    public RTPHeader(boolean markerBit, byte payloadType, int sequenceNumber,
                     int timestamp, int SSRC){

        this.markerBit = markerBit;
        this.payloadType = payloadType;
        this.sequenceNumber = sequenceNumber;
        this.timestamp = timestamp;
        this.SSRC = SSRC;
    }

    public boolean getMarkerBit() {
        return markerBit;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public byte getPayloadType() {
        return payloadType;
    }

    public int getSSRC() {
        return SSRC;
    }
}
