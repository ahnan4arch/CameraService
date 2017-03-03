package com.example.zebul.cameraservice.communication;

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
import com.example.zebul.cameraservice.av_protocols.sdp.SessionDescriptionProtocol;
import com.example.zebul.cameraservice.communication.client.RTPSessionLifecycleListener;
import com.example.zebul.cameraservice.communication.client.RTSPClientSessionController;
import com.example.zebul.cameraservice.communication.client.ClientSessionSettings;

import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by zebul on 1/28/17.
 */

public class RTSPClientSessionControllerTest {

    private RTSPClientSessionController controller;
    private final Version version = new Version(1, 0);

    @Before
    public void setUp() throws MalformedURLException {

        String userAgent = "Camera Service";
        URI requestUri = URI.fromString("rtsp://192.168.1.106:8554/s1");
        ClientSessionSettings sessionSettings = new ClientSessionSettings("CameraService", requestUri, 12331, 12332, 13221, 13222);
        controller = new RTSPClientSessionController(sessionSettings, new RTPSessionLifecycleListener(){

            @Override
            public void onRTPSetupAudioSession() {

            }

            @Override
            public void onRTPSetupVideoSession() {

            }

            @Override
            public void onRTPPlay() {

            }

            @Override
            public void onRTPTearDownSession() {

            }
        });
    }

    @Test
    public void test_controller_generates_expected_requests_for_audio_video(){

        controller.begin();

        //--- OPTION
        final RTSPRequest rtspRequest_option = controller.produceRTSPRequest();
        final Version version = rtspRequest_option.getVersion();
        assertEquals(Method.OPTIONS, rtspRequest_option.getMethod());
        int CSeq = rtspRequest_option.getCSeq();
        controller.consumeRTSPResponse(createRTSPResponseOption(CSeq));

        //--- DESCRIBE
        CSeq++;
        final RTSPRequest rtspRequest_describe = controller.produceRTSPRequest();
        assertEquals(Method.DESCRIBE, rtspRequest_describe.getMethod());
        assertEquals(CSeq, rtspRequest_describe.getCSeq());
        controller.consumeRTSPResponse(createRTSPResponseDescribe(CSeq, "trackID=video", "trackID=audio"));

        //--- SETUP VIDEO
        CSeq++;
        final RTSPRequest rtspRequest_setupVideo = controller.produceRTSPRequest();
        assertEquals(Method.SETUP, rtspRequest_setupVideo.getMethod());
        final URI videoRequestUri = rtspRequest_setupVideo.getRequestUri();
        final String videoControl = videoRequestUri.getFile();
        assertTrue(videoControl.contains("trackID=video"));
        assertEquals(CSeq, rtspRequest_setupVideo.getCSeq());
        final HeaderField headerFieldVideoTransport = rtspRequest_setupVideo.findHeaderField(HeaderField.KnownName.Transport);
        final String videoTransportAsText = headerFieldVideoTransport.getValue();
        Transport videoTransport = TransportDecoder.decode(videoTransportAsText);
        assertTrue(videoTransport.getMinClientPort()>0);
        assertTrue(videoTransport.getMaxClientPort()>0);
        controller.consumeRTSPResponse(createRTSPResponseSetUp(CSeq, videoTransport));


        //--- SETUP AUDIO
        CSeq++;
        final RTSPRequest rtspRequest_setupAudio = controller.produceRTSPRequest();
        assertEquals(Method.SETUP, rtspRequest_setupAudio.getMethod());
        final URI requestUri = rtspRequest_setupAudio.getRequestUri();
        final String audioControl = requestUri.getFile();
        assertTrue(audioControl.contains("trackID=audio"));
        assertEquals(CSeq, rtspRequest_setupAudio.getCSeq());
        final HeaderField headerFieldAudioTransport = rtspRequest_setupAudio.findHeaderField(HeaderField.KnownName.Transport);
        final String audioTransportAsText = headerFieldAudioTransport.getValue();
        Transport audioTransport = TransportDecoder.decode(audioTransportAsText);
        assertTrue(audioTransport.getMinClientPort()>0);
        assertTrue(audioTransport.getMaxClientPort()>0);
        controller.consumeRTSPResponse(createRTSPResponseSetUp(CSeq, audioTransport));

        //--- PLAY
        CSeq++;
        final RTSPRequest rtspRequest_play = controller.produceRTSPRequest();
        assertEquals(Method.PLAY, rtspRequest_play.getMethod());
        assertEquals(CSeq, rtspRequest_play.getCSeq());
        final HeaderField headerFieldRange = rtspRequest_play.findHeaderField(HeaderField.KnownName.Range);
        assertNotNull(headerFieldRange);


        controller.end();
    }

