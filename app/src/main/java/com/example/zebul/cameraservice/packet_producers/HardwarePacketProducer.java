package com.example.zebul.cameraservice.packet_producers;

/**
 * Created by zebul on 1/4/17.
 */

public abstract class HardwarePacketProducer implements Runnable{

    protected ProductionThread thread = new ProductionThread();
    protected PacketProductionExceptionListener packetProductionExceptionListener;

    protected HardwarePacketProducer(
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
    protected abstract void close()throws PacketProductionException;

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
}
