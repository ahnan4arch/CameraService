package com.example.zebul.cameraservice.av_streaming.rtp.h264;

import com.example.zebul.cameraservice.av_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by zebul on 11/20/16.
 */

public class H264PacketizerTest {

    @Test
    public void test_when_rtpPackets_are_created_from_valid_H264Packets_then_rtpPackets_contains_packets() {

        //given
        NALUnit nalUnit = new NALUnit(new byte[]{0,1,2,3,4});
        Timestamp timestamp = new Timestamp(0);
        H264Packet h264Packet = new H264Packet(nalUnit, timestamp);
        H264Packets h264Packets = new H264Packets();
        h264Packets.addPacket(h264Packet);

        //when
        H264Packetizer rtpPacketizationSession = new H264Packetizer();
        RTPPackets rtpPackets = rtpPacketizationSession.createRTPPackets(h264Packets);

        //then
        assertTrue(rtpPackets.getNumberOfPackets() > 0);
    }

    @Test
    public void test_when_NALUnit_has_grater_length_than_MTU_then_packetizer_creates_fragmentation_units() {

        byte NALUnitHeader = 0x01;
        byte [] nalUnitPayload = new byte[]{NALUnitHeader, 0x11, 0x22, 0x33, 0x44, 0x55};
        byte [] nalUnitPayloadWithStartCodes = new byte[NALUnit.START_CODES.length+nalUnitPayload.length];
        System.arraycopy(NALUnit.START_CODES, 0, nalUnitPayloadWithStartCodes, 0, NALUnit.START_CODES.length);
        System.arraycopy(nalUnitPayload, 0, nalUnitPayloadWithStartCodes, NALUnit.START_CODES.length, nalUnitPayload.length);

        NALUnit nalUnit = new NALUnit(nalUnitPayloadWithStartCodes);
        Timestamp timestamp = new Timestamp(0);
        H264Packet h264Packet = new H264Packet(nalUnit, timestamp);
        H264Packets h264Packets = new H264Packets();
        h264Packets.addPacket(h264Packet);

        H264Packetizer packetizer = new H264Packetizer();
        int maxLengthOfPacket = 2;
        packetizer.setMaxPayloadLength(maxLengthOfPacket);

        RTPPackets rtpPackets = packetizer.createRTPPackets(h264Packets);
        assertEquals(3, rtpPackets.getNumberOfPackets());

        //packet0
        RTPPacket rtpPacket0 = rtpPackets.getPacket(0);
        byte [] packet0 = rtpPacket0.toBytes();
        int expectedLengthOfpacket0 =
                RTPHeader.LENGTH+ H264Packetizer.FU_INDICATOR_LENGTH+ H264Packetizer.FU_HEADER_LENGTH+maxLengthOfPacket;
        assertEquals(expectedLengthOfpacket0, packet0.length);

        byte encodedFUHeader = packet0[RTPHeader.LENGTH+ H264Packetizer.FU_INDICATOR_LENGTH];
        byte S = (byte)0b10000000;
        byte E = (byte)0b01000000;
        assertTrue((encodedFUHeader&S)==S);//start must be set
        assertTrue((encodedFUHeader&E)==0);//end must not be set

        assertEquals(0x11, packet0[packet0.length-2]);
        assertEquals(0x22, packet0[packet0.length-1]);

        //packet1
        RTPPacket rtpPacket1 = rtpPackets.getPacket(1);
        byte [] packet1 = rtpPacket1.toBytes();
        int expectedLengthOfpacket1 =
                RTPHeader.LENGTH+ H264Packetizer.FU_INDICATOR_LENGTH+ H264Packetizer.FU_HEADER_LENGTH+maxLengthOfPacket;
        assertEquals(expectedLengthOfpacket1, packet1.length);

        encodedFUHeader = packet1[RTPHeader.LENGTH+ H264Packetizer.FU_INDICATOR_LENGTH];
        assertTrue((encodedFUHeader&S)==0);//start must not be set
        assertTrue((encodedFUHeader&E)==0);//end must not be set

        assertEquals(0x33, packet1[packet1.length-2]);
        assertEquals(0x44, packet1[packet1.length-1]);

        //packet2
        RTPPacket rtpPacket2 = rtpPackets.getPacket(2);
        byte [] packet2 = rtpPacket2.toBytes();
        int expectedLengthOfpacket2 =
                RTPHeader.LENGTH+ H264Packetizer.FU_INDICATOR_LENGTH+ H264Packetizer.FU_HEADER_LENGTH+1;
        assertEquals(expectedLengthOfpacket2, packet2.length);

        encodedFUHeader = packet2[RTPHeader.LENGTH+ H264Packetizer.FU_INDICATOR_LENGTH];
        assertTrue((encodedFUHeader&S)==0);//start must not be set
        assertTrue((encodedFUHeader&E)==E);//end must be set

        assertEquals(0x55, packet2[packet2.length-1]);
    }
}
