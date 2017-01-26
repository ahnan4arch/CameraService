package com.example.zebul.cameraservice.communication;

import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.audio.AudioSettings;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_streaming.rtsp.session.Session;
import com.example.zebul.cameraservice.av_streaming.rtsp.session.SessionAudioInfo;
import com.example.zebul.cameraservice.av_streaming.rtsp.session.SessionVideoInfo;
import com.example.zebul.cameraservice.av_streaming.rtsp.transport.Transport;
import com.example.zebul.cameraservice.av_streaming.rtsp.transport.TransportDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.transport.TransportEncoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.video.Resolution;
import com.example.zebul.cameraservice.av_streaming.rtsp.video.VideoSettings;
import com.example.zebul.cameraservice.communication.udp.RTPSession;
import com.example.zebul.cameraservice.packet_producers.audio.MicrophoneSettings;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraSettings;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;



/**
 * Created by zebul on 1/1/17.
 */

public class RTPSessionController implements RTSPRequestListener {

    private Socket clientSocket;
    private RTPSession rtpSession;

    private VideoSettings videoSettings =
            new VideoSettings(Resolution._640x480, 1000000/*6000000*/, VideoSettings.DEFAULT_FRAME_RATE);

    private AudioSettings audioSettings = AudioSettings.DEFAULT;

    private int port = 5001;
    private Session session = new Session(port);

    public RTPSessionController(Socket clientSocket){

        this.clientSocket = clientSocket;
    }

    @Override
    public RTSPResponse onDescribe(RTSPRequest request) throws RTSP4xxClientRequestError {

        int CSeq = request.getHeader().getCSeq();
        HeaderFields headerFields = new HeaderFields();
        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq+""));
        Header header = new Header(headerFields);

        SessionVideoInfo sessionVideoInfo = new SessionVideoInfo(1, videoSettings);
        session.setSessionVideoInfo(sessionVideoInfo);
        SessionAudioInfo sessionAudioInfo = new SessionAudioInfo(2, audioSettings);
        session.setSessionAudioInfo(sessionAudioInfo);
        Body body = new Body(session.getDescription());
        return new RTSPResponse(StatusCode.OK, request.getVersion(), header, body);
    }

    @Override
    public RTSPResponse onAnnounce(RTSPRequest request) throws RTSP4xxClientRequestError {
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

    @Override
    public RTSPResponse onGetParameter(RTSPRequest request) throws RTSP4xxClientRequestError {
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

    @Override
    public RTSPResponse onOptions(RTSPRequest request) throws RTSP4xxClientRequestError {

        int CSeq = request.getHeader().getCSeq();
        HeaderFields headerFields = new HeaderFields();
        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq));
        headerFields.add(new HeaderField(HeaderField.KnownName.Public, "DESCRIBE, SETUP, TEARDOWN, PLAY, PAUSE"));
        Header header = new Header(headerFields);
        return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
    }

    @Override
    public RTSPResponse onPause(RTSPRequest request) throws RTSP4xxClientRequestError {
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

    @Override
    public RTSPResponse onPlay(RTSPRequest request) throws RTSP4xxClientRequestError {

        HeaderFields headerFields = new HeaderFields();

        String rtpInfo = "url=rtsp://" + clientSocket.getLocalAddress().getHostAddress() + ":" + clientSocket.getLocalPort() + "/trackID=" + 0 + ";seq=0,";
        int CSeq = request.getHeader().getCSeq();
        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq+""));
        headerFields.add(new HeaderField(HeaderField.KnownName.RTP_Info, rtpInfo));
        headerFields.add(new HeaderField(HeaderField.KnownName.Session, session.getIdentifier()));
        Header header = new Header(headerFields);
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

        CameraSettings cameraSettings = new CameraSettings(videoSettings);
        MicrophoneSettings microphoneSettings =  new MicrophoneSettings(AudioSettings.DEFAULT);
        rtpSession = new RTPSession(videoSocketAddress, audioSocketAddress,
                cameraSettings, microphoneSettings);
        rtpSession.start();
        return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
    }

    @Override
    public RTSPResponse onRecord(RTSPRequest request) throws RTSP4xxClientRequestError {
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

    @Override
    public RTSPResponse onRedirect(RTSPRequest request) throws RTSP4xxClientRequestError {
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

    @Override
    public RTSPResponse onSetup(RTSPRequest request) throws RTSP4xxClientRequestError {

        HeaderField transportHeaderField = request.findHeaderField(HeaderField.KnownName.Transport);
        Transport transport = null;
        if(transportHeaderField == null){
            transport = TransportDecoder.decode(transportHeaderField.getValue());
        }
        else{
            transport = new Transport();
        }
        int trackId = request.getRequestUri().getTrackId();


        if(transportHeaderField != null) {

            if(session.isVideoTrackId(trackId)){

                transport.setSsrc("11221A87");
                session.setVideoTransport(transport);
            }
            else if(session.isAudioTrackId(trackId)){

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

        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq));
        headerFields.add(new HeaderField(HeaderField.KnownName.Transport, TransportEncoder.encode(transport)));
        headerFields.add(new HeaderField(HeaderField.KnownName.Session, session.getIdentifier()));
        headerFields.add(new HeaderField(HeaderField.KnownName.Cache_Control, "no-cache"));
        Header header = new Header(headerFields);

        return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
    }

    @Override
    public RTSPResponse onSetParameter(RTSPRequest request) throws RTSP4xxClientRequestError {
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
    }

    @Override
    public RTSPResponse onTeardown(RTSPRequest request) throws RTSP4xxClientRequestError {

        stop();
        int CSeq = request.getHeader().getCSeq();
        HeaderFields headerFields = new HeaderFields();
        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq));
        Header header = new Header(headerFields);
        return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
    }

    public void stop() {

        if(rtpSession != null){

            rtpSession.stop();
            rtpSession = null;
        }
    }
}
