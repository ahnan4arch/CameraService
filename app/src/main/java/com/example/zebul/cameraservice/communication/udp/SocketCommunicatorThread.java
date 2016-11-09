package com.example.zebul.cameraservice.communication.udp;

import java.io.IOException;

/**
 * Created by zebul on 9/19/16.
 */
public class SocketCommunicatorThread implements Runnable{

    private CommunicationSupervisor communicationSupervisor;
    private Thread thread;
    private SocketCommunicator socketCommunicator;

    protected SocketCommunicatorThread(
            CommunicationSupervisor workSupervisor_,
            SocketCommunicator socketCommunicator_){

        communicationSupervisor = workSupervisor_;
        socketCommunicator = socketCommunicator_;
    }

    void start() {

        thread = new Thread(this);
        thread.start();
    }

    void stop() {

        socketCommunicator.shutdown();
        if(thread == null){
            return;
        }
        try {
            thread.join();
        } catch (InterruptedException exc_) {
            exc_.printStackTrace();
        }
    }

    @Override
    public void run() {

        try{
            while(communicationSupervisor.keepCommunication()){

                socketCommunicator.communicate();
            }
        }
        catch (IOException exc_){

            communicationSupervisor.onCommunicatorFailure();
        }
    }
}

