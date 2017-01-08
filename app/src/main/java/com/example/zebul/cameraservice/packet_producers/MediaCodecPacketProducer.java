package com.example.zebul.cameraservice.packet_producers;

import android.media.MediaCodec;

/**
 * Created by zebul on 1/4/17.
 */

public abstract class MediaCodecPacketProducer implements Runnable{

    protected ProductionThread thread = new ProductionThread();
    protected PacketProductionExceptionListener packetProductionExceptionListener;

    protected MediaCodec mediaCodec;
    protected MediaCodec.BufferInfo bufferInfo;

    protected MediaCodecPacketProducer(
            PacketProductionExceptionListener packetProductionExceptionListener){

        this.packetProductionExceptionListener = packetProductionExceptionListener;
    }

    protected boolean start(){

        try {
            thread.start(this);
            return true;
        } catch (IllegalProductionStateException exc) {
            packetProductionExceptionListener.onPacketProductionException(exc);
        }
        return false;
    }

    public void stop(){

        try {
            thread.stop();
        } catch (IllegalProductionStateException exc) {
            packetProductionExceptionListener.onPacketProductionException(exc);
        }
    }

    @Override
    public void run() {

        try {
            open();
            keepProduction();
        } catch (PacketProductionException exc) {
            packetProductionExceptionListener.onPacketProductionException(exc);
        }
        finally {
            tryClose();
        }
    }

    private void keepProduction() throws PacketProductionException {

        try {

            while (!Thread.interrupted()) {

                produce();
            }
        }
        catch(InterruptedException exc){
            exc.printStackTrace();
        }
    }

    protected abstract void open()throws PacketProductionException;
    protected abstract void produce()throws PacketProductionException, InterruptedException;
    protected void close()throws PacketProductionException{

        if(mediaCodec != null){

            mediaCodec.release();
            mediaCodec = null;
        }
    }

    private void tryClose() {

        try {
            close();
        } catch (PacketProductionException exc) {
            packetProductionExceptionListener.onPacketProductionException(exc);
        }
    }

    public boolean isWorking() {
        return thread.isWorking();
    }

    protected void flushMediaCodecOutput(){

        final long OUTPUT_BUFFER_TIMEOUT_US = 500000;
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, OUTPUT_BUFFER_TIMEOUT_US);
        if ( outputBufferIndex>=0 ){

            onFlushMediaCodecOutputSuccess(outputBufferIndex);
        }
        else{

            onFlushMediaCodecOutputFailure(outputBufferIndex);
        }
    }

    protected void onFlushMediaCodecOutputSuccess(int infoIndex) {

    }

    protected void onFlushMediaCodecOutputFailure(int infoIndex) {

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
