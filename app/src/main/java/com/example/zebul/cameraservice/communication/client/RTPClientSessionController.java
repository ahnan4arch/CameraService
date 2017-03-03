package com.example.zebul.cameraservice.communication.client;

import com.example.zebul.cameraservice.av_protocols.rtp.aac.AACDepacketizer;
import com.example.zebul.cameraservice.av_protocols.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.aac.AACPackets;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Depacketizer;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.communication.udp.SocketEngine;
import com.example.zebul.cameraservice.communication.udp.SocketMessageReceptionListener;
import com.example.zebul.cameraservice.message.Message;
import com.example.zebul.cameraservice.av_processing.audio.AACPacketConsumer;
import com.example.zebul.cameraservice.av_processing.video.H264PacketConsumer;

/**
 * Created by zebul on 1/30/17.
 */

public class RTPClientSessionController implements RTPSessionLifecycleListener {

    private ClientSessionSettings sessionSettings;
    private H264PacketConsumer h264PacketConsumer;
    private AACPacketConsumer aacPacketConsumer;
    private SocketEngine videoSocketEngine;
    private SocketEngine audioSocketEngine;

    private H264Depacketizer h264Depacketizer = new H264Depacketizer();
    private AACDepacketizer aacDepacketizer = new AACDepacketizer();

    public RTPClientSessionController(
            ClientSessionSettings sessionSettings,
            H264PacketConsumer h264PacketConsumer,
            AACPacketConsumer aacPacketConsumer){

        this.sessionSettings = sessionSettings;
        this.h264PacketConsumer = h264PacketConsumer;
        this.aacPacketConsumer = aacPacketConsumer;

        videoSocketEngine = new SocketEngine(sessionSettings.VideoMinPort, new SocketMessageReceptionListener() {
            @Override
            public void onSocketMessageReceived(Message message_) {

                onVideoMessageReceived(message_);
            }
        });

        audioSocketEngine = new SocketEngine(sessionSettings.AudioMinPort, new SocketMessageReceptionListener() {
            @Override
            public void onSocketMessageReceived(Message message_) {

                onAudioMessageReceived(message_);
            }
        });
    }

    private void onVideoMessageReceived(Message message_){

        byte [] data = (byte [])message_.getData();
        final H264Packets h264Packets = h264Depacketizer.createH264Packets(data);//refactor to never run
        if(h264Packets == null){
            return;
        }
        for(H264Packet h264Packet: h264Packets){
            h264PacketConsumer.consumeH264Packet(h264Packet);
        }
    }

    private void onAudioMessageReceived(Message message_){

        byte [] data = (byte [])message_.getData();
        final AACPackets aacPackets = aacDepacketizer.createAACPackets(data);//refactor to never run
        if(aacPackets == null){
            return;
        }
        for(AACPacket aacPacket: aacPackets){
            aacPacketConsumer.consumeAACPacket(aacPacket);
        }
    }

    public ClientSessionSettings getSessionSettings() {
        return sessionSettings;
    }

    @Override
    public void onRTPSetupAudioSession() {

    }

    @Override
    public void onRTPSetupVideoSession() {

    }

    @Override
    public void onRTPPlay() {

        videoSocketEngine.start();
        audioSocketEngine.start();
    }

    @Override
    public void onRTPTearDownSession() {

        videoSocketEngine.stop();
        audioSocketEngine.stop();
    }

}
