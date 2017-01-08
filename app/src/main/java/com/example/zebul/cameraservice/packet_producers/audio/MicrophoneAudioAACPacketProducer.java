package com.example.zebul.cameraservice.packet_producers.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.zebul.cameraservice.packet_producers.IllegalProductionStateException;
import com.example.zebul.cameraservice.packet_producers.PacketProductionException;
import com.example.zebul.cameraservice.packet_producers.PacketProductionExceptionListener;
import com.example.zebul.cameraservice.av_streaming.rtsp.audio.AudioSettings;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AccessUnit;
import com.example.zebul.cameraservice.packet_producers.ProductionThread;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zebul on 12/8/16.
 */

public class MicrophoneAudioAACPacketProducer implements Runnable{

    private static final String TAG = MicrophoneAudioAACPacketProducer.class.getSimpleName();
    private static final long OUTPUT_BUFFER_TIMEOUT_US = 500000;
    private static final long INPUT_BUFFER_TIMEOUT_US = 10000;

    private ProductionThread engine = new ProductionThread();
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private AACPacketListener aacPacketListener;
    private PacketProductionExceptionListener packetProductionExceptionListener;

    private AudioSettings audioSettings;
    private AudioRecord audioRecord;
    private MediaCodec mediaCodec;

    private Clock clock;
    private int bufferSize = 0;

    public MicrophoneAudioAACPacketProducer(
            AACPacketListener aacPacketListener,
            PacketProductionExceptionListener packetProductionExceptionListener){

        this.aacPacketListener = aacPacketListener;
        this.packetProductionExceptionListener = packetProductionExceptionListener;
    }

    public void start(MicrophoneSettings microphoneSettings) {

        this.audioSettings = microphoneSettings.getAudioSettings();
        try {
            engine.start(this);
        } catch (IllegalProductionStateException exc) {
            packetProductionExceptionListener.onPacketProductionException(exc);
        }
    }

    public void stop() {

        try {
            engine.stop();
        } catch (IllegalProductionStateException exc) {
            packetProductionExceptionListener.onPacketProductionException(exc);
        }
    }

    @Override
    public void run() {

        try {
            configure();
            while(!Thread.interrupted()){

                produce();
            }
        } catch (Exception exc) {

            PacketProductionException ppe = new PacketProductionException(exc);
            packetProductionExceptionListener.onPacketProductionException(ppe);
        }
        finally {

            tryRelease();
        }
    }

    private void tryRelease() {

        try {
            release();
        } catch (Exception exc) {
            PacketProductionException ppe = new PacketProductionException(exc);
            packetProductionExceptionListener.onPacketProductionException(ppe);
        }
    }

    private void release() {

        if(mediaCodec != null){
            //mediaCodec.stop();
            mediaCodec.release();
        }
        if(audioRecord != null){
            audioRecord.stop();
            audioRecord.release();
        }
    }

    private void configure() throws IOException {

        clock = new Clock(audioSettings.getSamplingRate());

        bufferSize = AudioRecord.getMinBufferSize(
                audioSettings.getSamplingRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)*2;

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                audioSettings.getSamplingRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        final String mimeType =  "audio/mp4a-latm";
        mediaCodec = MediaCodec.createEncoderByType(mimeType);
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, mimeType);
        format.setInteger(MediaFormat.KEY_BIT_RATE, audioSettings.getBitRate());
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, audioSettings.getSamplingRate());
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSize);
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        audioRecord.startRecording();
        mediaCodec.start();
    }

    private void produce() {

        manageInput();
        manageOutput();
    }

    private void manageInput(){

        int inputBufferIndex = mediaCodec.dequeueInputBuffer(INPUT_BUFFER_TIMEOUT_US);
        if ( inputBufferIndex>=0 ) {

            onInputSuccess(inputBufferIndex);
        }
        else{

            onInputFailure(inputBufferIndex);
        }
    }

    private void manageOutput() {

        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(mBufferInfo, OUTPUT_BUFFER_TIMEOUT_US);
        if ( outputBufferIndex>=0 ){

            onOutputSuccess(outputBufferIndex);
        }
        else{

            onOutputFailure(outputBufferIndex);
        }
    }

    private void onInputSuccess(int inputBufferIndex) {

        final ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        inputBuffers[inputBufferIndex].clear();
        int len = audioRecord.read(inputBuffers[inputBufferIndex], bufferSize);
        if (len ==  AudioRecord.ERROR_INVALID_OPERATION) {
            Log.e(TAG, "AudioRecord.ERROR_INVALID_OPERATION");
        }
        else if (len == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "AudioRecord.ERROR_BAD_VALUE");
        }
        else {
            long presentationTimeUs = System.nanoTime()/1000;
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, len, presentationTimeUs, 0);
        }
    }

    private void onInputFailure(int inputBufferId) {

    }

    private void onOutputSuccess(int outputBufferIndex) {

        final ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)
        {
            Log.d(TAG, "Got config bytes");
        }

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0)
        {
            Log.d(TAG, "Got Sync Frame");
        }

        if (mBufferInfo.size != 0)
        {
            // adjust the ByteBuffer values to match BufferInfo (not needed?)
            outputBuffer.position(mBufferInfo.offset);
            outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
            Log.d(TAG, "Audio data out len: " + mBufferInfo.size);
            byte [] accessUnitData = new byte[mBufferInfo.size];
            outputBuffer.get(accessUnitData, 0, mBufferInfo.size);

            AccessUnit accessUnit = new AccessUnit(accessUnitData);
            Timestamp timestamp = clock.getTimestamp();
            AACPacket aacPacket = new AACPacket(accessUnit, timestamp);

            aacPacketListener.onAACPacket(aacPacket);
            Log.d(TAG, "added " + mBufferInfo.size + " bytes to sent + presentation time ms"+timestamp.getTimestampInMillis());
        }

        mediaCodec.releaseOutputBuffer(outputBufferIndex, false);

        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
        {
            // Stream is marked as done,
            // break out of while
            Log.d(TAG, "Marked EOS");
        }
    }

    private void onOutputFailure(int infoIndex) {

        switch(infoIndex){
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                break;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                break;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                break;
        }
    }
}
