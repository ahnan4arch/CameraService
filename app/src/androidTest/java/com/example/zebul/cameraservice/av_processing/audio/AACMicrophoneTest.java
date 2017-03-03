package com.example.zebul.cameraservice.av_processing.audio;

import android.test.AndroidTestCase;

import com.example.zebul.cameraservice.av_processing.PacketProcessingException;
import com.example.zebul.cameraservice.av_processing.PacketProcessingExceptionListener;
import com.example.zebul.cameraservice.av_protocols.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.av_processing.audio.microphone.AACMicrophone;
import com.example.zebul.cameraservice.utils.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zebul on 12/22/16.
 */

public class AACMicrophoneTest extends AndroidTestCase {


    public static class Idleness {

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

        AACPacketConsumer aacPacketListener = new AACPacketConsumer() {
            @Override
            public void consumeAACPacket(AACPacket aacPacket) {

                packetReceived.set(true);
            }
        };
        PacketProcessingExceptionListener packetProductionExceptionListener =
                new PacketProcessingExceptionListener() {
                    @Override
                    public void onPacketProductionException(PacketProcessingException exc) {

                        errorReceived.set(true);
                        throw new RuntimeException(exc);
                    }
                };

        AACMicrophone producer = new AACMicrophone(
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
            AACMicrophone producer,
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
