package com.example.zebul.cameraservice.av_processing.audio.speaker;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaFormat;

import com.example.zebul.cameraservice.ManualResetEvent;
import com.example.zebul.cameraservice.av_processing.MediaCodecPacketProcessor;
import com.example.zebul.cameraservice.av_processing.PacketProcessingException;
import com.example.zebul.cameraservice.av_processing.PacketProcessingExceptionListener;
import com.example.zebul.cameraservice.av_processing.audio.AACPacketConsumer;
import com.example.zebul.cameraservice.av_processing.audio.AudioSettings;
import com.example.zebul.cameraservice.av_processing.audio.microphone.AACMicrophone;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;
import com.example.zebul.cameraservice.av_protocols.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.aac.AccessUnit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 2/15/17.
 */

public class AACSpeaker
        extends MediaCodecPacketProcessor
        implements AACPacketConsumer, Runnable {

    private static final int TIMEOUT_US = 1000;
    private ManualResetEvent event = new ManualResetEvent(false);
    private List<AACPacket> aacPackets = new LinkedList<>();
    private AudioTrack audioTrack;

    protected AACSpeaker(PacketProcessingExceptionListener packetProcessingExceptionListener) {
        super(packetProcessingExceptionListener);

        bufferInfo = new MediaCodec.BufferInfo();
        inputBufferTimeoutInUs = TIMEOUT_US;
        outputBufferTimeoutInUs = TIMEOUT_US;
    }

    @Override
    public void consumeAACPacket(AACPacket aacPacket) {

        synchronized (this) {
            aacPackets.add(aacPacket);
        }
        event.set();
    }

    public void doStart() throws IOException {

        super.start();
    }

    @Override
    public void process(){

        List<AACPacket> aacPacketsToProcess = null;
        synchronized (this){
            aacPacketsToProcess = aacPackets;
            aacPackets = new LinkedList<>();
        }

        for(AACPacket aacPacket: aacPacketsToProcess){

            processAACPacket(aacPacket);
        }
        event.reset();
        try {
            event.waitOne();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void open() throws PacketProcessingException {

        try {
            AudioSettings audioSettings = AudioSettings.DEFAULT;

            int bufferSize = AudioRecord.getMinBufferSize(
                    audioSettings.getSamplingRate(),
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT) * 2;

            MediaFormat format = AACMicrophone.createMediaFormat(audioSettings, bufferSize);
            if (format == null)
                return;

            mediaCodec = MediaCodec.createDecoderByType(AACMicrophone.MIME_TYPE);
            mediaCodec.configure(format, null, null, 0);

            if (mediaCodec == null) {
                return;
            }

            mediaCodec.start();

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC, audioSettings.getSamplingRate(),
                    AudioFormat.CHANNEL_OUT_MONO,//CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
        }
        catch(IOException exc){

            throw new PacketProcessingException(exc);
        }
    }

    private void processAACPacket(AACPacket aacPacket) {

        int decoderInputStatus = mediaCodec.dequeueInputBuffer(TIMEOUT_US);
        if(decoderInputStatus >= 0){
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer inputBuffer = inputBuffers[decoderInputStatus];
            final Timestamp timestamp = aacPacket.getTimestamp();
            final AccessUnit accessUnit = aacPacket.getAccessUnit();
            final byte[] accessUnitData = accessUnit.getData();
            inputBuffer.put(accessUnitData, 0, accessUnitData.length);
            mediaCodec.queueInputBuffer(decoderInputStatus, 0, accessUnitData.length, timestamp.getTimestampInMillis()*1000, 0);
        }

        int decoderOutputStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);
        if (decoderOutputStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // no output available yet
            //Log.d(TAG, "no output from mediaCodec available");
        } else if (decoderOutputStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            // not important for us, since we're using Surface
            //Log.d(TAG, "mediaCodec output buffers changed");
        } else if (decoderOutputStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

        } else if (decoderOutputStatus < 0) {
            throw new RuntimeException(
                    "unexpected result from mediaCodec.dequeueOutputBuffer: " +
                            decoderOutputStatus);
        } else {
            onFlushMediaCodecOutputSuccess(decoderOutputStatus);
        }
    }

    protected void onFlushMediaCodecOutputSuccess(int outputBufferIndex) {

        final ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
        outputBuffer.position(bufferInfo.offset);
        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);

        byte [] data = new byte[bufferInfo.size];
        outputBuffer.get(data, bufferInfo.offset, data.length);

        audioTrack.write(data, bufferInfo.offset, bufferInfo.offset + bufferInfo.size); // AudioTrack write data
        mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
    }


}
