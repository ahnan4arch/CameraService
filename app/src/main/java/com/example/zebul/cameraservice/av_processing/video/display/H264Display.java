package com.example.zebul.cameraservice.av_processing.video.display;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import com.example.zebul.cameraservice.ManualResetEvent;
import com.example.zebul.cameraservice.av_processing.MediaCodecPacketProcessor;
import com.example.zebul.cameraservice.av_processing.PacketProcessingException;
import com.example.zebul.cameraservice.av_processing.PacketProcessingExceptionListener;
import com.example.zebul.cameraservice.av_processing.video.H264PacketConsumer;
import com.example.zebul.cameraservice.av_processing.video.camera.H264Camera;
import com.example.zebul.cameraservice.av_processing.video.camera.Resolution;
import com.example.zebul.cameraservice.av_processing.video.camera.VideoSettings;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.NALUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 2/22/17.
 */

public class H264Display extends MediaCodecPacketProcessor
implements H264PacketConsumer {

    public static final String TAG = H264Display.class.getSimpleName();

    private List<H264Packet> listOfConsumedH264Packets = Collections.synchronizedList(new LinkedList<H264Packet>());
    private ManualResetEvent eventConsumed = new ManualResetEvent(false);
    private VideoSettings videoSettings;

    private Surface surface;
    private volatile boolean processConsumedPacket = false;

    public H264Display(
            PacketProcessingExceptionListener packetProcessingExceptionListener,
            Surface surface){

        super(packetProcessingExceptionListener);
        this.surface = surface;
        inputBufferTimeoutInUs = 15000;
        outputBufferTimeoutInUs = 15000;
    }

    @Override
    protected void open() throws PacketProcessingException {

        try {
            bufferInfo = new MediaCodec.BufferInfo();//find best place to init bufferInfo

            final Resolution res = videoSettings.getResolution();
            MediaFormat format = MediaFormat.createVideoFormat(
                    H264Camera.MIME_TYPE, res.getWidth(), res.getHeight());

            format.setInteger(MediaFormat.KEY_BIT_RATE, videoSettings.getBitRate());
            format.setInteger(MediaFormat.KEY_FRAME_RATE, videoSettings.getFrameRate());
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            mediaCodec = MediaCodec.createDecoderByType(H264Camera.MIME_TYPE);
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();

        } catch (IOException exc) {
            throw new PacketProcessingException(exc);
        }
    }

    public boolean start(VideoSettings videoSettings) {

        this.videoSettings = videoSettings;
        processConsumedPacket = true;
        return super.start();
    }

    @Override
    public void stop() {

        processConsumedPacket = false;
        eventConsumed.set();
        super.stop();
    }

    @Override
    public void consumeH264Packet(H264Packet h264Packet){

        listOfConsumedH264Packets.add(h264Packet);
        eventConsumed.set();
    }

    public void onInputBufferAvailable(
            int inputBufferIndex,
            ByteBuffer inputBuffer,
            MediaCodec mediaCodec){

        if(listOfConsumedH264Packets.isEmpty()){

            waitForH264PacketConsumption();
        }

        if(!processConsumedPacket)
        {
            return;
        }

        final H264Packet h264Packet = listOfConsumedH264Packets.remove(0);
        final NALUnit nalUnit = h264Packet.getNALUnit();
        final Timestamp timestamp = h264Packet.getTimestamp();

        byte [] nalUnitData = nalUnit.getData();
        inputBuffer.put(nalUnitData, 0, nalUnitData.length);
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, nalUnitData.length,
                timestamp.getTimestampInMillis(), 0);
    }

    private void waitForH264PacketConsumption(){

        eventConsumed.reset();
        try {
            eventConsumed.waitOne();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOutputBufferAvailable(int outputBufferIndex) {

        boolean doRender = (bufferInfo.size != 0);
        mediaCodec.releaseOutputBuffer(outputBufferIndex, doRender);
    }
}
