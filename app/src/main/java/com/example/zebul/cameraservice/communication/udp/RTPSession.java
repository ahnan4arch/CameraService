package com.example.zebul.cameraservice.communication.udp;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.zebul.cameraservice.packet_producers.PacketProductionExceptionListener;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.packet_producers.audio.AACPacketListener;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.packet_producers.video.H264PacketListener;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacketizer;
import com.example.zebul.cameraservice.av_streaming.rtp.basic.RTPPacketizer;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packetizer;
import com.example.zebul.cameraservice.packet_producers.audio.MicrophoneSettings;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraSettings;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraVideoH264PacketProducer;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.message.Message;
import com.example.zebul.cameraservice.packet_producers.PacketProductionException;
import com.example.zebul.cameraservice.packet_producers.audio.MicrophoneAudioAACPacketProducer;

import java.net.InetSocketAddress;


/**
 * Created by zebul on 10/23/16.
 */
public class RTPSession {

    private InetSocketAddress videoSocketAddress = null;
    private InetSocketAddress audioSocketAddress = null;

    private CameraSettings cameraSettings;
    private MicrophoneSettings microphoneSettings;

    private SocketEngine videoSocketEngine;
    private SocketEngine audioSocketEngine;

    private VideoSession videoSession = new VideoSession();
    private AudioSession audioSession = new AudioSession();

    public RTPSession(
            @Nullable InetSocketAddress videoSocketAddress,
            @Nullable InetSocketAddress audioSocketAddress,
            CameraSettings cameraSettings,
            MicrophoneSettings microphoneSettings){

        this.videoSocketAddress = videoSocketAddress;
        this.audioSocketAddress = audioSocketAddress;

        this.cameraSettings = cameraSettings;
        this.microphoneSettings = microphoneSettings;

        if(videoSocketAddress != null){

            videoSocketEngine = new SocketEngine(videoSocketAddress.getPort(),
                    new SocketMessageReceptionListener() {
                        @Override
                        public void onSocketMessageReceived(Message message_) {

                            int foo = 1;
                            int bar = foo;
                        }
                    });
        }

        if(audioSocketAddress != null){

            audioSocketEngine = new SocketEngine(audioSocketAddress.getPort(),
                    new SocketMessageReceptionListener() {
                        @Override
                        public void onSocketMessageReceived(Message message_) {

                            int foo = 1;
                            int bar = foo;
                        }
                    });
        }
    }

    public void start(){

        if(videoSocketEngine != null){

            videoSocketEngine.start();
            videoSession.start();
        }

        if(audioSocketEngine != null){

            audioSocketEngine.start();
            audioSession.start();
        }
    }

    public void stop(){

        if(videoSocketEngine != null){

            videoSocketEngine.stop();
            videoSession.stop();
        }

        if(audioSocketEngine != null){

            audioSocketEngine.stop();
            audioSession.stop();
        }
    }

    class AudioSession implements AACPacketListener, PacketProductionExceptionListener {

        private final String TAG = AudioSession.class.getSimpleName();

        private MicrophoneAudioAACPacketProducer aacPacketProducer =
                new MicrophoneAudioAACPacketProducer(this, this);

        private AACPacketizer aacPacketizer = new AACPacketizer(
                RTPPacketizer.generateRandomInt(),
                RTPPacketizer.generateRandomInt()
        );

        public void start() {

            aacPacketProducer.start(microphoneSettings);
        }

        @Override
        public void onAACPacket(AACPacket aacPacket) {

            RTPPackets rtpPackets = aacPacketizer.createRTPPackets(aacPacket);
            for (RTPPacket rtpPacket : rtpPackets) {

                byte [] rtpPacketBytes = rtpPacket.toBytes();
                Message message = new Message(audioSocketAddress, rtpPacketBytes);
                audioSocketEngine.post(message);
            }
        }

        public void stop() {

            aacPacketProducer.stop();
        }

        @Override
        public void onPacketProductionException(PacketProductionException exc) {

        }
    }

    class VideoSession implements H264PacketListener, PacketProductionExceptionListener{

        private static final String TAG = "ZZZ";

        CameraVideoH264PacketProducer h264PacketProducer =
                //new AssetFileAVPacketProducer("H264_artifacts_motion.h264");
                new CameraVideoH264PacketProducer(this, this);

        H264Packetizer h264Packetizer = new H264Packetizer(
            500, 0xDEADBEEF
        );

        public void start() {

            if(!h264PacketProducer.isWorking()){

                h264PacketProducer.start(cameraSettings);
            }
        }

        public void stop() {

            if(h264PacketProducer.isWorking()){

                h264PacketProducer.stop();
            }
        }

        private int h264PacketCounter = 0;
        private int rtpPacketCounter = 0;
        @Override
        public void onH264Packet(H264Packet h264Packet) {

            Log.d(TAG, "received h264Packet with size: " + h264Packet.getNALUnit().getData().length +
                    " h264PacketCounter:"+h264PacketCounter++);

            RTPPackets rtpPackets = h264Packetizer.createRTPPackets(h264Packet);
            for (RTPPacket rtpPacket : rtpPackets) {

                byte [] rtpPacketBytes = rtpPacket.toBytes();
                Log.d(TAG, "received rtpPackets with size: " + rtpPacketBytes.length +
                        " rtpPacketCounter:"+rtpPacketCounter++);
                //bytes to packetize
                Message message = new Message(videoSocketAddress, rtpPacketBytes);
                videoSocketEngine.post(message);
            }
        }

        @Override
        public void onPacketProductionException(PacketProductionException exc) {

        }
    }
}
