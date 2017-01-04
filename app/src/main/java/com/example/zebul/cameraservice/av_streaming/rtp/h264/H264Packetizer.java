package com.example.zebul.cameraservice.av_streaming.rtp.h264;

import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264Packet;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264Packets;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.basic.RTPPacketizer;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.H264Payload;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.H264PayloadType;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.fragmentation_unit.FUHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.fragmentation_unit.FUIndicator;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.fragmentation_unit.FU_A_RTPPayload;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.single_time.STAP_A_RTPPayload;
import com.example.zebul.cameraservice.av_streaming.av_packet.basic.DataPacket;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 11/20/16.
 */

public class H264Packetizer extends RTPPacketizer {

    private final String TAG = H264Packetizer.class.getSimpleName();

    public static int FU_INDICATOR_LENGTH = 1;
    public static int FU_HEADER_LENGTH = 1;

    public H264Packetizer(){

        super();
    }

    public H264Packetizer(int sequenceNumber, int SSRC){

        super(sequenceNumber, SSRC);
    }

    public RTPPackets createRTPPackets(H264Packets h264packets) {

        RTPPackets rtpPackets = new RTPPackets();
        for(H264Packet h264packet: h264packets) {

            rtpPackets.addRTPPackets(createRTPPackets(h264packet));
        }
        return rtpPackets;
    }

    public RTPPackets createRTPPackets(H264Packet h264Packet){

        NALUnit nalUnit = h264Packet.getNALUnit();
        byte [] nalUnitData = nalUnit.getData();
        if(nalUnitData.length < maxPayloadLength){

            RTPPackets rtpPackets = new RTPPackets();
            if(nalUnitData.length <= 4){
                return rtpPackets;
            }
            RTPPacket rtpPacket = createSingleTimeNALUnitPacket(h264Packet);
            rtpPackets.addRTPPacket(rtpPacket);
            return rtpPackets;
        }
        else{

            return createFragmentationUnitPackets(h264Packet);
        }
    }

    private RTPPacket createSingleTimeNALUnitPacket(H264Packet h264Packet) {

        RTPHeader rtpHeader = createRTPHeader(h264Packet.getTimestamp(), true);
        H264Payload rtpPayload = new STAP_A_RTPPayload(h264Packet.getNALUnit());
        RTPPacket rtpPacket = new RTPPacket(rtpHeader, rtpPayload);
        return rtpPacket;
    }

    private RTPPackets createFragmentationUnitPackets(H264Packet h264packet) {

        RTPPackets rtpPackets = new RTPPackets();

        int offsetOfNALUnit = NALUnit.START_CODES.length;
        int offsetOfDataInNalUnit = offsetOfNALUnit+1;//omit NALUnit header
        boolean start = true;
        boolean end = false;

        byte [] dataWithNalUnit = h264packet.getNALUnit().getData();
        NALUnitHeader nalUnitHeader = NALUnitHeader.fromByte(dataWithNalUnit[offsetOfNALUnit]);
        FUIndicator fuIndicator = new FUIndicator(false, nalUnitHeader.getNalReferenceIndicator());
        while(true){

            int payloadFragmentDataLength = 0;
            if(offsetOfDataInNalUnit+ maxPayloadLength < dataWithNalUnit.length){

                payloadFragmentDataLength = maxPayloadLength;
                end = false;
            }
            else{

                payloadFragmentDataLength = dataWithNalUnit.length-offsetOfDataInNalUnit;
                end = true;
            }

            byte [] payloadFragmentData = new byte[payloadFragmentDataLength];
            System.arraycopy(dataWithNalUnit, offsetOfDataInNalUnit,
                    payloadFragmentData, 0, payloadFragmentDataLength);

            NALUnitType nalUnitType = nalUnitHeader.getNALUnitType();

            FUHeader fuHeader = new FUHeader(start, end, nalUnitType);
            if(start){
                start = false;
            }

            RTPHeader rtpHeader = createRTPHeader(h264packet.getTimestamp(), end);
            H264Payload rtpPayload = new FU_A_RTPPayload(fuIndicator, fuHeader, payloadFragmentData);
            RTPPacket rtpPacket = new RTPPacket(rtpHeader, rtpPayload);
            rtpPackets.addRTPPacket(rtpPacket);

            if(end){
                break;
            }
            else{
                offsetOfDataInNalUnit+=payloadFragmentDataLength;
            }
        }

        return rtpPackets;
    }

    private H264PayloadType selectRTPPayloadTypeFor(DataPacket avPacket) {

        return null;
    }
}
