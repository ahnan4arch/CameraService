package com.example.zebul.cameraservice.communication.tcp;

import com.example.zebul.cameraservice.communication.RTPServerSessionController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by zebul on 10/22/16.
 */
public class RTSPServer implements Runnable{

    private int serverPort = 9999;
    private Thread thread;
    private SocketProxy socketProxy = new SocketProxy();

    private List<RTSPSessionEventListener> rtspSessionLifecycleListeners =
            new LinkedList<RTSPSessionEventListener>();

    public RTSPServer(int serverPort){

        this.serverPort = serverPort;
    }

    public void start(){

        doStop();
        doStart();
    }

    public void stop(){

        doStop();
    }

    private void doStart(){

        thread = new Thread(this);
        thread.start();
    }

    private void doStop(){

        if(thread == null){
            return;
        }

        if(thread.isAlive()){
            socketProxy.closeServerSocket();
            thread.interrupt();
        }
        thread = null;
    }

    public void attachRTSPSessionLifecycleListener(
            RTSPSessionEventListener rtspSessionLifecycleListener) {

        rtspSessionLifecycleListeners.add(rtspSessionLifecycleListener);
    }

    public void detachRTSPSessionLifecycleListener(
            RTSPSessionEventListener rtspSessionLifecycleListener) {

        rtspSessionLifecycleListeners.remove(rtspSessionLifecycleListener);
    }

    class SocketProxy{

        private ServerSocket serverSocket;

        synchronized void setServerSocket(ServerSocket serverSocket){
            this.serverSocket = serverSocket;
        }

        synchronized void closeServerSocket(){
            if(serverSocket == null){
                return;
            }
            try {
                serverSocket.close();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }

    @Override
    public void run() {


        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            socketProxy.setServerSocket(serverSocket);
            while(!Thread.interrupted()){

                Socket clientSocket = serverSocket.accept();
                final RTPServerSessionController rtpSessionController = new RTPServerSessionController(clientSocket);

                RTSPSessionEventListener rtspSessionEventListener = new RTSPSessionEventListener(){

                    @Override
                    public void onRTSPSessionCreatedEvent(
                            RTSPSessionCreatedEvent rtspSessionCreatedEvent) {

                        for(RTSPSessionEventListener rtspSessionEventListener: rtspSessionLifecycleListeners){

                            rtspSessionEventListener.onRTSPSessionCreatedEvent(rtspSessionCreatedEvent);
                        }
                    }

                    @Override
                    public void onRTSPSessionDestroyedEvent(
                            RTSPSessionDestroyedEvent rtspSessionDestroyedEvent) {

                        rtpSessionController.stop();
                        for(RTSPSessionEventListener rtspSessionEventListener: rtspSessionLifecycleListeners){

                            rtspSessionEventListener.onRTSPSessionDestroyedEvent(rtspSessionDestroyedEvent);
                        }
                    }
                };
                RTSPSession c = new RTSPSession(clientSocket, rtspSessionEventListener, rtpSessionController);
                c.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketProxy.setServerSocket(null);
    }
}
