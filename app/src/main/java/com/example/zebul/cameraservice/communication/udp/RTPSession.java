package com.example.zebul.cameraservice.communication.udp;

import android.util.Log;

import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacket;
import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacketListener;
import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPackets;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264Packets;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacketizer;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packetizer;
import com.example.zebul.cameraservice.av_streaming.rtsp.AudioSettings;
import com.example.zebul.cameraservice.packet_producers.video.CameraVideoH264PacketProducer;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.message.Message;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264PacketProducer;
import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionException;
import com.example.zebul.cameraservice.packet_producers.audio.MicrophoneAudioAACPacketProducer;

import java.net.InetSocketAddress;


/**
 * Created by zebul on 10/23/16.
 */
public class RTPSession {

    private InetSocketAddress videoSocketAddress = null;
    private InetSocketAddress audioSocketAddress = null;

    private SocketEngine videoSocketEngine;
    private SocketEngine audioSocketEngine;

    private Thread videoThread;
    AudioSession audioSession = new AudioSession();

    public RTPSession(InetSocketAddress videoSocketAddress, InetSocketAddress audioSocketAddress){

        this.videoSocketAddress = videoSocketAddress;
        this.audioSocketAddress = audioSocketAddress;

        videoSocketEngine = new SocketEngine(videoSocketAddress.getPort(),
                new SocketMessageReceptionListener() {
                    @Override
                    public void onSocketMessageReceived(Message message_) {

                        int foo = 1;
                        int bar = foo;
                    }
        });

        audioSocketEngine = new SocketEngine(audioSocketAddress.getPort(),
                new SocketMessageReceptionListener() {
                    @Override
                    public void onSocketMessageReceived(Message message_) {

                        int foo = 1;
                        int bar = foo;
                    }
                });
    }

    public void start(){

        videoSocketEngine.start();
        videoThread = new Thread(new VideoSession());
        videoThread.start();

        audioSocketEngine.start();
        audioSession.start();
    }

    public void stop(){

        videoSocketEngine.stop();
        videoThread.interrupt();

        audioSocketEngine.stop();
        audioSession.stop();
    }

    class AudioSession implements AACPacketListener {

        private final String TAG = AudioSession.class.getSimpleName();

        MicrophoneAudioAACPacketProducer aacPacketProducer =
                new MicrophoneAudioAACPacketProducer(AudioSettings.DEFAULT, this);

        AACPacketizer aacPacketizer = new AACPacketizer();

        public void start() {

            aacPacketProducer.start();
        }

        @Override
        public void onAACPacket(AACPacket aacPacket) {

            AACPackets aacPackets = new AACPackets();
            aacPackets.addPacket(aacPacket);
            RTPPackets rtpPackets = aacPacketizer.createRTPPackets(aacPackets);
            Log.d(TAG, "created: "+rtpPackets.getNumberOfPackets()+" rtpPackets");
            int count = rtpPackets.getNumberOfPackets();
            for (RTPPacket rtpPacket : rtpPackets) {

                byte [] rtpPacketBytes = rtpPacket.toBytes();
                Log.d(TAG, rtpPacketBytes.length +" bytes will be sent");
                Message message = new Message(audioSocketAddress, rtpPacketBytes);
                audioSocketEngine.post(message);
            }
        }

        public void stop() {

            aacPacketProducer.stop();
        }
    }

    class VideoSession implements Runnable{

        private static final String TAG = "ZZZ";

        H264PacketProducer avPacketProducer =
                //new AssetFileAVPacketProducer("H264_artifacts_motion.h264");
                new CameraVideoH264PacketProducer();

        H264Packetizer h264RtpPacketizer = new H264Packetizer();
        @Override
        public void run() {

            try {

                while (true) {

                    packetize();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch(PacketProductionException exc_){

                int foo = 1;
                int bar = foo;
            }
        }

        private void packetize() throws PacketProductionException {

            H264Packets H264Packets = avPacketProducer.produceH264Packets();
            //Log.d(TAG, "produced: "+avPackets.getNumberOfPackets()+" avPackets");
            RTPPackets rtpPackets = h264RtpPacketizer.createRTPPackets(H264Packets);
            //Log.d(TAG, "created: "+rtpPackets.getNumberOfPackets()+" rtpPackets");
            int count = rtpPackets.getNumberOfPackets();
            for (RTPPacket rtpPacket : rtpPackets) {

                byte [] rtpPacketBytes = rtpPacket.toBytes();
                //Log.d(TAG, rtpPacketBytes.length +" bytes will be sent");
                Message message = new Message(videoSocketAddress, rtpPacketBytes);
                videoSocketEngine.post(message);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
