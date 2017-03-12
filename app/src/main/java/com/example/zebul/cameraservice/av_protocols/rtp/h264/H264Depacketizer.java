package com.example.zebul.cameraservice.av_protocols.rtp.h264;

import com.example.zebul.cameraservice.av_protocols.rtp.BytesOfRTPPackets;
import com.example.zebul.cameraservice.av_protocols.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_protocols.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.RTPPayload;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.H264Payload;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.fragmentation_unit.FUHeader;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.fragmentation_unit.FUIndicator;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.fragmentation_unit.FU_A_RTPPayload;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.payload.single_time.SingleNALUnit_RTPPayload;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 1/30/17.
 */

public class H264Depacketizer {

    private int lastSequenceNumber = -1;
    private List<RTPPacket> fuRTPPackets = new LinkedList<RTPPacket>();

    public H264Packets createH264Packets(BytesOfRTPPackets bytesOfRTPPackets){

        H264Packets allH264Packets = new H264Packets();
        for(byte [] bytesOfRTPPacket: bytesOfRTPPackets){

            H264Packets h264Packets = createH264Packets(bytesOfRTPPacket);
            if(h264Packets != null){
                allH264Packets.addPackets(h264Packets);
            }
        }
        return allH264Packets;
    }

    public H264Packets createH264Packets(byte[] bytesOfRTPPacket) {

        if(bytesOfRTPPacket.length < (RTPHeader.LENGTH+2)){
            return null;
        }
        RTPHeader rtpHeader = RTPHeader.fromBytes(bytesOfRTPPacket);
        byte nalUnitHeaderByte = bytesOfRTPPacket[RTPHeader.LENGTH];
        NALUnitHeader nalUnitHeader = NALUnitHeader.fromByte(nalUnitHeaderByte);

        Depacketizer depacketizer = null;
        switch(nalUnitHeader.getNALUnitType()){

            case STAP_A:
                break;
            case STAP_B:
                break;
            case MTAP16:
                break;
            case MTAP24:
                break;
            case FU_A:
                depacketizer = new FU_A_Depacketizer();
                break;
            case FU_B:
                break;
            default:
                depacketizer = new SingleNALUnitDepacketizer();
                break;
        }
        if(depacketizer == null){
            return null;
        }
        return depacketizer.depacketize(rtpHeader, bytesOfRTPPacket, RTPHeader.LENGTH);
    }

    private interface Depacketizer{

        H264Packets depacketize(RTPHeader rtpHeader,
                                byte[] bytesOfRTPPacket,
                                int payloadStartPositionInRTPPacket);
    }

    private class SingleNALUnitDepacketizer implements Depacketizer {

        @Override
        public H264Packets depacketize(RTPHeader rtpHeader,
                                       byte[] bytesOfRTPPacket,
                                       int payloadStartPositionInRTPPacket) {

            int payloadLength = bytesOfRTPPacket.length-payloadStartPositionInRTPPacket;
            SingleNALUnit_RTPPayload singleNALUnit = SingleNALUnit_RTPPayload.fromBytes(
                    bytesOfRTPPacket, payloadStartPositionInRTPPacket, payloadLength);

            NALUnit nalUnit = singleNALUnit.getNalUnit();
            Timestamp timestamp = new Timestamp(rtpHeader.getTimestamp());
            H264Packets h264Packets = new H264Packets();
            h264Packets.addPacket(new H264Packet(nalUnit, timestamp));
            return h264Packets;
        }
    }

    private class FU_A_Depacketizer implements Depacketizer{

        @Override
        public H264Packets depacketize(RTPHeader rtpHeader,
                                       byte[] bytesOfRTPPacket,
                                       int payloadStartPositionInRTPPacket) {

            int payloadLength = bytesOfRTPPacket.length-payloadStartPositionInRTPPacket;
            final FU_A_RTPPayload fragmentationUnitPayload = FU_A_RTPPayload.fromBytes(
                    bytesOfRTPPacket, payloadStartPositionInRTPPacket, payloadLength);

            RTPPacket rtpPacket = new RTPPacket(rtpHeader, fragmentationUnitPayload);
            final FUHeader fuHeader = fragmentationUnitPayload.getFuHeader();
            if(fuHeader.isEnd()){
                fuRTPPackets.add(rtpPacket);
                sortBySequenceNumbers(fuRTPPackets);
                if(hasContinousSequenceNumbers(fuRTPPackets) && timestampsInEveryPacketAreEqual(fuRTPPackets)){

                    final H264Packet h264Packet = createH264PacketFromFuRtpPackets(fuRTPPackets);
                    fuRTPPackets.clear();
                    H264Packets h264Packets = new H264Packets();
                    h264Packets.addPacket(h264Packet);
                    return h264Packets;
                }
                else{
                    fuRTPPackets.clear();
                }
            }
            else if(fuHeader.isStart()){
                fuRTPPackets.clear();
                fuRTPPackets.add(rtpPacket);
            }
            else{
                fuRTPPackets.add(rtpPacket);
            }
            return null;
        }
    }

