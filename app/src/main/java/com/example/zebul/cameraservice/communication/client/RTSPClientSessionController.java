package com.example.zebul.cameraservice.communication.client;

import com.example.zebul.cameraservice.av_protocols.rtsp.Method;
import com.example.zebul.cameraservice.av_protocols.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_protocols.rtsp.URI;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.Transport;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.TransportDecoder;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.TransportEncoder;
import com.example.zebul.cameraservice.av_protocols.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_protocols.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_protocols.rtsp.version.Version;
import com.example.zebul.cameraservice.av_protocols.sdp.SessionDescription;

import java.net.MalformedURLException;
import java.util.Random;

/**
 * Created by zebul on 1/28/17.
 */

public class RTSPClientSessionController
        implements RTSPResponseConsumer, RTSPRequestProducer {

    private ClientSessionSettings settings;
    private RTPSessionLifecycleListener rtpSessionLifecycleListener;
    private Version version = new Version(1, 0);

    private int CSeq;
    private SessionStage currentSessionStage = new IdleStage();
    private SessionDescription sessionDescription;

    public RTSPClientSessionController(ClientSessionSettings settings,
                                       RTPSessionLifecycleListener rtpSessionLifecycleListener){

        this.settings = settings;
        this.rtpSessionLifecycleListener = rtpSessionLifecycleListener;
        transitionToIdle();
    }

    public void begin(){

        transitionTo(new OptionsStage());
    }

    public void end() {

        transitionTo(new TearDownStage());
    }

    @Override
    public void consumeRTSPResponse(RTSPResponse response){

        if(CSeq != response.getCSeq()){
            transitionToIdle();
        }
        else if(response.getStatusCode()!= StatusCode.OK){
            transitionToIdle();
        }
        else{
            currentSessionStage.consumeResponse(response);
        }
    }

    @Override
    public RTSPRequest produceRTSPRequest() {
        return currentSessionStage.produceRequest();
    }

    private void transitionTo(SessionStage newSessionStage){
        currentSessionStage = newSessionStage;
        CSeq++;
    }

    private void transitionToIdle(){
        currentSessionStage = new IdleStage();
        CSeq = new Random().nextInt();
    }

    private Header createHeader() {
        Header header = new Header(CSeq);
        header.addHeaderField(new HeaderField(HeaderField.KnownName.User_Agent, settings.UserAgent));
        return header;
    }

    private interface SessionStage{
        RTSPRequest produceRequest();
        void consumeResponse(RTSPResponse response);
    }

    private class IdleStage implements SessionStage{

        @Override
        public RTSPRequest produceRequest() {
            return null;
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

        }
    }

    private class OptionsStage implements SessionStage{

        @Override
        public RTSPRequest produceRequest() {

            Header header = createHeader();
            return new RTSPRequest(settings.RequestUri, version, header, Method.OPTIONS);
        }

        @Override
        public void consumeResponse(RTSPResponse response){

            transitionTo(new DescribeStage());
        }
    }

    private class DescribeStage implements SessionStage{

        @Override
        public RTSPRequest produceRequest() {

            Header header = createHeader();
            return new RTSPRequest(settings.RequestUri, version, header, Method.DESCRIBE);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            final Body body = response.getBody();
            sessionDescription = body.getSessionDescription();
            boolean setUpAudio = sessionDescription.hasInfoAboutAudioMedia();
            boolean setUpVideo = sessionDescription.hasInfoAboutVideoMedia();
            if(setUpVideo && setUpAudio){
                transitionTo(new SetUpAudioVideoStage());
            }
            else if(setUpVideo){
                transitionTo(new SetUpVideoStage());
            }
            else if(setUpAudio){
                transitionTo(new SetUpAudioStage());
            }
            else{
                transitionTo(new TearDownStage());
            }
        }
    }

    private class SetUpAudioVideoStage extends SetUpVideoStage {

        @Override
        public void consumeResponse(RTSPResponse response) {

            onResponse(response);
            transitionTo(new SetUpAudioStage());
        }
    }

    private class SetUpVideoStage implements SessionStage {

        @Override
        public RTSPRequest produceRequest() {

            Header header = createHeader();
            Transport clientTransport = new Transport();
            clientTransport.setTransportProtocol(Transport.TransportProtocol.RTP);
            clientTransport.setProfile(Transport.Profile.AVP);
            clientTransport.setTransmissionType(Transport.TransmissionType.unicast);
            clientTransport.setClientPortRange(settings.VideoMinPort, settings.VideoMaxPort);
            final String transportAsText = TransportEncoder.encode(clientTransport);
            header.addHeaderField(new HeaderField(HeaderField.KnownName.Transport, transportAsText));
            URI requestUri = settings.RequestUri;
            if(sessionDescription != null){
                final String valueOfAttribute = sessionDescription.findVideoMediaValueOfAttribute("control");
                try {
                    requestUri = URI.fromString(valueOfAttribute);
                } catch (MalformedURLException e) {
                }
            }
            return new RTSPRequest(requestUri, version, header, Method.SETUP);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            onResponse(response);
            transitionTo(new PlayStage());
        }

        protected void onResponse(RTSPResponse response){

            final HeaderField transportHeaderField = response.findHeaderField(HeaderField.KnownName.Transport);
            final String transportAsText = transportHeaderField.getValue();
            Transport serverTransport = TransportDecoder.decode(transportAsText);
            rtpSessionLifecycleListener.onRTPSetupVideoSession();
        }
    }

    private class SetUpAudioStage implements SessionStage {

        @Override
        public RTSPRequest produceRequest() {

            Header header = createHeader();
            Transport clientTransport = new Transport();
            clientTransport.setTransportProtocol(Transport.TransportProtocol.RTP);
            clientTransport.setProfile(Transport.Profile.AVP);
            clientTransport.setTransmissionType(Transport.TransmissionType.unicast);
            clientTransport.setClientPortRange(settings.AudioMinPort, settings.AudioMaxPort);
            final String transportAsText = TransportEncoder.encode(clientTransport);
            header.addHeaderField(new HeaderField(HeaderField.KnownName.Transport, transportAsText));
            URI requestUri = settings.RequestUri;
            if(sessionDescription != null){
                final String valueOfAttribute = sessionDescription.findAudioMediaValueOfAttribute("control");
                try {
                    requestUri = URI.fromString(valueOfAttribute);
                } catch (MalformedURLException e) {
                }
            }
            return new RTSPRequest(requestUri, version, header, Method.SETUP);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            final HeaderField transportHeaderField = response.findHeaderField(HeaderField.KnownName.Transport);
            final String transportAsText = transportHeaderField.getValue();
            Transport serverTransport = TransportDecoder.decode(transportAsText);
            transitionTo(new PlayStage());
            rtpSessionLifecycleListener.onRTPSetupVideoSession();
        }
    }

    private class PlayStage implements SessionStage {

        @Override
        public RTSPRequest produceRequest() {

            Header header = createHeader();
            header.addHeaderField(new HeaderField(HeaderField.KnownName.Range, "npt=0.000-"));
            return new RTSPRequest(settings.RequestUri, version, header, Method.PLAY);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            transitionTo(new PlayingStage());
            rtpSessionLifecycleListener.onRTPPlay();
        }
    }

    private class PlayingStage implements SessionStage {

        @Override
        public RTSPRequest produceRequest() {

            return null;
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

        }
    }

    private class TearDownStage implements SessionStage {
        @Override
        public RTSPRequest produceRequest() {
            Header header = createHeader();
            return new RTSPRequest(settings.RequestUri, version, header, Method.TEARDOWN);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            transitionToIdle();
            rtpSessionLifecycleListener.onRTPTearDownSession();
        }
    }
}
