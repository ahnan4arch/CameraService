package com.example.udp;

/**
 * Created by zebul on 9/19/16.
 */
public class Event {

    private Object monitor = new Object();

    public void signal() {

        synchronized (monitor) {

            monitor.notify();
        }
    }

    public void waitForSignalFor(int milliseconds_){

        synchronized (monitor){

            try {
                monitor.wait(milliseconds_);
            } catch (InterruptedException exc_) {
                exc_.printStackTrace();
            }
        }
    }

    public void waitForSignalInfinite(){

        waitForSignalFor(0);
    }
}
