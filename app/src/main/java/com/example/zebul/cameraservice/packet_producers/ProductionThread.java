package com.example.zebul.cameraservice.packet_producers;

import com.example.zebul.cameraservice.ManualResetEvent;

/**
 * Created by zebul on 12/23/16.
 */

public class ProductionThread {

    private Thread thread;
    private Object threadLock = new Object();

    private ManualResetEvent stopEvent = new ManualResetEvent(false);
    private ManualResetEvent startEvent = new ManualResetEvent(false);

    public void start(Runnable runnable){

        resetStarted();
        doStop();
        doStart(runnable);
        waitUntilStarted();
    }

    public void stop(){

        resetStopped();
        doStop();
        waitUntilStopped();
    }

    private void resetStarted() {

        startEvent.reset();
    }

    private void resetStopped() {

        stopEvent.reset();
    }

    private void waitUntilStarted() {

        try {
            startEvent.waitOne();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void notifyStarted() {

        startEvent.set();
    }

    private void waitUntilStopped() {

        try {
            stopEvent.waitOne();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void notifyStopped() {

        stopEvent.set();
    }

    private class Runner implements Runnable{

        private Runnable runnable;
        private Runner(Runnable runnable){

            this.runnable = runnable;
        }

        @Override
        public void run() {

            notifyStarted();
            runnable.run();
            notifyStopped();
        }
    }

    private void doStart(final Runnable runnable){

        synchronized (threadLock){

            thread = new Thread(new Runnable(){

                @Override
                public void run() {

                    notifyStarted();
                    runnable.run();
                    notifyStopped();
                }
            });
            thread.start();
        }
    }

    private void doStop(){

        synchronized (threadLock){

            if(thread != null){

                thread.interrupt();
            }
            thread = null;
        }
    }

}
