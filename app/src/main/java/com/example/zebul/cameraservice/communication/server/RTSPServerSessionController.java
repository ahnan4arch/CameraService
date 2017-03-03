package com.example.zebul.cameraservice.communication.server;

import com.example.zebul.cameraservice.av_protocols.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_processing.audio.AudioSettings;
import com.example.zebul.cameraservice.av_protocols.rtsp.error.RTSP4xxClientRequestError;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_protocols.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_protocols.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_protocols.sdp.Attribute;
import com.example.zebul.cameraservice.av_protocols.sdp.MediaDescription;
import com.example.zebul.cameraservice.av_protocols.sdp.SessionDescription;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.Transport;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.TransportDecoder;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.TransportEncoder;
import com.example.zebul.cameraservice.av_processing.video.camera.Resolution;
import com.example.zebul.cameraservice.av_processing.video.camera.VideoSettings;
import com.example.zebul.cameraservice.av_processing.audio.MicrophoneSettings;
import com.example.zebul.cameraservice.av_processing.video.camera.CameraSettings;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;



/**
 * Created by zebul on 1/1/17.
 */

public class RTSPServerSessionController implements RTSPRequestListener {

    private Transport videoTransport;
    private Transport audioTransport;

    private Socket clientSocket;
    private RTPServerSession rtpServerSession;

    private VideoSettings videoSettings = new VideoSettings(
            Resolution._640x480, VideoSettings.DEFAULT_BIT_RATE, VideoSettings.DEFAULT_FRAME_RATE);

    private AudioSettings audioSettings = AudioSettings.DEFAULT;

    private int port = 5001;
    private SessionDescription sessionDescription = new SessionDescription(port);

    public RTSPServerSessionController(Socket clientSocket){

        this.clientSocket = clientSocket;

        MediaDescription videoMediaDescription = new MediaDescription(MediaDescription.MediaType.Video, 0, "RTP/AVP", "96");
        videoMediaDescription.addAttribute(new Attribute("rtpmap","96 H264/90000"));
        videoMediaDescription.addAttribute(new Attribute("fmtp","96 packetization-mode=1;profile-level-id=42e00d;"));

        String videoControlUrl = "rtsp://" + clientSocket.getLocalAddress().getHostAddress() + ":" + clientSocket.getLocalPort() + "/trackID=" + 1;
        videoMediaDescription.addAttribute(new Attribute("control",videoControlUrl));
        sessionDescription.addMediaDescription(videoMediaDescription);

        MediaDescription audioMediaDescription = new MediaDescription(MediaDescription.MediaType.Audio, 0, "RTP/AVP", "96");
        audioMediaDescription.addAttribute(new Attribute("rtpmap","96 mpeg4-generic/8000"));
        audioMediaDescription.addAttribute(new Attribute("fmtp","96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;"));
        String audioControlUrl = "rtsp://" + clientSocket.getLocalAddress().getHostAddress() + ":" + clientSocket.getLocalPort() + "/trackID=" + 2;
        audioMediaDescription.addAttribute(new Attribute("control",audioControlUrl));
        sessionDescription.addMediaDescription(audioMediaDescription);
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
    public RTSPResponse onDescribe(RTSPRequest request) throws RTSP4xxClientRequestError {

        int CSeq = request.getHeader().getCSeq();
        HeaderFields headerFields = new HeaderFields();
        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq+""));
        Header header = new Header(headerFields);

        Body body = new Body(sessionDescription);
        return new RTSPResponse(StatusCode.OK, request.getVersion(), header, body);
    }

    @Override
    public RTSPResponse onSetup(RTSPRequest request) throws RTSP4xxClientRequestError {

        HeaderField transportHeaderField = request.findHeaderField(HeaderField.KnownName.Transport);
        Transport transport = null;
        if(transportHeaderField != null){
            transport = TransportDecoder.decode(transportHeaderField.getValue());
        }
        else{
            transport = new Transport();
        }
        String controlTrack = request.getRequestUri().toString();
        if(transportHeaderField != null) {

            if(sessionDescription.videoMediaHasValueOfAttribute("control", controlTrack)){

                transport.setSsrc(0x11221A87);
                videoTransport = transport;
            }
            else if(sessionDescription.audioMediaHasValueOfAttribute("control", controlTrack)){

                transport.setSsrc(0x9E7D1A87);
                audioTransport = transport;
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
        headerFields.add(new HeaderField(HeaderField.KnownName.Session, sessionDescription.getIdentifier()));
        headerFields.add(new HeaderField(HeaderField.KnownName.Cache_Control, "no-cache"));
        Header header = new Header(headerFields);

        return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
    }

    @Override
    public RTSPResponse onPlay(RTSPRequest request) throws RTSP4xxClientRequestError {

        HeaderFields headerFields = new HeaderFields();

        String rtpInfo = "url=rtsp://" + clientSocket.getLocalAddress().getHostAddress() + ":" + clientSocket.getLocalPort() + "/trackID=" + 0 + ";seq=0,";
        int CSeq = request.getHeader().getCSeq();
        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, CSeq+""));
        headerFields.add(new HeaderField(HeaderField.KnownName.RTP_Info, rtpInfo));
        headerFields.add(new HeaderField(HeaderField.KnownName.Session, sessionDescription.getIdentifier()));
        Header header = new Header(headerFields);
        SocketAddress socketAddress = clientSocket.getRemoteSocketAddress();
        String clientIp = "127.0.0.1";
        String [] addressElems = socketAddress.toString().split(":");
        if(1<addressElems.length){
            clientIp = addressElems[0].replaceAll("/", "");
        }

        InetSocketAddress videoSocketAddress = null;
        if(videoTransport != null){
            videoSocketAddress = new InetSocketAddress(
                    clientIp, videoTransport.getMinClientPort());
        }

        InetSocketAddress audioSocketAddress = null;
        if(audioTransport != null) {
            audioSocketAddress = new InetSocketAddress(
                    clientIp, audioTransport.getMinClientPort());
        }

        CameraSettings cameraSettings = new CameraSettings(videoSettings);
        MicrophoneSettings microphoneSettings =  new MicrophoneSettings(AudioSettings.DEFAULT);
        rtpServerSession = new RTPServerSession(videoSocketAddress, audioSocketAddress,
                cameraSettings, microphoneSettings);
        rtpServerSession.start();
        return new RTSPResponse(StatusCode.OK, request.getVersion(), header);
    }

    @Override
    public RTSPResponse onPause(RTSPRequest request) throws RTSP4xxClientRequestError {
        throw new RTSP4xxClientRequestError(StatusCode.NOT_IMPLEMENTED, "Not impelmented");
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

        if(rtpServerSession != null){

            rtpServerSession.stop();
            rtpServerSession = null;
        }
    }
}
