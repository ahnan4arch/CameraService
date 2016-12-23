package com.example.zebul.cameraservice.packet_producers;

/**
 * Created by zebul on 12/23/16.
 */

public class ProductionEngine {

    private Thread thread;
    private Object lock = new Object();

    public void start(Runnable runnable){

        doStop();
        doStart(runnable);
    }

    public void stop(){

        doStop();
    }

    private void doStart(Runnable runnable){

        synchronized (lock){

            thread = new Thread(runnable);
            thread.start();
        }
    }

    private void doStop(){

        synchronized (lock){

            if(thread != null){

                thread.interrupt();
            }
            thread = null;
        }
    }

}
