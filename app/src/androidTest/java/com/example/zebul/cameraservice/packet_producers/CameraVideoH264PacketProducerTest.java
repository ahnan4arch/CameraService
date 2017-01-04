package com.example.zebul.cameraservice.packet_producers;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionException;
import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionExceptionListener;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264Packet;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264PacketListener;
import com.example.zebul.cameraservice.av_streaming.rtsp.video.Resolution;
import com.example.zebul.cameraservice.av_streaming.rtsp.video.VideoSettings;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraSettings;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraVideoH264PacketProducer;
import com.example.zebul.cameraservice.utils.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zebul on 12/28/16.
 */

public class CameraVideoH264PacketProducerTest extends AndroidTestCase {

    public void testVideoH264PacketProduction() throws PacketProductionException {

        final AtomicBoolean packetReceived = new AtomicBoolean(false);
        final AtomicBoolean errorReceived = new AtomicBoolean(false);

        H264PacketListener h264PacketListener = new H264PacketListener(){

            @Override
            public void onH264Packet(H264Packet h264Packet) {

                packetReceived.set(true);
            }
        };

        PacketProductionExceptionListener packetProductionExceptionListener =
                new PacketProductionExceptionListener(){

                    @Override
                    public void onPacketProductionException(PacketProductionException exc) {

                        errorReceived.set(true);
                        throw new RuntimeException(exc);
                    }
                };

        CameraVideoH264PacketProducer producer = new CameraVideoH264PacketProducer(
                h264PacketListener, packetProductionExceptionListener);

        for(int i=0; i<10; i++){

            Log.d("*** iteration ***", "before test"+i);
            packetReceived.set(false);
            errorReceived.set(false);
            testVideoH264PacketProduction(producer, packetReceived, i);
            assertTrue("error in iteration: "+i, packetReceived.get());
            assertFalse("error in iteration: "+i, errorReceived.get());
            Log.d("*** iteration ***", "after test"+i);
        }
    }

    private void testVideoH264PacketProduction(CameraVideoH264PacketProducer producer,
                                               AtomicBoolean packetReceived,
                                               int i) {

        Resolution resolution = Resolution._640x480;
        VideoSettings videoSettings = new VideoSettings(resolution, 6000000, VideoSettings.DEFAULT_FRAME_RATE);
        CameraSettings cameraSettings = new CameraSettings(videoSettings);
        producer.start(cameraSettings, i+"");
        Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
        while(!timeout.isTimeout()) {

            if(packetReceived.get()){
                break;
            }
            else{
                new MicrophoneAudioAACPacketProducerTest.Idleness().makeIdle(100);
            }
        }
        producer.stop();
    }

}
