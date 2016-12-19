package com.example.zebul.cameraservice.packet_producers;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacket;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacketProducer;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPacketProductionException;
import com.example.zebul.cameraservice.av_streaming.av_packet.AVPackets;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.header.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnit;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zebul on 12/8/16.
 */

public class MicrophoneAudioPacketProducer implements AVPacketProducer {

    private static final String TAG = "CameraPacketProducer";
    protected AudioRecord mAudioRecord = null;
    protected MediaCodec mMediaCodec;
    protected AudioQuality mQuality = DEFAULT_AUDIO_QUALITY;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private AVPackets avPackets = new AVPackets();
    private Clock clock = new Clock(mQuality.samplingRate);

    int bufferSize = 0;
    public MicrophoneAudioPacketProducer(){

        bufferSize = AudioRecord.getMinBufferSize(
                mQuality.samplingRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)*2;
        startProduction();
    }

    private void startProduction() {

        Thread productionThread = new Thread(new Runnable() {
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
        });

        productionThread.start();
    }

    private void configure() throws IOException {

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mQuality.samplingRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        mMediaCodec = MediaCodec.createEncoderByType("audio/mp4a-latm");
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
        format.setInteger(MediaFormat.KEY_BIT_RATE, mQuality.bitRate);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, mQuality.samplingRate);
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

                    byte [] data = new byte[mBufferInfo.size+NALUnit.START_CODES.length+4];

                    outputBuffer.get(data, NALUnit.START_CODES.length+4, mBufferInfo.size);
                    int offset = NALUnit.START_CODES.length;
                    data[0+offset] = 0;
                    data[1+offset] = 0x10;

                    /*
                    // AU-size
                    data[2] = (byte) (mBufferInfo.size>>5);
                    data[3] = (byte) (mBufferInfo.size<<3);

                    // AU-Index
                    data[3] &= 0xF8;
                    data[3] |= 0x00;
                    */

                    data[2+offset] = (byte) (((int) mBufferInfo.size & 0x1FE0) >> 5);
                    data[3+offset] = (byte) (((int) mBufferInfo.size & 0x001F) << 3);

                    //return mBufferInfo.presentationTimeUs;

                    NALUnit nalUnit = new NALUnit(0, data);
                    //long timestampInMillis = mBufferInfo.presentationTimeUs;
                    Timestamp timestamp = clock.getTimestamp(); /*new Timestamp(timestampInMillis)*/;
                    AVPacket avPacket = new AVPacket(nalUnit, timestamp);

                    synchronized (this){

                        avPackets.addAVPacket(avPacket);
                    }

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

    @Override
    public AVPackets produceAVPackets() throws AVPacketProductionException {

        synchronized (this) {

            AVPackets result = avPackets;
            avPackets = new AVPackets();
            Log.d(TAG, "avPackets contains "+result.getNumberOfPackets()+" packets");
            return result;
        }
    }


    public static class AudioQuality {

        /** Default audio stream quality. */


        /**	Represents a quality for a video stream. */
        public AudioQuality() {}

        /**
         * Represents a quality for an audio stream.
         * @param samplingRate The sampling rate
         * @param bitRate The bitrate in bit per seconds
         */
        public AudioQuality(int samplingRate, int bitRate) {
            this.samplingRate = samplingRate;
            this.bitRate = bitRate;
        }

        public int samplingRate = 0;
        public int bitRate = 0;

        public boolean equals(AudioQuality quality) {
            if (quality==null) return false;
            return (quality.samplingRate == this.samplingRate 				&
                    quality.bitRate == this.bitRate);
        }

        public AudioQuality clone() {
            return new AudioQuality(samplingRate, bitRate);
        }

    }

    public final static AudioQuality DEFAULT_AUDIO_QUALITY = new AudioQuality(8000,32000);

    public static AudioQuality parseQuality(String str) {
        AudioQuality quality = DEFAULT_AUDIO_QUALITY.clone();
        if (str != null) {
            String[] config = str.split("-");
            try {
                quality.bitRate = Integer.parseInt(config[0])*1000; // conversion to bit/s
                quality.samplingRate = Integer.parseInt(config[1]);
            }
            catch (IndexOutOfBoundsException ignore) {}
        }
        return quality;
    }

}
