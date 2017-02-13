package com.example.zebul.cameraservice.av_streaming.rtp;

/**
 * Created by zebul on 10/24/16.
 */

/*
 0                   1                   2                   3
 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |V=2|P|X|  CC   |M|     PT      |       sequence number         |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |                           timestamp                           |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |           synchronization source (SSRC) identifier            |
 +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
 |            contributing source (CSRC) identifiers             |
 |                             ....                              |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */

public class RTPHeader {

    public static final int LENGTH = 12;
    private boolean markerBit;
    private byte payloadType;
    private int sequenceNumber;
    private int timestamp;
    private int SSRC;

    private RTPHeader(){

    }

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

    /*
     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |V=2|P|X|  CC   |M|     PT      |       sequence number         |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                           timestamp                           |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |           synchronization source (SSRC) identifier            |
     +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+
     |            contributing source (CSRC) identifiers             |
     |                             ....                              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */

    public byte[] toBytes() {

        byte [] rtpHeaderBytes = new byte[RTPHeader.LENGTH];
        toBytes(rtpHeaderBytes, 0);
        return rtpHeaderBytes;
    }

    public void toBytes(byte[] rtpHeaderBytes, int position) {

        int offset = position;
        rtpHeaderBytes[offset] = (byte)0x80;

        rtpHeaderBytes[++offset] = (byte)(markerBit ? 0x80 : 0x00);
        rtpHeaderBytes[offset]  |= (byte)(payloadType&0x7F);

        rtpHeaderBytes[++offset] = (byte)(sequenceNumber>>8);
        rtpHeaderBytes[++offset] = (byte)(sequenceNumber>>0);

        rtpHeaderBytes[++offset] = (byte)(timestamp>>24);
        rtpHeaderBytes[++offset] = (byte)(timestamp>>16);
        rtpHeaderBytes[++offset] = (byte)(timestamp>>8);
        rtpHeaderBytes[++offset] = (byte)(timestamp>>0);

        rtpHeaderBytes[++offset] = (byte)(SSRC>>24);
        rtpHeaderBytes[++offset] = (byte)(SSRC>>16);
        rtpHeaderBytes[++offset] = (byte)(SSRC>>8);
        rtpHeaderBytes[++offset] = (byte)(SSRC>>0);
    }

    public static RTPHeader fromBytes(byte [] rtpHeaderBytes) {

        return fromBytes(rtpHeaderBytes, 0);
    }

    public static RTPHeader fromBytes(byte [] rtpHeaderBytes, int position) {

        RTPHeader rtpHeader = new RTPHeader();

        int offset = position+1;
        rtpHeader.markerBit   = ((rtpHeaderBytes[offset]&0x80)==0x80);
        rtpHeader.payloadType = (byte)(rtpHeaderBytes[offset]&0x7F);

        rtpHeader.sequenceNumber =

                (0x0000FF00&(rtpHeaderBytes[++offset]<<8))|
                (0x000000FF&(rtpHeaderBytes[++offset]<<0));

        rtpHeader.timestamp =
                (0xFF000000&(rtpHeaderBytes[++offset]<<24))|
                (0x00FF0000&(rtpHeaderBytes[++offset]<<16))|
                (0x0000FF00&(rtpHeaderBytes[++offset]<< 8))|
                (0x000000FF&(rtpHeaderBytes[++offset]<< 0));

        rtpHeader.SSRC =
                (0xFF000000&(rtpHeaderBytes[++offset]<<24))|
                (0x00FF0000&(rtpHeaderBytes[++offset]<<16))|
                (0x0000FF00&(rtpHeaderBytes[++offset]<< 8))|
                (0x000000FF&(rtpHeaderBytes[++offset]<< 0));
        return rtpHeader;
    }

    /*
    final static int NUMBER_OF_BYTES_IN_INTEGER = Integer.SIZE/8;
    final static int [] SHIFTS = new int[]{24, 16, 8, 0};

    public static byte[] serialize2(RTPHeader rtpHeader) {

        int VPXCC = 0x80000000;
        int M = rtpHeader.markerBit ? 0x00800000 : 0x00000000;
        int PT = (rtpHeader.payloadType&0x7F)<<16;
        int [] data = new int []{VPXCC|M|PT|rtpHeader.sequenceNumber, rtpHeader.timestamp, rtpHeader.SSRC};

        byte [] serializedRTPHeader = new byte[RTPHeader.LENGTH];
        for(int i=0; i<data.length; i++) {

            for (int j = 0; j < NUMBER_OF_BYTES_IN_INTEGER; j++) {

                serializedRTPHeader[(NUMBER_OF_BYTES_IN_INTEGER * i) + j] = (byte) (data[i] >> SHIFTS[j]);
            }
        }
        return serializedRTPHeader;
    }

    public static RTPHeader deserialize2(byte [] serializedRTPHeader) {

        int [] data = new int [3];
        for(int i=0; i<data.length; i++) {

            for (int j = 0; j < NUMBER_OF_BYTES_IN_INTEGER; j++) {

                data[i] |= (0xFF<< SHIFTS[j])&(serializedRTPHeader[(NUMBER_OF_BYTES_IN_INTEGER * i) + j] << SHIFTS[j]);;
            }
        }

        boolean M = (data[0]&0x00800000)==0x00800000;
        byte PT = (byte)((data[0]>>16)&0x7F);
        int sequenceNumber = data[0]&0x0000FFFF;
        int timestamp = data[1];
        int SSRC = data[2];

        return new RTPHeader(M, PT, sequenceNumber, timestamp, SSRC);
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RTPHeader rtpHeader = (RTPHeader) o;

        if (markerBit != rtpHeader.markerBit) return false;
        if (payloadType != rtpHeader.payloadType) return false;
        if (sequenceNumber != rtpHeader.sequenceNumber) return false;
        if (timestamp != rtpHeader.timestamp) return false;
        return SSRC == rtpHeader.SSRC;

    }

    @Override
    public int hashCode() {
        int result = (markerBit ? 1 : 0);
        result = 31 * result + (int) payloadType;
        result = 31 * result + sequenceNumber;
        result = 31 * result + timestamp;
        result = 31 * result + SSRC;
        return result;
    }

}
