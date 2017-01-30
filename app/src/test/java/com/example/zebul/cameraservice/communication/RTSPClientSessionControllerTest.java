package com.example.zebul.cameraservice.communication;

import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.URI;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Transport;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.TransportDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.TransportEncoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequest;
import com.example.zebul.cameraservice.av_streaming.rtsp.response.RTSPResponse;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;

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
        URI requestUri = URI.decodeFromString("rtsp://192.168.1.106:8554/s1");
        controller = new RTSPClientSessionController(userAgent, requestUri);
    }

    @Test
    public void test_controller_generates_expected_requests(){

        controller.setUp();

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
        controller.consumeRTSPResponse(createRTSPResponseDescribe(CSeq));

        //--- SETUP
        CSeq++;
        final RTSPRequest rtspRequest_setup = controller.produceRTSPRequest();
        assertEquals(Method.SETUP, rtspRequest_setup.getMethod());
        assertEquals(CSeq, rtspRequest_setup.getCSeq());
        final HeaderField headerFieldTransport = rtspRequest_setup.findHeaderField(HeaderField.KnownName.Transport);
        final String transportAsText = headerFieldTransport.getValue();
        Transport transport = TransportDecoder.decode(transportAsText);
        assertTrue(transport.getMinClientPort()>0);
        assertTrue(transport.getMaxClientPort()>0);
        controller.consumeRTSPResponse(createRTSPResponseSetUp(CSeq, transport));

        //--- PLAY
        CSeq++;
        final RTSPRequest rtspRequest_play = controller.produceRTSPRequest();
        assertEquals(Method.PLAY, rtspRequest_play.getMethod());
        assertEquals(CSeq, rtspRequest_play.getCSeq());
        final HeaderField headerFieldRange = rtspRequest_play.findHeaderField(HeaderField.KnownName.Range);
        assertNotNull(headerFieldRange);


        controller.tearDown();
    }

    private RTSPResponse createRTSPResponseOption(int CSeq) {

        Header headerResponse_option = new Header(CSeq);
        final HeaderField headerField_optionPublic = new HeaderField(HeaderField.KnownName.Public, "DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE,GET_PARAMETER");
        headerResponse_option.addHeaderField(headerField_optionPublic);
        final HeaderField headerField_optionContentLenght = new HeaderField(HeaderField.KnownName.Content_Length, 0);
        headerResponse_option.addHeaderField(headerField_optionContentLenght);
        return new RTSPResponse(StatusCode.OK, version, headerResponse_option);
    }

    private RTSPResponse createRTSPResponseDescribe(int CSeq) {

        Header header = new Header(CSeq);
        final HeaderField headerFieldContentType = new HeaderField(HeaderField.KnownName.Content_Type, "application/sdp");
        return new RTSPResponse(StatusCode.OK, version, header);
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
