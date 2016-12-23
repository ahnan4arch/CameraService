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

    public List<RTPPacket> createRTPPackets(H264Packet h264packet){

        NALUnit nalUnit = h264packet.getNALUnit();
        byte [] nalUnitData = nalUnit.getData();
        if(nalUnitData.length < maxPayloadLength){

            List<RTPPacket> rtpPackets = new LinkedList<RTPPacket>();
            if(nalUnitData.length <= 4){
                return rtpPackets;
            }
            RTPPacket rtpPacket = createSingleTimeNALUnitPacket(h264packet);
            rtpPackets.add(rtpPacket);
            return rtpPackets;
        }
        else{

            return createFragmentationUnitPackets(h264packet);
        }
    }

    private RTPPacket createSingleTimeNALUnitPacket(H264Packet h264packet) {

        RTPHeader rtpHeader = createRTPHeader(h264packet.getTimestamp(), true);
        H264Payload rtpPayload = new STAP_A_RTPPayload(h264packet.getNALUnit());
        RTPPacket rtpPacket = new RTPPacket(rtpHeader, rtpPayload);
        return rtpPacket;
    }

    private List<RTPPacket> createFragmentationUnitPackets(H264Packet h264packet) {

        List<RTPPacket> rtpPackets = new LinkedList<RTPPacket>();

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
            rtpPackets.add(rtpPacket);

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
