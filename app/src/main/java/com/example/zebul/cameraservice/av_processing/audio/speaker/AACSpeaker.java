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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 2/15/17.
 */

public class AACSpeaker extends MediaCodecPacketProcessor
        implements AACPacketConsumer {

    private List<AACPacket> listOfConsumedAACPackets =
            Collections.synchronizedList(new LinkedList<AACPacket>());
    private ManualResetEvent packetConsumedEvent = new ManualResetEvent(false);

    private AudioTrack audioTrack;
    private volatile boolean processConsumedPacket = false;
    private AudioSettings audioSettings;

    public AACSpeaker(PacketProcessingExceptionListener packetProcessingExceptionListener) {
        super(packetProcessingExceptionListener);
        inputBufferTimeoutInMicroSeconds = 1000;
        outputBufferTimeoutInMicroSeconds = 1000;
    }

    public void start(AudioSettings audioSettings) throws IOException {

        this.audioSettings = audioSettings;
        processConsumedPacket = true;
        super.start();
    }

    @Override
    public void stop() {

        processConsumedPacket = false;
        packetConsumedEvent.set();
        super.stop();
    }

    @Override
    protected void open() throws PacketProcessingException {

        try {

            int bufferSize = AudioRecord.getMinBufferSize(
                    audioSettings.getSamplingRate(),
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT) * 2;

            MediaFormat format = AACMicrophone.createMediaFormat(audioSettings, bufferSize);
            if (format == null)
                return;

            mediaCodec = MediaCodec.createDecoderByType(AACMicrophone.MIME_TYPE);
            mediaCodec.configure(format, null, null, 0);

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

    @Override
    public void consumeAACPacket(AACPacket aacPacket) {

        listOfConsumedAACPackets.add(aacPacket);
        packetConsumedEvent.set();
    }

    @Override
    protected void onInputBufferAvailable(
            int inputBufferIndex,
            ByteBuffer inputBuffer){

        if(listOfConsumedAACPackets.isEmpty()){

            waitForACCPacketConsumption();
        }

        if(!processConsumedPacket)
        {
            return;
        }

        final AACPacket aacPacket = listOfConsumedAACPackets.remove(0);
        final Timestamp timestamp = aacPacket.getTimestamp();
        final AccessUnit accessUnit = aacPacket.getAccessUnit();
        final byte[] accessUnitData = accessUnit.getData();
        inputBuffer.put(accessUnitData, 0, accessUnitData.length);
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, accessUnitData.length, timestamp.getTimestampInMillis()*1000, 0);//TODO WTF MEANS '*1000'
    }

    private void waitForACCPacketConsumption(){

        packetConsumedEvent.reset();
        try {
            packetConsumedEvent.waitOne();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOutputBufferAvailable(int outputBufferIndex, ByteBuffer outputBuffer) {

        outputBuffer.position(bufferInfo.offset);
        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);

        byte [] data = new byte[bufferInfo.size];
        outputBuffer.get(data, bufferInfo.offset, data.length);

        audioTrack.write(data, bufferInfo.offset, bufferInfo.offset + bufferInfo.size); // AudioTrack write data
        mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
    }
}
