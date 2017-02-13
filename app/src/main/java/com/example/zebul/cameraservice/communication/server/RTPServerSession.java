package com.example.zebul.cameraservice.communication.server;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.zebul.cameraservice.CameraService;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.communication.udp.SocketEngine;
import com.example.zebul.cameraservice.communication.udp.SocketMessageReceptionListener;
import com.example.zebul.cameraservice.packet_producers.PacketProductionExceptionListener;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.packet_producers.audio.AACPacketListener;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.packet_producers.video.H264PacketConsumer;
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
import com.example.zebul.cameraservice.packet_producers.video.file.AssetFileAVPacketProducer;

import java.net.InetSocketAddress;


/**
 * Created by zebul on 10/23/16.
 */
public class RTPServerSession {

    private InetSocketAddress videoSocketAddress = null;
    private InetSocketAddress audioSocketAddress = null;

    private CameraSettings cameraSettings;
    private MicrophoneSettings microphoneSettings;

    private SocketEngine videoSocketEngine;
    private SocketEngine audioSocketEngine;

    private VideoSession videoSession = new VideoSession();
    private AudioSession audioSession = new AudioSession();

    public RTPServerSession(
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

    class VideoSession implements H264PacketConsumer, PacketProductionExceptionListener{

        private static final String TAG = "ZZZ";
        private boolean useCameraProducer = true;

        CameraVideoH264PacketProducer cameraH264PacketProducer = new CameraVideoH264PacketProducer(this, this);
        AssetFileAVPacketProducer fileH264PacketProducer =
                new AssetFileAVPacketProducer(CameraService.CAMERA_SERVICE, "H264_artifacts_motion.h264");

        H264Packetizer h264Packetizer = new H264Packetizer(
            500, 0xDEADBEEF
        );

        public void start() {

            if(useCameraProducer){
                if(!cameraH264PacketProducer.isWorking()){

                    cameraH264PacketProducer.start(cameraSettings);
                }
            }
            else {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        try {

                            while (true) {

                                final H264Packets h264Packets = fileH264PacketProducer.produceH264Packets();
                                for (H264Packet h264Packet : h264Packets) {
                                    consumeH264Packet(h264Packet);
                                }
                                Thread.sleep(10);
                            }
                        } catch (Exception exc) {

                        }

                    }
                });
                thread.start();
            }
        }

        public void stop() {

            if(useCameraProducer){

                if(cameraH264PacketProducer.isWorking()){

                    cameraH264PacketProducer.stop();
                }
            }
        }

        private int h264PacketCounter = 0;
        private int rtpPacketCounter = 0;
        @Override
        public void consumeH264Packet(H264Packet h264Packet) {

            Log.d(TAG, "received h264Packet with size: " + h264Packet.getNALUnit().getData().length +
                    " h264PacketCounter:"+h264PacketCounter+", timestamp: "+h264Packet.getTimestamp().getTimestampInMillis());

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
