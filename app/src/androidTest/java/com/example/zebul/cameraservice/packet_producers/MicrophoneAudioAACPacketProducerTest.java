package com.example.zebul.cameraservice.packet_producers;

import android.test.AndroidTestCase;

import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.packet_producers.audio.AACPacketListener;
import com.example.zebul.cameraservice.av_streaming.rtsp.audio.AudioSettings;
import com.example.zebul.cameraservice.packet_producers.audio.MicrophoneAudioAACPacketProducer;
import com.example.zebul.cameraservice.packet_producers.audio.MicrophoneSettings;
import com.example.zebul.cameraservice.utils.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zebul on 12/22/16.
 */

public class MicrophoneAudioAACPacketProducerTest extends AndroidTestCase {


    static class Idleness {

        public void makeIdle(long milliseconds_) {

            try {
                Thread.sleep(milliseconds_);
            } catch (InterruptedException exc_) {
                exc_.printStackTrace();
            }
        }
    }

    public void testAudioAACPacketProduction() throws Throwable {

        final AtomicBoolean packetReceived = new AtomicBoolean(false);
        final AtomicBoolean errorReceived = new AtomicBoolean(false);

        AACPacketListener aacPacketListener = new AACPacketListener() {
            @Override
            public void onAACPacket(AACPacket aacPacket) {

                packetReceived.set(true);
            }
        };
        PacketProductionExceptionListener packetProductionExceptionListener =
                new PacketProductionExceptionListener() {
                    @Override
                    public void onPacketProductionException(PacketProductionException exc) {

                        errorReceived.set(true);
                        throw new RuntimeException(exc);
                    }
                };

        MicrophoneAudioAACPacketProducer producer = new MicrophoneAudioAACPacketProducer(
                aacPacketListener, packetProductionExceptionListener);

        for(int i=0; i<10; i++){

            packetReceived.set(false);
            errorReceived.set(false);
            audioAACPacketProduction(producer, packetReceived, i);
            assertTrue("error in iteration: "+i, packetReceived.get());
            assertFalse("error in iteration: "+i, errorReceived.get());
        }
    }

    public void audioAACPacketProduction(
            MicrophoneAudioAACPacketProducer producer,
            AtomicBoolean packetReceived,
            int i){

        MicrophoneSettings microphoneSettings = new MicrophoneSettings(AudioSettings.DEFAULT);
        producer.start(microphoneSettings);
        Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
        while(!timeout.isTimeout()) {

            if(packetReceived.get()){
                break;
            }
            else {
                new Idleness().makeIdle(100);
            }
        }
        producer.stop();
    }
}
