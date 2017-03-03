package com.example.zebul.cameraservice.av_processing;

import android.media.MediaCodec;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by zebul on 1/4/17.
 */

public abstract class MediaCodecPacketProcessor implements Runnable{

    protected static final long WAIT_INDEFINITELY = -1;
    protected long inputBufferTimeoutInUs = WAIT_INDEFINITELY;
    protected long outputBufferTimeoutInUs = WAIT_INDEFINITELY;

    protected ProductionThread thread = new ProductionThread();
    protected MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    protected PacketProcessingExceptionListener packetProcessingExceptionListener;
    protected MediaCodec mediaCodec;

    protected MediaCodecPacketProcessor(
            PacketProcessingExceptionListener packetProcessingExceptionListener){

        this.packetProcessingExceptionListener = packetProcessingExceptionListener;
    }

    protected boolean start(){

        try {
            thread.start(this);
            return true;
        } catch (IllegalProductionStateException exc) {
            packetProcessingExceptionListener.onPacketProductionException(exc);
        }
        return false;
    }

    public void stop(){

        try {
            thread.stop();
        } catch (IllegalProductionStateException exc) {
            packetProcessingExceptionListener.onPacketProductionException(exc);
        }
    }

    @Override
    public void run() {

        try {
            open();
            keepProcessing();
        } catch (PacketProcessingException exc) {
            packetProcessingExceptionListener.onPacketProductionException(exc);
        }
        finally {
            tryClose();
        }
    }

    private void keepProcessing() throws PacketProcessingException {

        try {

            while (!Thread.interrupted()) {

                process();
            }
        }
        catch(InterruptedException exc){
            exc.printStackTrace();
        }
    }

    protected abstract void open()throws PacketProcessingException;
    protected void process()throws PacketProcessingException, InterruptedException{

        processMediaCodecInput();
        processMediaCodecOutput();
    }

    protected void processMediaCodecInput(){

        int inputBufferIndex = mediaCodec.dequeueInputBuffer(inputBufferTimeoutInUs);
        if ( inputBufferIndex>=0 ) {

            onInputBufferAvailable(inputBufferIndex);
        }
        else{

            onInputBufferUnavailable(inputBufferIndex);
        }
    }

    protected void processMediaCodecOutput(){

        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, outputBufferTimeoutInUs);
        if ( outputBufferIndex>=0 ){

            onOutputBufferAvailable(outputBufferIndex);
        }
        else{

            onOutputBufferUnavailable(outputBufferIndex);
        }
    }

    protected void onInputBufferAvailable(int inputBufferIndex) {

        final ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
        onInputBufferAvailable(inputBufferIndex, inputBuffer);
    }

    protected void onInputBufferAvailable(
            int inputBufferIndex,
            ByteBuffer inputBuffer){

    }

    protected void onInputBufferUnavailable(int statusCode) {

        Log.d("MediaCodecProcessor", "onInputBufferUnavailable status code"+statusCode);
    }

    protected void onOutputBufferAvailable(int outputBufferIndex) {

        final ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        final ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
        onOutputBufferAvailable(outputBufferIndex, outputBuffer);
    }

    protected void onOutputBufferAvailable(
            int outputBufferIndex,
            ByteBuffer outputBuffer){

    }

    protected void onOutputBufferUnavailable(int statusCode) {

        Log.d("MediaCodecProcessor", "onOutputBufferUnavailable status code"+statusCode);
        switch(statusCode){
            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                break;
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                break;
            case MediaCodec.INFO_TRY_AGAIN_LATER:
                break;
        }
    }

    protected void close()throws PacketProcessingException {

        if(mediaCodec != null){

            mediaCodec.release();
            mediaCodec = null;
        }
    }

    private void tryClose() {

        try {
            close();
        } catch (PacketProcessingException exc) {
            packetProcessingExceptionListener.onPacketProductionException(exc);
        }
    }

    public boolean isWorking() {
        return thread.isWorking();
    }
}
