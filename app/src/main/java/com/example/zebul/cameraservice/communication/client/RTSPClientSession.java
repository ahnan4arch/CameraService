package com.example.zebul.cameraservice.communication.client;

import com.example.zebul.cameraservice.av_protocols.rtsp.URI;
import com.example.zebul.cameraservice.av_protocols.rtsp.error.RTSP5xxServerResponseError;
import com.example.zebul.cameraservice.av_protocols.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_protocols.rtsp.request.RTSPRequestEncoder;
import com.example.zebul.cameraservice.av_protocols.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_protocols.rtsp.response.RTSPResponseDecoder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by zebul on 2/3/17.
 */

public class RTSPClientSession implements Runnable{

    private URI rtspUri;
    private Thread thread;

    private RTSPRequestProducer rtspRequestProducer;
    private RTSPResponseConsumer rtspResponseConsumer;

    public RTSPClientSession(
            URI rtspUri,
            RTSPRequestProducer rtspRequestProducer,
            RTSPResponseConsumer rtspResponseConsumer){

        this.rtspUri = rtspUri;
        this.rtspRequestProducer = rtspRequestProducer;
        this.rtspResponseConsumer = rtspResponseConsumer;
    }

    public void start(){

        thread = new Thread(this);
        thread.start();
    }

    public void stop(){

        thread.interrupt();
    }

    @Override
    public void run() {

        try {
            Socket clientSocket = createClientSocket();
            keepSession(clientSocket);
            destroyClientSocket(clientSocket);
        }
        catch(IOException exc_){

            int foo = 1;
            int bar = foo;
        }
    }

    private Socket createClientSocket() throws IOException {

        Socket clientSocket = new Socket(rtspUri.getHost(), rtspUri.getPort());
        clientSocket.setSoTimeout(5000);
        return clientSocket;
    }

    private void keepSession(Socket clientSocket) throws IOException {

        DataOutputStream output = new DataOutputStream( clientSocket.getOutputStream());
        DataInputStream input = new DataInputStream( clientSocket.getInputStream());
        while (!Thread.interrupted()) {

            send(output);
            receive(input);
        }
    }

    private void send(DataOutputStream output) throws IOException {

        final RTSPRequest rtspRequest = rtspRequestProducer.produceRTSPRequest();
        if(rtspRequest != null){

            final String encodedRTSPRequest = RTSPRequestEncoder.encode(rtspRequest);
            output.write(encodedRTSPRequest.getBytes());
        }
    }

    private void receive(DataInputStream input) throws IOException {

        try {

            byte[] messageBytes = new byte[1024 * 4];
            StringBuilder serverResponseStringBuilder = new StringBuilder();
            boolean dataAvailable = true;
            while (dataAvailable) {
                int bytesRead = input.read(messageBytes);
                if (0 < bytesRead) {
                    serverResponseStringBuilder.append(new String(messageBytes, 0, bytesRead));
                    dataAvailable = (0 < input.available());
                } else {
                    return;
                }
            }

            try {

                String serverRtspResponseRepresentaionAsText = serverResponseStringBuilder.toString();
                System.out.println(serverRtspResponseRepresentaionAsText + "\n");
                final RTSPResponse serverRtspResponse = RTSPResponseDecoder.decode(serverRtspResponseRepresentaionAsText);
                rtspResponseConsumer.consumeRTSPResponse(serverRtspResponse);
            } catch (RTSP5xxServerResponseError error) {

                int foo = 1;
                int bar = foo;
            }
        }
        catch(SocketTimeoutException exc_){

            int foo = 1;
            int bar = foo;
        }
    }

    private void destroyClientSocket(Socket clientSocket) throws IOException {

        clientSocket.close();
    }
}
