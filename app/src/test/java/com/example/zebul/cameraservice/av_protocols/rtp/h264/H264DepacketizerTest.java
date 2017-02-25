package com.example.zebul.cameraservice.av_protocols.rtp.h264;

import com.example.zebul.cameraservice.av_protocols.rtp.BytesOfRTPPackets;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by zebul on 1/30/17.
 */

public class H264DepacketizerTest {

    @Test
    public void test_when_packetizer_fragments_H264Packets_then_depacketizer_can_defagment_it(){

        //given
        final H264Packets expectedH264Packets = H264PacketizerTest.createExampleH264Packets();
        final H264Packetizer packetizer = new H264Packetizer();
        packetizer.setMaxPayloadLength(2);
        final BytesOfRTPPackets bytesOfRTPPackets = packetizer.createBytesOfRTPPackets(expectedH264Packets);

        final H264Depacketizer h264Depacketizer = new H264Depacketizer();
        final H264Packets actualH264Packets = h264Depacketizer.createH264Packets(bytesOfRTPPackets);
        assertTrue(expectedH264Packets.equals(actualH264Packets));
    }

    @Test
    public void test_when_packetizer_creates_H264Packets_then_depacketizer_can_recreate_it(){

        //given
        final H264Packets expectedH264Packets = H264PacketizerTest.createExampleH264Packets();
        final H264Packetizer packetizer = new H264Packetizer();
        packetizer.setMaxPayloadLength(H264Packetizer.MAXIMUM_TRANSMISSION_UNIT);
        final BytesOfRTPPackets bytesOfRTPPackets = packetizer.createBytesOfRTPPackets(expectedH264Packets);

        final H264Depacketizer h264Depacketizer = new H264Depacketizer();
        final H264Packets actualH264Packets = h264Depacketizer.createH264Packets(bytesOfRTPPackets);
        assertTrue(expectedH264Packets.equals(actualH264Packets));
    }
}
