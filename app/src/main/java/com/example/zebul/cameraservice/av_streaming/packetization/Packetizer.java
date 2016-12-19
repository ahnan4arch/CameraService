package com.example.zebul.cameraservice.av_streaming.packetization;

import com.example.zebul.cameraservice.av_streaming.rtp.header.RTPHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnitHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnitType;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 11/5/16.
 */

public class Packetizer {

    public static int MAXIMUM_TRANSMISSION_UNIT = 1500;//MTU
    public static int FU_INDICATOR_LENGTH = 1;
    public static int FU_HEADER_LENGTH = 1;

    private int maxPayloadLength = MAXIMUM_TRANSMISSION_UNIT/2;

    public void setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
    }

    public List<byte[]> makePackets(byte [] dataWithNALUnit, int offsetOfNALUnit){

        int lengthOfNALUnit = dataWithNALUnit.length-offsetOfNALUnit;
        if(lengthOfNALUnit < maxPayloadLength){
            byte [] packet = makeSingleNALUnitPacket(dataWithNALUnit, offsetOfNALUnit);
            List<byte[]> packets = new LinkedList<byte[]>();
            packets.add(packet);
            return packets;
        }
        else{
            return makeMultipleFragmentationUnitPackets(dataWithNALUnit, offsetOfNALUnit);
        }
    }

    private byte[] makeSingleNALUnitPacket(byte[] dataWithNALUnit, int offsetOfNALUnit) {

        byte [] rtpPacket = new byte[RTPHeader.LENGTH+(dataWithNALUnit.length-offsetOfNALUnit)];
        System.arraycopy(dataWithNALUnit, offsetOfNALUnit, rtpPacket, RTPHeader.LENGTH, rtpPacket.length-RTPHeader.LENGTH);
        return rtpPacket;
    }

    private List<byte[]> makeMultipleFragmentationUnitPackets(byte[] dataWithNALUnit, int offsetOfNALUnit) {

        List<byte[]> listOfPackets = new LinkedList<>();

        int offsetOfData = offsetOfNALUnit+1;//omit NALUnit header
        boolean start = true;
        boolean end = false;

        NALUnitHeader nalUnitHeader = NALUnitHeader.fromByte(dataWithNALUnit[offsetOfNALUnit]);
        NALUnitHeader fuIndicator = new NALUnitHeader(false,
                nalUnitHeader.getNalReferenceIndicator(), NALUnitType.FU_A);
        byte encodedFUIndicator = fuIndicator.toByte();
        while(true){

            int fuPayloadLength = 0;
            if(offsetOfData+ maxPayloadLength < dataWithNALUnit.length){

                fuPayloadLength = maxPayloadLength;
                end = false;
            }
            else{

                fuPayloadLength = dataWithNALUnit.length-offsetOfData;
                end = true;
            }

            int fuPayloadOffsetInPacket = RTPHeader.LENGTH+FU_INDICATOR_LENGTH+FU_HEADER_LENGTH;
            byte [] rtpPacket = new byte[fuPayloadOffsetInPacket+fuPayloadLength];
            System.arraycopy(dataWithNALUnit, offsetOfData, rtpPacket, fuPayloadOffsetInPacket, fuPayloadLength);


            byte S = 0;//start
            byte E = 0;//end
            byte R = 0;//reserved bit
            if(start){
                S = (byte)0b10000000;
                start = false;
            }
            if(end){
                E = (byte)0b01000000;
            }
            byte type = nalUnitHeader.getNalUnitType().toByte();
            /*
            The FU header has the following format:
            +---------------+
            |0|1|2|3|4|5|6|7|
            +-+-+-+-+-+-+-+-+
            |S|E|R|  Type   |
            +---------------+
            */
            byte encodedFUHeader = (byte)(S|E|R|type);

            rtpPacket[RTPHeader.LENGTH] = encodedFUIndicator;
            rtpPacket[RTPHeader.LENGTH+FU_INDICATOR_LENGTH] = encodedFUHeader;
            listOfPackets.add(rtpPacket);

            if(end){
                break;
            }
            else{
                offsetOfData+=fuPayloadLength;
            }
        }
        return listOfPackets;
    }
}
