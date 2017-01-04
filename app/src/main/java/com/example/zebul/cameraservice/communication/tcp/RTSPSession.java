package com.example.zebul.cameraservice.communication.tcp;

import com.example.zebul.cameraservice.communication.RTPSessionController;
import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequestDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponseEncoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by zebul on 10/22/16.
 */
public class RTSPSession extends Thread {

    private DataInputStream input;
    private DataOutputStream output;
    private Socket clientSocket;
    private RTPSessionController rtpSessionController;

    public RTSPSession(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            input = new DataInputStream( clientSocket.getInputStream());
            output =new DataOutputStream( clientSocket.getOutputStream());
            rtpSessionController = new RTPSessionController(clientSocket);
            this.start();
        }
        catch(IOException e) {
            System.out.println("Connection:"+e.getMessage());
        }
    }

    public void run() {
        try {

            while(true){

                byte[] messageBytes = new byte[1000];
                StringBuilder sb = new StringBuilder();
                boolean dataAvailable = true;
                while(dataAvailable)
                {
                    int bytesRead = input.read(messageBytes);
                    if(0<bytesRead){
                        sb.append(new String(messageBytes, 0, bytesRead));
                        dataAvailable = (0<input.available());
                    }
                    else{
                        return;
                    }

                }

                RTSPResponse response = null;
                try{

                    String requestRepresentaionAsText = sb.toString();
                    System.out.println(requestRepresentaionAsText+"\n");
                    RTSPRequest request = RTSPRequestDecoder.decode(requestRepresentaionAsText);
                    response = processRequest(request);
                }
                catch(RTSP4xxClientRequestError error){

                    Version version = new Version(1,0);
                    HeaderFields headerFields = new HeaderFields();
                    Header header = new Header(1, headerFields);
                    response = new RTSPResponse(error.getStatusCode(), version, header);
                }
                String responseRepresentaionAsText = RTSPResponseEncoder.encode(response);
                output.write(responseRepresentaionAsText.getBytes());
                System.out.println(responseRepresentaionAsText+"\n");
            }
        }
        catch(EOFException e) {
            System.out.println("EOF:"+e.getMessage()); }
        catch(IOException e) {
            System.out.println("IO:"+e.getMessage());}
        finally {
            try {
                clientSocket.close();
            }
            catch (IOException e){/*close failed*/}
        }
    }

    private RTSPResponse processRequest(RTSPRequest request)
            throws RTSP4xxClientRequestError {

        if(request.getMethod().equals(Method.OPTIONS)){

            return rtpSessionController.onOptions(request);
        }
        else if(request.getMethod().equals(Method.DESCRIBE)){

            return rtpSessionController.onDescribe(request);
        }
        else if(request.getMethod().equals(Method.SETUP)){

            return rtpSessionController.onSetup(request);
        }
        else if(request.getMethod().equals(Method.PLAY)){

            return rtpSessionController.onPlay(request);
        }
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

}