    @Test
    public void test_controller_generates_expected_requests_for_audio(){

        controller.begin();

        //--- OPTION
        final RTSPRequest rtspRequest_option = controller.produceRTSPRequest();
        final Version version = rtspRequest_option.getVersion();
        assertEquals(Method.OPTIONS, rtspRequest_option.getMethod());
        int CSeq = rtspRequest_option.getCSeq();
        controller.consumeRTSPResponse(createRTSPResponseOption(CSeq));

        //--- DESCRIBE
        CSeq++;
        final RTSPRequest rtspRequest_describe = controller.produceRTSPRequest();
        assertEquals(Method.DESCRIBE, rtspRequest_describe.getMethod());
        assertEquals(CSeq, rtspRequest_describe.getCSeq());
        controller.consumeRTSPResponse(createRTSPResponseDescribe(CSeq, "", "trackID=audio"));

        //--- SETUP AUDIO
        CSeq++;
        final RTSPRequest rtspRequest_setupAudio = controller.produceRTSPRequest();
        assertEquals(Method.SETUP, rtspRequest_setupAudio.getMethod());
        final URI requestUri = rtspRequest_setupAudio.getRequestUri();
        final String audioControl = requestUri.getFile();
        assertTrue(audioControl.contains("trackID=audio"));
        assertEquals(CSeq, rtspRequest_setupAudio.getCSeq());
        final HeaderField headerFieldAudioTransport = rtspRequest_setupAudio.findHeaderField(HeaderField.KnownName.Transport);
        final String audioTransportAsText = headerFieldAudioTransport.getValue();
        Transport audioTransport = TransportDecoder.decode(audioTransportAsText);
        assertTrue(audioTransport.getMinClientPort()>0);
        assertTrue(audioTransport.getMaxClientPort()>0);
        controller.consumeRTSPResponse(createRTSPResponseSetUp(CSeq, audioTransport));

        //--- PLAY
        CSeq++;
        final RTSPRequest rtspRequest_play = controller.produceRTSPRequest();
        assertEquals(Method.PLAY, rtspRequest_play.getMethod());
        assertEquals(CSeq, rtspRequest_play.getCSeq());
        final HeaderField headerFieldRange = rtspRequest_play.findHeaderField(HeaderField.KnownName.Range);
        assertNotNull(headerFieldRange);


        controller.end();
    }

    private RTSPResponse createRTSPResponseOption(int CSeq) {

        Header header = new Header(CSeq);
        final HeaderField headerField_optionPublic = new HeaderField(HeaderField.KnownName.Public, "DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE,GET_PARAMETER");
        header.addHeaderField(headerField_optionPublic);
        final HeaderField headerField_optionContentLenght = new HeaderField(HeaderField.KnownName.Content_Length, 0);
        header.addHeaderField(headerField_optionContentLenght);
        return new RTSPResponse(StatusCode.OK, version, header);
    }

    private RTSPResponse createRTSPResponseDescribe(int CSeq, String videoControl, String audioControl) {

        Header header = new Header(CSeq);
        final HeaderField headerFieldContentType = new HeaderField(HeaderField.KnownName.Content_Type, "application/sdp");
        String sessionDescriptionAsText =
                "v=0\r\n"+
                        "o=- 15867114619150279987 15867114619150279987 IN IP4 zebul-NV78\r\n"+
                        "s=Unnamed\r\n"+
                        "i=N/A\r\n"+
                        "c=IN IP4 0.0.0.0\r\n"+
                        "t=0 0\r\n"+
                        "a=tool:vlc 2.2.2\r\n"+
                        "a=recvonly\r\n"+
                        "a=type:broadcast\r\n"+
                        "a=charset:UTF-8\r\n"+
                        "a=control:rtsp://192.168.1.106:8554/s1\r\n";

                        if(audioControl.length()>0) {
                            sessionDescriptionAsText +=
                            "m=audio 0 RTP/AVP 96\r\n" +
                            "b=RR:0\r\n" +
                            "a=rtpmap:96 mpeg4-generic/44100/2\r\n" +
                            "a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;\r\n" +
                            "a=control:rtsp://192.168.1.106:8554/"+audioControl+"\r\n";
                        }
                        if(videoControl.length()>0) {
                            sessionDescriptionAsText +=
                            "m=video 0 RTP/AVP 96\r\n" +
                            "b=RR:0\r\n" +
                            "a=rtpmap:96 H264/90000\r\n" +
                            "a=fmtp:96 packetization-mode=1;profile-level-id=42e00d;sprop-parameter-sets=J0LgDakYKD9gDUGAQa23oC8B6XvfAQ==,KM4JiA==;\r\n" +
                            "a=control:rtsp://192.168.1.106:8554/"+videoControl+"\r\n";
                        }
        final SessionDescription sessionDescription = SessionDescriptionProtocol.decode(sessionDescriptionAsText);
        final Body body = new Body(sessionDescription);
        return new RTSPResponse(StatusCode.OK, version, header, body);
    }

    private RTSPResponse createRTSPResponseSetUp(int CSeq, Transport clientTransport) {

        Header header = new Header(CSeq);
        clientTransport.setServerPortRange(26671, 26673);
        clientTransport.setMode(Transport.Mode.PLAY);
        clientTransport.setSsrc(0xAABBCCDD);
        final String transportAsText = TransportEncoder.encode(clientTransport);
        header.addHeaderField(new HeaderField(HeaderField.KnownName.Transport, transportAsText));
        return new RTSPResponse(StatusCode.OK, version, header);
    }
}
