package com.example.zebul.cameraservice.av_processing.video;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.example.zebul.cameraservice.av_protocols.rtp.BytesOfRTPPackets;
import com.example.zebul.cameraservice.av_protocols.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.RTPPackets;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Depacketizer;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packetizer;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.av_processing.video.file.FileH264PacketProducer;

/**
 * Created by zebul on 2/6/17.
 */

public class FilePacketizationTest extends ApplicationTestCase<Application> {

    public FilePacketizationTest() {
        super(Application.class);
    }

    public void test1(){

        final Context context = getContext();
        FileH264PacketProducer fileProducer = new FileH264PacketProducer(context, "H264_artifacts_motion.h264");

        H264Packetizer packetizer = new H264Packetizer();
        H264Depacketizer depacketizer = new H264Depacketizer();

        try{

            while(true){

                final H264Packets inputH264Packets = fileProducer.produceH264Packets();
                final RTPPackets rtpPackets = packetizer.createRTPPackets(inputH264Packets);
                BytesOfRTPPackets bytesOfRTPPackets = new BytesOfRTPPackets();
                for(RTPPacket rtpPacket: rtpPackets){
                    bytesOfRTPPackets.addRTPPacketBytes(rtpPacket.toBytes());
                }
                final H264Packets outputH264Packets = depacketizer.createH264Packets(bytesOfRTPPackets);
                assertEquals(inputH264Packets, outputH264Packets);
            }
        }
        catch(Exception exc_){

            int foo = 1;
            int bar = foo;
        }
    }

}
