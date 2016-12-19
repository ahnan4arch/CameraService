package com.example.zebul.cameraservice.communication.tcp;

import com.example.zebul.cameraservice.av_streaming.rtsp.transport.Transport;
import com.example.zebul.cameraservice.communication.udp.RTPSession;
import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequestDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponseEncoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.session.Session;
import com.example.zebul.cameraservice.av_streaming.rtsp.transport.TransportDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.transport.TransportEncoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;


/**
 * Created by zebul on 10/22/16.
 */
public class RTSPSession extends Thread {

    private DataInputStream input;
    private DataOutputStream output;
    private Socket clientSocket;
    private RTPSession rtpSession;

    private int port = 5001;
    private Session session = new Session(port);

    public RTSPSession(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            input = new DataInputStream( clientSocket.getInputStream());
            output =new DataOutputStream( clientSocket.getOutputStream());
            this.start();
        }
        catch(IOException e) {
            System.out.println("Connection:"+e.getMessage());
        }
    }

    public void run() {
        try {

            while(true){

                byte[] messageByte = new byte[1000];
                StringBuilder sb = new StringBuilder();
                boolean dataAvailable = true;
                while(dataAvailable)
                {
                    int bytesRead = input.read(messageByte);
                    if(0<bytesRead){
                        sb.append(new String(messageByte, 0, bytesRead));
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

            int CSeq = request.getHeader().getCSeq();
            HeaderFields headerFields = new HeaderFields();
            headerFields.add(new HeaderField("Public", "DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE"));
            Header header = new Header(CSeq, headerFields);
            return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
        }
        else if(request.getMethod().equals(Method.DESCRIBE)){

            int CSeq = request.getHeader().getCSeq();
            HeaderFields headerFields = new HeaderFields();
            Header header = new Header(CSeq, headerFields);
            session.setVideoTrackId(1);
            session.setAudioTrackId(2);
            Body body = new Body(session.getDescription());
            return new RTSPResponse(StatusCode.OK, request.getVersion(), header, body);
        }
        else if(request.getMethod().equals(Method.SETUP)){

            HeaderField transportHeaderField = request.getHeader().getHeaderFields().find("Transport");
            int trackId = request.getRequestUri().getTrackId();
            Transport transport = TransportDecoder.decode(transportHeaderField.getValue());
            if(!transportHeaderField.isEmpty()) {

                if(trackId == session.getVideoTrackId()){

                    transport.setSsrc("11221A87");
                    session.setVideoTransport(transport);
                }
                else if(trackId == session.getAudioTrackId()){

                    transport.setSsrc("9E7D1A87");
                    session.setAudioTransport(transport);
                }
            }

            int CSeq = request.getHeader().getCSeq();
            HeaderFields headerFields = new HeaderFields();
            try {
                transport.setDestination(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException exc_) {
                exc_.printStackTrace();
            }

            transport.setMode(Transport.Mode.PLAY);

            headerFields.add(new HeaderField("Transport", TransportEncoder.encode(transport)));
            headerFields.add(new HeaderField("Session", session.getIdentifier()));
            headerFields.add(new HeaderField("Cache-Control", "no-cache"));

            Header header = new Header(CSeq, headerFields);
            return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
        }
        else if(request.getMethod().equals(Method.PLAY)){

            HeaderFields headerFields = new HeaderFields();

            String rtpInfo = "url=rtsp://" + clientSocket.getLocalAddress().getHostAddress() + ":" + clientSocket.getLocalPort() + "/trackID=" + 0 + ";seq=0,";
            headerFields.add(new HeaderField("RTP-Info", rtpInfo));
            headerFields.add(new HeaderField("Session", session.getIdentifier()));
            int CSeq = request.getHeader().getCSeq();
            Header header = new Header(CSeq, headerFields);
            SocketAddress socketAddress = clientSocket.getRemoteSocketAddress();
            String clientIp = "127.0.0.1";
            String [] addressElems = socketAddress.toString().split(":");
            if(1<addressElems.length){
                clientIp = addressElems[0].replaceAll("/", "");
            }
            Transport videoTransport = session.getVideoTransport();
            Transport audioTransport = session.getAudioTransport();
            InetSocketAddress videoSocketAddress = new InetSocketAddress(
                    clientIp, videoTransport.getMinClientPort());
            InetSocketAddress audioSocketAddress = new InetSocketAddress(
                    clientIp, audioTransport.getMinClientPort());

            /*

            String fileFullPath = "/home/zebul/Videos/output.mov";
            rtpEngine = new RTPEngine(address, fileFullPath);
            rtpEngine.start();
            */
            rtpSession = new RTPSession(videoSocketAddress, audioSocketAddress);
            rtpSession.start();
            return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
        }
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

}
