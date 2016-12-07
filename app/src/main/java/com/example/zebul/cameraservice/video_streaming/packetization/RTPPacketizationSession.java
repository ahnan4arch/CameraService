package com.example.zebul.cameraservice.video_streaming.packetization;

import android.util.Log;

import com.example.zebul.cameraservice.video_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.video_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.video_streaming.rtp.header.RTPHeader;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnit;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitHeader;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitHeaderDecoder;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitHeaderEncoder;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitReader;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitType;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayload;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.RTPPayloadType;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.fragmentation_unit.FUHeader;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.fragmentation_unit.FUIndicator;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.fragmentation_unit.FU_A_RTPPayload;
import com.example.zebul.cameraservice.video_streaming.rtp.payload.single_time.STAP_A_RTPPayload;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPacket;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPackets;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by zebul on 11/20/16.
 */

public class RTPPacketizationSession {

    private final String TAG = RTPPacketizationSession.class.getSimpleName();

    private int sequenceNumber;
    private int SSRC;
    private RTPPacketizer rtpPacketizer = new RTPPacketizer();

    public RTPPacketizationSession(){

        this(0, new Random().nextInt());
    }

    public RTPPacketizationSession(int sequenceNumber, int SSRC){

        this.sequenceNumber = sequenceNumber;
        this.SSRC = SSRC;
    }

    public RTPPackets createRTPPackests(AVPackets avPackets){

        RTPPackets rtpPackets = rtpPacketizer.createRTPPackets(avPackets);
        return rtpPackets;
    }

    public static int MAXIMUM_TRANSMISSION_UNIT = 1500;//MTU
    public static int FU_INDICATOR_LENGTH = 1;
    public static int FU_HEADER_LENGTH = 1;

    class RTPPacketizer{

        private int maxPayloadLength = MAXIMUM_TRANSMISSION_UNIT/2;

        public RTPPackets createRTPPackets(AVPackets avPackets) {

            RTPPackets rtpPackets = new RTPPackets();
            for(AVPacket avPacket: avPackets) {

                rtpPackets.addRTPPackets(createRTPPackets(avPacket));
            }
            return rtpPackets;
        }

        public List<RTPPacket> createRTPPackets(AVPacket avPacket){

            NALUnit nalUnit = avPacket.getNALUnit();
            byte [] nalUnitData = nalUnit.getData();
            if(nalUnitData.length < maxPayloadLength){
                List<RTPPacket> rtpPackets = new LinkedList<RTPPacket>();
                if(nalUnitData.length <= 4){
                    return rtpPackets;
                }
                RTPPacket rtpPacket = makeSingleTimeNALUnitPacket(avPacket);
                rtpPackets.add(rtpPacket);
                return rtpPackets;
            }
            else{
                return makeFragmentationUnitPackets(avPacket);
            }
        }

        private RTPHeader makeRTPHeader(AVPacket avPacket, boolean isLast){

            boolean markerBit = isLast ? true : false;///???????????????
            byte payloadType = (byte) 96;
            int timestamp = (int)avPacket.getTimestamp().getTimestampInMillis();
            RTPHeader rtpHeader = new RTPHeader(
                    markerBit, payloadType, sequenceNumber++, timestamp, SSRC);
            return rtpHeader;
        }

        private RTPPacket makeSingleTimeNALUnitPacket(AVPacket avPacket) {

            RTPHeader rtpHeader = makeRTPHeader(avPacket, true);
            RTPPayload rtpPayload = new STAP_A_RTPPayload(avPacket.getNALUnit());

            //TODO: remove after test
            byte [] dataWithNalUnit = avPacket.getNALUnit().getData();
            int offsetOfNALUnit = NALUnit.START_CODES.length;
            NALUnitHeader nalUnitHeader = NALUnitHeaderDecoder.decode(dataWithNalUnit[offsetOfNALUnit]);
            byte type = nalUnitHeader.getNALUnitType();
            Log.d(TAG, "single NALUnitType type: "+type);
            //TODO: -----------------

            RTPPacket rtpPacket = new RTPPacket(rtpHeader, rtpPayload);
            return rtpPacket;
        }

        private List<RTPPacket> makeFragmentationUnitPackets(AVPacket avPacket) {

            List<RTPPacket> rtpPackets = new LinkedList<RTPPacket>();

            int offsetOfNALUnit = NALUnit.START_CODES.length;
            int offsetOfDataInNalUnit = offsetOfNALUnit+1;//omit NALUnit header
            boolean start = true;
            boolean end = false;

            byte [] dataWithNalUnit = avPacket.getNALUnit().getData();
            NALUnitHeader nalUnitHeader = NALUnitHeaderDecoder.decode(dataWithNalUnit[offsetOfNALUnit]);
            FUIndicator fuIndicator = new FUIndicator(false, nalUnitHeader.getNALReferenceIndicator());
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

                byte nalUnitType = nalUnitHeader.getNALUnitType();
                //TODO: remove after test
                Log.d(TAG, "fragmentation NALUnitType type: "+nalUnitType);
                //-----------------------

                FUHeader fuHeader = new FUHeader(start, end, NALUnitType.fromByte(nalUnitType));
                if(start){
                    start = false;
                }

                RTPHeader rtpHeader = makeRTPHeader(avPacket, end);
                RTPPayload rtpPayload = new FU_A_RTPPayload(fuIndicator, fuHeader, payloadFragmentData);
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

        private RTPPayloadType selectRTPPayloadTypeFor(AVPacket avPacket) {

            return null;
        }

    }
}
