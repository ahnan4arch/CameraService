package com.example.zebul.cameraservice.video_streaming.packetization;

import com.example.zebul.cameraservice.video_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.video_streaming.rtp.header.Timestamp;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnit;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPacket;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPackets;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by zebul on 11/20/16.
 */

public class RTPPacketizationSessionTest {

    @Test
    public void test1() {

        NALUnit nalUnit = new NALUnit(0, new byte[]{0,1,2,3,4});
        Timestamp timestamp = new Timestamp(0);
        AVPacket avPacket = new AVPacket(nalUnit, timestamp);
        AVPackets avPackets = new AVPackets();
        avPackets.addAVPacket(avPacket);

        RTPPacketizationSession rtpPacketizationSession = new RTPPacketizationSession();
        RTPPackets rtpPackets = rtpPacketizationSession.createRTPPackests(avPackets);
        assertTrue(rtpPackets.getNumberOfPackets() > 0);
    }
}
