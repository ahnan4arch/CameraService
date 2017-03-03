package com.example.zebul.cameraservice.av_processing.video;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.zebul.cameraservice.av_processing.IllegalProductionStateException;
import com.example.zebul.cameraservice.av_processing.audio.AACMicrophoneTest;
import com.example.zebul.cameraservice.av_processing.PacketProcessingException;
import com.example.zebul.cameraservice.av_processing.PacketProcessingExceptionListener;
import com.example.zebul.cameraservice.av_processing.video.camera.Resolution;
import com.example.zebul.cameraservice.av_processing.video.camera.VideoSettings;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_processing.video.camera.CameraSettings;
import com.example.zebul.cameraservice.av_processing.video.camera.H264Camera;
import com.example.zebul.cameraservice.utils.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zebul on 12/28/16.
 */

public class H264CameraTest extends AndroidTestCase {

    private final Resolution defaultResolution = Resolution._640x480;
    private final VideoSettings defaultVideoSettings = new VideoSettings(defaultResolution, 6000000, VideoSettings.DEFAULT_FRAME_RATE);
    private final CameraSettings defaultCameraSettings = new CameraSettings(defaultVideoSettings);

    public void test_when_VideoH264PacketProducer_is_restarted_then_produces_packets_in_expected_time(){

        //given
        final AtomicBoolean packetReceived = new AtomicBoolean(false);
        final AtomicBoolean exceptionReceived = new AtomicBoolean(false);

        H264PacketConsumer h264PacketConsumer = new H264PacketConsumer(){

            @Override
            public void consumeH264Packet(H264Packet h264Packet) {

                packetReceived.set(true);
            }
        };

        PacketProcessingExceptionListener packetProductionExceptionListener =
                new PacketProcessingExceptionListener(){

                    @Override
                    public void onPacketProductionException(PacketProcessingException exc) {

                        exceptionReceived.set(true);
                        throw new RuntimeException(exc);
                    }
                };

        H264Camera producer = new H264Camera(
                h264PacketConsumer, packetProductionExceptionListener);

        for(int i=0; i<10; i++){

            Log.d("*** iteration ***", "before test"+i);
            packetReceived.set(false);
            exceptionReceived.set(false);

            //when
            producer.start(defaultCameraSettings);
            Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
            while(!timeout.isTimeout()) {

                if(packetReceived.get()){
                    break;
                }
                else{
                    new AACMicrophoneTest.Idleness().makeIdle(100);
                }
            }
            producer.stop();

            //then
            assertTrue("error in iteration: "+i, packetReceived.get());
            assertFalse("error in iteration: "+i, exceptionReceived.get());
            Log.d("*** iteration ***", "after test"+i);
        }
    }

    private class H264PacketListenerFake implements H264PacketConsumer {

        @Override
        public void consumeH264Packet(H264Packet h264Packet) {

        }
    }

    private class PacketProductionExceptionListenerFake implements PacketProcessingExceptionListener {

        PacketProcessingException receivedException;
        @Override
        public void onPacketProductionException(PacketProcessingException exc) {

            receivedException = exc;
        }
    }

    public void test_when_CameraVideoH264PacketProducer_starts_second_time_in_row_then_IllegalProductionStateException_is_received(){

        //given
        PacketProductionExceptionListenerFake exceptionListener =
                new PacketProductionExceptionListenerFake();

        H264Camera producer = new H264Camera(
                new H264PacketListenerFake(), exceptionListener);

        try {

            producer.start(defaultCameraSettings);//1'st time
            assertNull(exceptionListener.receivedException);

            //when
            producer.start(defaultCameraSettings);//2'nd time

            //then
            assertNotNull(exceptionListener.receivedException);
            assertTrue(exceptionListener.receivedException instanceof IllegalProductionStateException);
        }
        finally {
            producer.stop();
        }
    }

    public void test_when_CameraVideoH264PacketProducer_starts_second_time_in_row_then_returns_false(){

        //given
        PacketProductionExceptionListenerFake exceptionListener =
                new PacketProductionExceptionListenerFake();

        H264Camera producer = new H264Camera(
                new H264PacketListenerFake(), exceptionListener);

        try {

            boolean startResult1 = producer.start(defaultCameraSettings);//1'st time
            assertTrue(startResult1);

            //when
            boolean startResult2 = producer.start(defaultCameraSettings);//2'nd time

            //then
            assertFalse(startResult2);
        }
        finally {
            producer.stop();
        }
    }

    public void test_when_VideoH264PacketProducer_has_started_then_isWorking_returns_true_otherwise_false(){

        //given
        H264Camera producer = new H264Camera(
                new H264PacketListenerFake(), new PacketProductionExceptionListenerFake());

        assertFalse(producer.isWorking());

        for(int i=0; i<3; i++){

            //when
            producer.start(defaultCameraSettings);
            //then
            assertTrue(producer.isWorking());

            //when
            producer.stop();
            //then
            assertFalse(producer.isWorking());
        }
    }

}
