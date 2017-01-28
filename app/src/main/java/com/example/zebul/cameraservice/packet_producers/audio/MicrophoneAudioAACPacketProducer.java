package com.example.zebul.cameraservice.packet_producers.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.zebul.cameraservice.packet_producers.MediaCodecPacketProducer;
import com.example.zebul.cameraservice.packet_producers.PacketProductionException;
import com.example.zebul.cameraservice.packet_producers.PacketProductionExceptionListener;
import com.example.zebul.cameraservice.av_streaming.AudioSettings;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.aac.AccessUnit;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zebul on 12/8/16.
 */

public class MicrophoneAudioAACPacketProducer extends MediaCodecPacketProducer {

    private static final String TAG = MicrophoneAudioAACPacketProducer.class.getSimpleName();
    private static final long INPUT_BUFFER_TIMEOUT_US = 10000;

    private AACPacketListener aacPacketListener;

    private AudioSettings audioSettings;
    private AudioRecord audioRecord;

    private Clock clock;
    private int bufferSize = 0;

    public MicrophoneAudioAACPacketProducer(
            AACPacketListener aacPacketListener,
            PacketProductionExceptionListener packetProductionExceptionListener){

        super(packetProductionExceptionListener);
        this.aacPacketListener = aacPacketListener;
        bufferInfo = new MediaCodec.BufferInfo();
    }

    public boolean start(MicrophoneSettings microphoneSettings) {

        this.audioSettings = microphoneSettings.getAudioSettings();
        return super.start();
    }

    @Override
    protected void open() throws PacketProductionException {

        try {

            clock = new Clock(audioSettings.getSamplingRate());

            bufferSize = AudioRecord.getMinBufferSize(
                    audioSettings.getSamplingRate(),
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT) * 2;

            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    audioSettings.getSamplingRate(),
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            final String mimeType = "audio/mp4a-latm";
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
        catch(IOException exc){

            throw new PacketProductionException(exc);
        }
    }

    @Override
    protected void close()throws PacketProductionException{

        super.close();
        if(audioRecord != null){
            audioRecord.stop();
            audioRecord.release();
        }
    }

    @Override
    protected void produce() throws PacketProductionException, InterruptedException{

        flushMediaCodecInput();
        flushMediaCodecOutput();
    }

    private void flushMediaCodecInput(){

        int inputBufferIndex = mediaCodec.dequeueInputBuffer(INPUT_BUFFER_TIMEOUT_US);
        if ( inputBufferIndex>=0 ) {

            onFlushMediaCodecInputSuccess(inputBufferIndex);
        }
        else{

            onFlushMediaCodecInputFailure(inputBufferIndex);
        }
    }

    private void onFlushMediaCodecInputSuccess(int inputBufferIndex) {

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

    private void onFlushMediaCodecInputFailure(int inputBufferId) {

    }

    @Override
    protected void onFlushMediaCodecOutputSuccess(int outputBufferIndex) {

        final ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)
        {
            Log.d(TAG, "Got config bytes");
        }

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0)
        {
            Log.d(TAG, "Got Sync Frame");
        }

        if (bufferInfo.size != 0)
        {
            // adjust the ByteBuffer values to match BufferInfo (not needed?)
            outputBuffer.position(bufferInfo.offset);
            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
            Log.d(TAG, "Audio data out len: " + bufferInfo.size);
            byte [] accessUnitData = new byte[bufferInfo.size];
            outputBuffer.get(accessUnitData, 0, bufferInfo.size);

            AccessUnit accessUnit = new AccessUnit(accessUnitData);
            Timestamp timestamp = clock.getTimestamp();
            AACPacket aacPacket = new AACPacket(accessUnit, timestamp);

            aacPacketListener.onAACPacket(aacPacket);
            Log.d(TAG, "added " + bufferInfo.size + " bytes to sent + presentation time ms"+timestamp.getTimestampInMillis());
        }

        mediaCodec.releaseOutputBuffer(outputBufferIndex, false);

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
        {
            // Stream is marked as done,
            // break out of while
            Log.d(TAG, "Marked EOS");
        }
    }
}
