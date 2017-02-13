package com.example.zebul.cameraservice.communication.client;

import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Depacketizer;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.communication.udp.SocketEngine;
import com.example.zebul.cameraservice.communication.udp.SocketMessageReceptionListener;
import com.example.zebul.cameraservice.message.Message;
import com.example.zebul.cameraservice.packet_producers.PacketConsumptionException;
import com.example.zebul.cameraservice.packet_producers.video.H264PacketConsumer;

/**
 * Created by zebul on 1/30/17.
 */

public class RTPClientSessionController implements
        RTPSessionLifecycleListener, SocketMessageReceptionListener {

    private ClientSessionSettings sessionSettings;
    private H264PacketConsumer h264PacketConsumer;
    private SocketEngine socketEngine;
    private H264Depacketizer h264Depacketizer = new H264Depacketizer();

    public RTPClientSessionController(ClientSessionSettings sessionSettings,
                                      H264PacketConsumer h264PacketConsumer){

        this.sessionSettings = sessionSettings;
        this.h264PacketConsumer = h264PacketConsumer;
        socketEngine = new SocketEngine(sessionSettings.MinPort, this);
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

        socketEngine.start();
    }

    @Override
    public void onRTPTearDownSession() {

        socketEngine.stop();
    }

    @Override
    public void onSocketMessageReceived(Message message_) {

        byte [] data = (byte [])message_.getData();
        final H264Packets h264Packets = h264Depacketizer.createH264Packets(data);//refactor to never run
        if(h264Packets == null){
            return;
        }
        for(H264Packet h264Packet: h264Packets){
            h264PacketConsumer.consumeH264Packet(h264Packet);
        }
    }
}
