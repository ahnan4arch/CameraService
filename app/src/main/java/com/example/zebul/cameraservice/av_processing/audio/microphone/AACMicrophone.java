package com.example.zebul.cameraservice.av_processing.audio.microphone;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.zebul.cameraservice.av_processing.MediaCodecPacketProcessor;
import com.example.zebul.cameraservice.av_processing.PacketProcessingException;
import com.example.zebul.cameraservice.av_processing.PacketProcessingExceptionListener;
import com.example.zebul.cameraservice.av_processing.audio.AACPacketConsumer;
import com.example.zebul.cameraservice.av_processing.audio.AudioSettings;
import com.example.zebul.cameraservice.av_processing.audio.MicrophoneSettings;
import com.example.zebul.cameraservice.av_protocols.rtp.aac.AACPacket;
import com.example.zebul.cameraservice.av_protocols.rtp.Clock;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;
import com.example.zebul.cameraservice.av_protocols.rtp.aac.AccessUnit;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zebul on 12/8/16.
 */

public class AACMicrophone extends MediaCodecPacketProcessor {

    public static final String MIME_TYPE = "audio/mp4a-latm";
    private static final String TAG = AACMicrophone.class.getSimpleName();

    private AACPacketConsumer aacPacketConsumer;
    private AudioSettings audioSettings;
    private AudioRecord audioRecord;

    private Clock clock;
    private int bufferSize = 0;

    public AACMicrophone(
            AACPacketConsumer aacPacketListener,
            PacketProcessingExceptionListener packetProductionExceptionListener){

        super(packetProductionExceptionListener);
        this.aacPacketConsumer = aacPacketListener;
        inputBufferTimeoutInMicroSeconds = 10000;
        outputBufferTimeoutInMicroSeconds = 10000;//(1/8000)*1000000;//bez sensu???
    }

    public boolean start(MicrophoneSettings microphoneSettings) {

        this.audioSettings = microphoneSettings.getAudioSettings();
        return super.start();
    }

    @Override
    protected void open() throws PacketProcessingException {

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

            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            MediaFormat format = createMediaFormat(audioSettings, bufferSize);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            audioRecord.startRecording();
            mediaCodec.start();
        }
        catch(IOException exc){

            throw new PacketProcessingException(exc);
        }
    }

    public static MediaFormat createMediaFormat(AudioSettings audioSettings, int bufferSize){

        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, MIME_TYPE);
        format.setInteger(MediaFormat.KEY_BIT_RATE, audioSettings.getBitRate());
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, audioSettings.getSamplingRate());
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSize);
        return format;
    }

    @Override
    protected void close()throws PacketProcessingException {

        super.close();
        if(audioRecord != null){
            audioRecord.stop();
            audioRecord.release();
        }
    }

    @Override
    protected void onInputBufferAvailable(int inputBufferIndex, ByteBuffer inputBuffer) {

        inputBuffer.clear();
        int len = audioRecord.read(inputBuffer, bufferSize);
        if (len ==  AudioRecord.ERROR_INVALID_OPERATION) {
            Log.e(TAG, "AudioRecord.ERROR_INVALID_OPERATION");
        }
        else if (len == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "AudioRecord.ERROR_BAD_VALUE");
        }
        else {
            long presentationTimeUs = clock.getTimestamp().getTimestampInMillis()*1000; //System.nanoTime()/1000;
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, len, presentationTimeUs, 0);
        }
    }

    @Override
    protected void onOutputBufferAvailable(int outputBufferIndex, ByteBuffer outputBuffer) {

        if (bufferInfo.size != 0)
        {
            // adjust the ByteBuffer values to match BufferInfo (not needed?)
            outputBuffer.position(bufferInfo.offset);
            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
            byte [] accessUnitData = new byte[bufferInfo.size];
            outputBuffer.get(accessUnitData, 0, bufferInfo.size);

            AccessUnit accessUnit = new AccessUnit(accessUnitData);
            Timestamp timestamp = clock.getTimestamp();
            AACPacket aacPacket = new AACPacket(accessUnit, timestamp);
            aacPacketConsumer.consumeAACPacket(aacPacket);
        }
        mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
    }
}
