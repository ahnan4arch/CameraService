package com.example.zebul.cameraservice.video_streaming.rtp.header;

/**
 * Created by zebul on 10/24/16.

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

public class RTPHeaderDecoder {
    public static RTPHeader decode(byte[] encodedRTPHeader) {

        int offset = 1;
        boolean M = ((encodedRTPHeader[offset]&0x80)==0x80);
        byte PT = (byte)(encodedRTPHeader[offset]&0x7F);

        int sequenceNumber =
            (0x0000FF00&(encodedRTPHeader[++offset]<<8))|
            (0x000000FF&(encodedRTPHeader[++offset]<<0));

        int timestamp =
            (0xFF000000&(encodedRTPHeader[++offset]<<24))|
            (0x00FF0000&(encodedRTPHeader[++offset]<<16))|
            (0x0000FF00&(encodedRTPHeader[++offset]<< 8))|
            (0x000000FF&(encodedRTPHeader[++offset]<< 0));

        int SSRC =
            (0xFF000000&(encodedRTPHeader[++offset]<<24))|
            (0x00FF0000&(encodedRTPHeader[++offset]<<16))|
            (0x0000FF00&(encodedRTPHeader[++offset]<< 8))|
            (0x000000FF&(encodedRTPHeader[++offset]<< 0));

        return new RTPHeader(M, PT, sequenceNumber, timestamp, (int)SSRC);
    }
}
