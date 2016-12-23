package com.example.zebul.cameraservice.av_streaming.rtp.aac;

import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacket;
import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPackets;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.av_streaming.rtp.basic.RTPPacketizer;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.H264Payload;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.single_time.STAP_A_RTPPayload;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 12/21/16.
 */

public class AACPacketizer extends RTPPacketizer {

    public RTPPackets createRTPPackets(AACPackets aacPackets) {

        RTPPackets rtpPackets = new RTPPackets();
        for(AACPacket aacPacket: aacPackets) {

            rtpPackets.addRTPPackets(createRTPPackets(aacPacket));
        }
        return rtpPackets;
    }

    private List<RTPPacket> createRTPPackets(AACPacket aacPacket) {

        List<RTPPacket> rtpPackets = new LinkedList<RTPPacket>();
        rtpPackets.add(createRTPPacket(aacPacket));
        return rtpPackets;
    }

    private RTPPacket createRTPPacket(AACPacket aacPacket) {

        RTPHeader rtpHeader = createRTPHeader(aacPacket.getTimestamp(), true);
        AACPayload rtpPayload = new AACPayload(aacPacket.getAccessUnit());
        RTPPacket rtpPacket = new RTPPacket(rtpHeader, rtpPayload);
        return rtpPacket;
    }
}
