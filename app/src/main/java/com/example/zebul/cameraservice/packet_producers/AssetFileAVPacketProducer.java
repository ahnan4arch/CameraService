package com.example.zebul.cameraservice.packet_producers;

import android.content.res.AssetManager;

import com.example.zebul.cameraservice.CameraService;
import com.example.zebul.cameraservice.NALUnitReader;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnit;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacket;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPackets;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacketProducer;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacketProductionException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zebul on 11/16/16.
 */

public class AssetFileAVPacketProducer implements AVPacketProducer {

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
    public AVPackets produceAVPackets() throws AVPacketProductionException {

        if(reader == null){
            throw new AVPacketProductionException("data unavailable");
        }

        NALUnit nalUnit = reader.read();
        AVPacket videoData = new AVPacket(nalUnit, clock.getTimestamp());
        AVPackets videoDataPack = new AVPackets();
        videoDataPack.addAVPacket(videoData);
        return videoDataPack;
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
