package com.example.udp;

import com.example.message.Message;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by zebul on 9/18/16.
 */
public class SocketEngine{

    private SocketMessageReceptionListener messageReceptionListener;
    private int port;

    private volatile boolean keepWorking = false;
    private CommunicationController communicationController = new CommunicationController();

    public SocketEngine(int port_, SocketMessageReceptionListener messageReceptionListener_){

        port = port_;
        messageReceptionListener = messageReceptionListener_;
    }

    public void start(){

        communicationController.start();
    }

    public void stop(){

        communicationController.stop();
    }

    public void post(Message message_) {

        communicationController.post(message_);
    }

    class CommunicationController implements Runnable, CommunicationSupervisor {

        private Sender sender;
        private Receiver receiver;
        private SocketCommunicatorThread senderThread;
        private SocketCommunicatorThread receiverThread;

        private boolean communicatorFailure = false;

        private Thread thread;
        void start(){

            doStop();
            doStart();
        }

        void stop(){

            doStop();
        }

        private void doStart(){

            keepWorking = true;
            thread = new Thread(this);
            thread.start();
        }

        private void doStop(){

            keepWorking = false;
            notifyClose();
            if(thread == null){
                return;
            }
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            while(keepWorking){

                try{
                    DatagramSocket datagramSocket = openSocket();
                    startCommunicators(datagramSocket);
                    waitForClose();
                    closeSocket(datagramSocket);
                }
                catch(SocketException exc_){
                    waitSome();
                }
            }
        }

        private synchronized void startCommunicators(DatagramSocket datagramSocket_){

            sender = new Sender(datagramSocket_);
            receiver = new Receiver(datagramSocket_, messageReceptionListener);
            senderThread = new SocketCommunicatorThread(this, sender);
            receiverThread = new SocketCommunicatorThread(this, receiver);
            senderThread.start();
            receiverThread.start();
        }

        private synchronized void stopCommunicators(){

            if(senderThread != null){

                senderThread.stop();
            }
            if(receiverThread != null){

                receiverThread.stop();
            }
        }

        public synchronized void post(Message message_) {

            if(sender != null){

                sender.post(message_);
            }
        }

        private DatagramSocket openSocket() throws SocketException {

            DatagramSocket datagramSocket = new DatagramSocket(port);
            return datagramSocket;
        }

        private void closeSocket(DatagramSocket datagramSocket_) {

            datagramSocket_.close();
        }

        private void waitForClose() {

            long infinite = 0;
            waitFor(infinite);
        }

        private void waitSome() {

            long someMilliseconds = 5000;
            waitFor(someMilliseconds);
        }

        private void waitFor(long milliseconds_){

            synchronized (this){

                try {
                    wait(milliseconds_);
                } catch (InterruptedException exc_) {
                    exc_.printStackTrace();
                }
            }
        }

        private void notifyClose() {

            synchronized (this){

                notify();
            }
        }

        @Override
        public boolean keepCommunication() {

            if(!keepWorking){
                return false;
            }

            if(communicatorFailure){
                return false;
            }
            return true;
        }

        @Override
        public void onCommunicatorFailure() {

            communicatorFailure = true;
        }
    }

}
