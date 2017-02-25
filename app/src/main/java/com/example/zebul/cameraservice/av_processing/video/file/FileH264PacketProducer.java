package com.example.zebul.cameraservice.av_processing.video.file;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.zebul.cameraservice.NALUnitReader;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.av_protocols.rtp.Clock;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.av_processing.video.H264PacketProducer;
import com.example.zebul.cameraservice.av_processing.PacketProcessingException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zebul on 11/16/16.
 */

public class FileH264PacketProducer implements H264PacketProducer {

    private byte [] data = null;
    private NALUnitReader reader;
    private Clock clock = new Clock();
    private Context context;

    public FileH264PacketProducer(Context context, String assetFileName){

        this.context = context;
        data = readFileData(assetFileName);
        if(data != null){
            reader = new NALUnitReader(data);
        }
    }

    @Override
    public H264Packets produceH264Packets() throws PacketProcessingException {

        if(reader == null){
            throw new PacketProcessingException("data unavailable");
        }

        NALUnit nalUnit = reader.read();
        H264Packet h264Packet = new H264Packet(nalUnit, clock.getTimestamp());
        H264Packets h264Packets = new H264Packets();
        h264Packets.addPacket(h264Packet);
        return h264Packets;
    }


    private byte [] readFileData(String assetFileName){

        try {
            AssetManager assetManager = context.getAssets();
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
