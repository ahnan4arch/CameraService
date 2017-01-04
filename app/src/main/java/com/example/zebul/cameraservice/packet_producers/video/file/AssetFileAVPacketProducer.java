package com.example.zebul.cameraservice.packet_producers.video.file;

import android.content.res.AssetManager;

import com.example.zebul.cameraservice.CameraService;
import com.example.zebul.cameraservice.NALUnitReader;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264Packet;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264Packets;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264PacketProducer;
import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zebul on 11/16/16.
 */

public class AssetFileAVPacketProducer implements H264PacketProducer {

    private byte [] data = null;
    private NALUnitReader reader;
    private Clock clock = new Clock();
    public AssetFileAVPacketProducer(String assetFileName){

        data = readFileData(assetFileName);
        if(data != null){
            reader = new NALUnitReader(data);
        }
    }

    @Override
    public H264Packets produceH264Packets() throws PacketProductionException {

        if(reader == null){
            throw new PacketProductionException("data unavailable");
        }

        NALUnit nalUnit = reader.read();
        H264Packet h264Packet = new H264Packet(nalUnit, clock.getTimestamp());
        H264Packets h264Packets = new H264Packets();
        h264Packets.addPacket(h264Packet);
        return h264Packets;
    }


    private byte [] readFileData(String assetFileName){

        try {
            AssetManager assetManager = CameraService.CAMERA_SERVICE.getAssets();
            InputStream inputStream = assetManager.open(assetFileName);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int readCount = 0;
            byte[] data = new byte[1024];
            while ((readCount = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readCount);
            }
            buffer.flush();
            return buffer.toByteArray();

        } catch (FileNotFoundException exc_) {
            exc_.printStackTrace();
            return null;
        } catch (IOException exc_) {
            exc_.printStackTrace();
            return null;
        }
    }
}
