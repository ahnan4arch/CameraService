package com.example.zebul.cameraservice.communication.udp;

import android.util.Log;

import com.example.zebul.cameraservice.packet_producers.CameraVideoPacketProducer;
import com.example.zebul.cameraservice.av_streaming.packetization.RTPPacketizationSession;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.message.Message;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacketProducer;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPackets;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacketProductionException;
import com.example.zebul.cameraservice.packet_producers.MicrophoneAudioPacketProducer;

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
    private Thread audioThread;

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
        audioThread = new Thread(new AudioSession());
        audioThread.start();
    }

    public void stop(){

        videoSocketEngine.stop();
        videoThread.interrupt();

        audioSocketEngine.stop();
        audioThread.interrupt();
    }

    class AudioSession implements Runnable{

        private final String TAG = AudioSession.class.getSimpleName();

        AVPacketProducer avPacketProducer =
                new MicrophoneAudioPacketProducer();

        RTPPacketizationSession rtpPacketizationSession = new RTPPacketizationSession();
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
            catch(AVPacketProductionException exc_){

                int foo = 1;
                int bar = foo;
            }
        }

        private void packetize() throws AVPacketProductionException {

            AVPackets avPackets = avPacketProducer.produceAVPackets();
            Log.d(TAG, "produced: "+avPackets.getNumberOfPackets()+" avPackets");
            RTPPackets rtpPackets = rtpPacketizationSession.createRTPPackests(avPackets);
            Log.d(TAG, "created: "+rtpPackets.getNumberOfPackets()+" rtpPackets");
            int count = rtpPackets.getNumberOfPackets();
            for (RTPPacket rtpPacket : rtpPackets) {

                byte [] rtpPacketBytes = rtpPacket.toBytes();
                Log.d(TAG, rtpPacketBytes.length +" bytes will be sent");
                Message message = new Message(audioSocketAddress, rtpPacketBytes);
                audioSocketEngine.post(message);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class VideoSession implements Runnable{

        private static final String TAG = "ZZZ";

        AVPacketProducer avPacketProducer =
                //new AssetFileAVPacketProducer("H264_artifacts_motion.h264");
                new CameraVideoPacketProducer();

        RTPPacketizationSession rtpPacketizationSession = new RTPPacketizationSession();
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
            catch(AVPacketProductionException exc_){

                int foo = 1;
                int bar = foo;
            }
        }

        private void packetize() throws AVPacketProductionException {

            AVPackets avPackets = avPacketProducer.produceAVPackets();
            //Log.d(TAG, "produced: "+avPackets.getNumberOfPackets()+" avPackets");
            RTPPackets rtpPackets = rtpPacketizationSession.createRTPPackests(avPackets);
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
