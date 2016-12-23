package com.example.zebul.cameraservice.packet_producers;

import android.test.AndroidTestCase;

import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacket;
import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacketListener;
import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPackets;
import com.example.zebul.cameraservice.av_streaming.rtsp.AudioSettings;
import com.example.zebul.cameraservice.packet_producers.audio.MicrophoneAudioAACPacketProducer;
import com.example.zebul.cameraservice.utils.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zebul on 12/22/16.
 */

public class MicrophoneAudioAACPacketProducerTest extends AndroidTestCase {


    class Idleness {

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
        MicrophoneAudioAACPacketProducer producer =
                new MicrophoneAudioAACPacketProducer(AudioSettings.DEFAULT,
                        new AACPacketListener() {
                            @Override
                            public void onAACPacket(AACPacket aacPacket) {

                                packetReceived.set(true);
                            }
                });
        producer.start();

        Timeout timeout = new Timeout(2, TimeUnit.SECONDS);
        while(!timeout.isTimeout()) {

            if(packetReceived.get()){
                break;
            }
            else {
                new Idleness().makeIdle(100);
            }
        }

        producer.stop();
        assertTrue(packetReceived.get());
    }
}
