package com.example.zebul.cameraservice.communication.tcp;

import com.example.zebul.cameraservice.communication.RTPSessionController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by zebul on 10/22/16.
 */
public class RTSPServer implements Runnable{

    private int serverPort = 9999;
    private Thread thread;
    private SocketProxy socketProxy = new SocketProxy();

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
                RTPSessionController rtpSessionController = new RTPSessionController(clientSocket);
                RTSPSession c = new RTSPSession(clientSocket, rtpSessionController, rtpSessionController);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketProxy.setServerSocket(null);
    }
}