    private static void sortBySequenceNumbers(List<RTPPacket> fuRTPPackets) {

        Collections.sort(fuRTPPackets, new Comparator<RTPPacket>(){

            @Override
            public int compare(RTPPacket lhs, RTPPacket rhs) {
                final RTPHeader lRtpHeader = lhs.getRtpHeader();
                final RTPHeader rRtpHeader = rhs.getRtpHeader();
                if(lRtpHeader.getSequenceNumber()<rRtpHeader.getSequenceNumber()){
                    return -1;
                }
                else if(lRtpHeader.getSequenceNumber()==rRtpHeader.getSequenceNumber()){
                    return 0;
                }
                else{
                    return 1;
                }
            }
        });
    }

    private static boolean hasContinousSequenceNumbers(List<RTPPacket> fuRTPPackets) {

        int expectedSequenceNumber = fuRTPPackets.get(0).getRtpHeader().getSequenceNumber();
        for(RTPPacket rtpPacket: fuRTPPackets){

            if(rtpPacket.getRtpHeader().getSequenceNumber() == expectedSequenceNumber){
                expectedSequenceNumber++;
            }
            else{
                return false;
            }
        }
        return true;
    }

    private boolean timestampsInEveryPacketAreEqual(List<RTPPacket> fuRTPPackets) {
        return true;
    }

    private static H264Packet createH264PacketFromFuRtpPackets(List<RTPPacket> fuRTPPackets) {

        //compute lenght of NALUnit
        int nalUnitLength = NALUnit.START_CODES.length + NALUnitHeader.LENGTH;
        for(RTPPacket rtpPacket: fuRTPPackets){

            final FU_A_RTPPayload fragmentationUnitPayload = (FU_A_RTPPayload)rtpPacket.getRtpPayload();
            nalUnitLength += fragmentationUnitPayload.getFragmentDataLenght();
        }

        //create NALUnit data
        byte[] nalUnitData = new byte[nalUnitLength];

        //set NALUnit start codes
        System.arraycopy(NALUnit.START_CODES, 0,
                nalUnitData, 0, NALUnit.START_CODES.length);
        int offset = NALUnit.START_CODES.length;

        //set NALUnit header
        final RTPPacket rtpPacket0 = fuRTPPackets.get(0);
        final FU_A_RTPPayload fragmentationUnitPayload0 = (FU_A_RTPPayload)rtpPacket0.getRtpPayload();
        final FUHeader fuHeader = fragmentationUnitPayload0.getFuHeader();
        final FUIndicator fuIndicator = fragmentationUnitPayload0.getFuIndicator();
        NALUnitHeader nalUnitHeader = new NALUnitHeader(
                fuIndicator.getForbiddenZeroBit(),
                fuIndicator.getNalReferenceIndicator(),
                fuHeader.getNalUnitType());
        nalUnitData[offset] = nalUnitHeader.toByte();
        offset += NALUnitHeader.LENGTH;

        //set NALUnit header
        for(RTPPacket rtpPacket: fuRTPPackets){

            final FU_A_RTPPayload fragmentationUnitPayload = (FU_A_RTPPayload)rtpPacket.getRtpPayload();
            final byte[] fragmentData = fragmentationUnitPayload.getFragmentData();
            System.arraycopy(fragmentData, 0,
                    nalUnitData, offset, fragmentData.length);
            offset += fragmentData.length;
        }

        final RTPHeader rtpHeader = rtpPacket0.getRtpHeader();
        final int timestamp = rtpHeader.getTimestamp();
        return new H264Packet(new NALUnit(nalUnitData), new Timestamp(timestamp));
    }
}
