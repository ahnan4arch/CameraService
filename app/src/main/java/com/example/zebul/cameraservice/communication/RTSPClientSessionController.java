package com.example.zebul.cameraservice.communication;

import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.URI;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Transport;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.TransportDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.TransportEncoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.av_streaming.sdp.MediaDescription;
import com.example.zebul.cameraservice.av_streaming.sdp.SessionDescription;

import java.util.List;
import java.util.Random;

/**
 * Created by zebul on 1/28/17.
 */

public class RTSPClientSessionController
        implements RTSPResponseConsumer, RTSPRequestProducer {

    private String userAgent;
    private URI requestUri;
    private Version version = new Version(1, 0);

    private int CSeq;
    private SessionStage currentSessionStage = new IdleStage();

    public RTSPClientSessionController(String userAgent, URI requestUri){

        this.userAgent = userAgent;
        this.requestUri = requestUri;
        transitionToIdle();
    }

    public void setUp(){

        transitionTo(new OptionsStage());
    }

    public void tearDown() {

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
        header.addHeaderField(new HeaderField(HeaderField.KnownName.User_Agent, userAgent));
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
            return new RTSPRequest(requestUri, version, header, Method.OPTIONS);
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
            return new RTSPRequest(requestUri, version, header, Method.DESCRIBE);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            final Body body = response.getBody();
            /*
            final SessionDescription sessionDescription = body.getSessionDescription();
            for(MediaDescription mediaDescriptions: sessionDescription.getMediaDescriptions()){

                int foo = 1;
                int bar = foo;
            }*/
            transitionTo(new SetUpStage());
        }
    }

    private class SetUpStage implements SessionStage {

        @Override
        public RTSPRequest produceRequest() {

            Header header = createHeader();
            Transport clientTransport = new Transport();
            clientTransport.setTransportProtocol(Transport.TransportProtocol.RTP);
            clientTransport.setProfile(Transport.Profile.AVP);
            clientTransport.setTransmissionType(Transport.TransmissionType.unicast);
            clientTransport.setClientPortRange(3456, 3457);
            final String transportAsText = TransportEncoder.encode(clientTransport);
            header.addHeaderField(new HeaderField(HeaderField.KnownName.Transport, transportAsText));
            return new RTSPRequest(requestUri, version, header, Method.SETUP);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            final HeaderField transportHeaderField = response.findHeaderField(HeaderField.KnownName.Transport);
            final String transportAsText = transportHeaderField.getValue();
            Transport serverTransport = TransportDecoder.decode(transportAsText);
            transitionTo(new PlayStage());
        }
    }

    private class PlayStage implements SessionStage {

        @Override
        public RTSPRequest produceRequest() {

            Header header = createHeader();
            header.addHeaderField(new HeaderField(HeaderField.KnownName.Range, "npt=0.000-"));
            return new RTSPRequest(requestUri, version, header, Method.PLAY);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            transitionTo(new PlayingStage());
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
            return new RTSPRequest(requestUri, version, header, Method.TEARDOWN);
        }

        @Override
        public void consumeResponse(RTSPResponse response) {

            transitionToIdle();
        }
    }
}
