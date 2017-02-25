package com.example.zebul.cameraservice.av_protocols.rtp.aac;

import com.example.zebul.cameraservice.av_protocols.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;

/**
 * Created by zebul on 2/8/17.
 */

public class AACDepacketizer {

    public AACPackets createAACPackets(byte[] bytesOfRTPPacket) {

        if(bytesOfRTPPacket.length < (RTPHeader.LENGTH+AACPayload.HEADER_LENGHT+2)){
            return null;
        }
        AACPackets aacPackets = new AACPackets();
        RTPHeader rtpHeader = RTPHeader.fromBytes(bytesOfRTPPacket);
        AACPayload aacPayload = AACPayload.fromBytes(bytesOfRTPPacket, RTPHeader.LENGTH);
        if(aacPayload != null){

            final AccessUnit accessUnit = aacPayload.getAccessUnit();
            final Timestamp timestamp = new Timestamp(rtpHeader.getTimestamp());
            aacPackets.addPacket(new AACPacket(accessUnit, timestamp));
        }
        return aacPackets;
    }
}
