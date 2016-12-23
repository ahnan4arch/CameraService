package com.example.zebul.cameraservice.packet_producers.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacketListener;
import com.example.zebul.cameraservice.av_streaming.rtsp.AudioSettings;
import com.example.zebul.cameraservice.av_streaming.av_packet.aac.AACPacket;
import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionException;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AccessUnit;
import com.example.zebul.cameraservice.packet_producers.ProductionEngine;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zebul on 12/8/16.
 */

public class MicrophoneAudioAACPacketProducer implements Runnable{

    private static final String TAG = "CameraPacketProducer";
    private AudioSettings audioSettings;
    private AACPacketListener aacPacketListener;

    protected AudioRecord mAudioRecord = null;
    protected MediaCodec mMediaCodec;

    private ProductionEngine engine = new ProductionEngine();
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private Clock clock;

    private int bufferSize = 0;
    public MicrophoneAudioAACPacketProducer(
            AudioSettings audioSettings,
            AACPacketListener aacPacketListener){

        this.audioSettings = audioSettings;
        this.aacPacketListener = aacPacketListener;

        clock = new Clock(audioSettings.getSamplingRate());

        bufferSize = AudioRecord.getMinBufferSize(
                audioSettings.getSamplingRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)*2;
    }

    public void start(){

        engine.start(this);
    }

    public void stop() {

        engine.stop();
    }

    @Override
    public void run() {

        try {
            configure();
            while(!Thread.interrupted()){

                produce();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configure() throws IOException {

        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                audioSettings.getSamplingRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        mMediaCodec = MediaCodec.createEncoderByType("audio/mp4a-latm");
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
        format.setInteger(MediaFormat.KEY_BIT_RATE, audioSettings.getBitRate());
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, audioSettings.getSamplingRate());
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSize);
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioRecord.startRecording();
        mMediaCodec.start();
    }

    private void produce() {

        manageInput();
        manageOutput();
    }

    private void manageInput(){

        try {
            long timeoutUs = 10000;
            int inputBufferId = mMediaCodec.dequeueInputBuffer(timeoutUs);
            if (inputBufferId>=0) {
                final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
                inputBuffers[inputBufferId].clear();
                int len = mAudioRecord.read(inputBuffers[inputBufferId], bufferSize);
                if (len ==  AudioRecord.ERROR_INVALID_OPERATION || len == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG,"An error occured with the AudioRecord API !");
                    int foo = 1;
                    int bar = foo;
                } else {
                    //Log.v(TAG,"Pushing raw audio to the decoder: len="+len+" bs: "+inputBuffers[bufferIndex].capacity());
                    mMediaCodec.queueInputBuffer(inputBufferId, 0, len, System.nanoTime()/1000, 0);
                }

            }
        } catch (Exception e) {

            int foo = 1;
            int bar = foo;
            e.printStackTrace();
        }
    }

    private void manageOutput() {

        long timeoutUs = 500000;
        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, timeoutUs);
        if ( outputBufferIndex>=0 ){
            //Log.d(TAG,"Index: "+mIndex+" Time: "+mBufferInfo.presentationTimeUs+" size: "+mBufferInfo.size);
            final ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();

            Log.d(TAG, "Queue Buffer out " + outputBufferIndex);
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)
            {
                // Config Bytes means SPS and PPS
                Log.d(TAG, "Got config bytes");
            }

            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0)
            {
                // Marks a Keyframe
                Log.d(TAG, "Got Sync Frame");
            }

            if (mBufferInfo.size != 0)
            {
                // adjust the ByteBuffer values to match BufferInfo (not needed?)
                outputBuffer.position(mBufferInfo.offset);
                outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                Log.d(TAG, "Audio data out len: " + mBufferInfo.size);
                try{

                    byte [] accessUnitData = new byte[mBufferInfo.size];
                    outputBuffer.get(accessUnitData, 0, mBufferInfo.size);

                    AccessUnit accessUnit = new AccessUnit(accessUnitData);
                    Timestamp timestamp = clock.getTimestamp();
                    AACPacket aacPacket = new AACPacket(accessUnit, timestamp);

                    aacPacketListener.onAACPacket(aacPacket);
                    Log.d(TAG, "added " + mBufferInfo.size + " bytes to sent + presentation time ms"+timestamp.getTimestampInMillis());
                }
                catch(Exception exc_){

                    int foo = 1;
                    int bar = foo;
                }
            }

            mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);

            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
            {
                // Stream is marked as done,
                // break out of while
                Log.d(TAG, "Marked EOS");
                int foo = 1;
                int bar = foo;
            }

        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

            int foo = 1;
            int bar = foo;
        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

            int foo = 1;
            int bar = foo;
        } else if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int foo = 1;
            int bar = foo;
        } else {

            int foo = 1;
            int bar = foo;
        }
    }
}
