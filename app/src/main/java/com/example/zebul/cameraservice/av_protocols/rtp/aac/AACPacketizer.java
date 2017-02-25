package com.example.zebul.cameraservice.av_protocols.rtp.aac;

import com.example.zebul.cameraservice.av_protocols.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_protocols.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.RTPPackets;
import com.example.zebul.cameraservice.av_protocols.rtp.basic.RTPPacketizer;

/**
 * Created by zebul on 12/21/16.
 */

public class AACPacketizer extends RTPPacketizer {

    public AACPacketizer(int sequenceNumber, int SSRC){

        super(sequenceNumber, SSRC);
    }

    public RTPPackets createRTPPackets(AACPackets aacPackets) {

        RTPPackets rtpPackets = new RTPPackets();
        for(AACPacket aacPacket: aacPackets) {

            rtpPackets.addRTPPackets(createRTPPackets(aacPacket));
        }
        return rtpPackets;
    }

    public RTPPackets createRTPPackets(AACPacket aacPacket) {

        RTPPackets rtpPackets = new RTPPackets();
        rtpPackets.addRTPPacket(createRTPPacket(aacPacket));
        return rtpPackets;
    }

    private RTPPacket createRTPPacket(AACPacket aacPacket) {

        RTPHeader rtpHeader = createRTPHeader(aacPacket.getTimestamp(), true);
        AACPayload rtpPayload = new AACPayload(aacPacket.getAccessUnit());
        RTPPacket rtpPacket = new RTPPacket(rtpHeader, rtpPayload);
        return rtpPacket;
    }
}
