package com.example.zebul.cameraservice.av_streaming.rtsp.request;

import com.example.zebul.cameraservice.av_streaming.rtsp.Method;
import com.example.zebul.cameraservice.av_streaming.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_streaming.rtsp.URI;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSPRequestError;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;

import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 1/15/17.
 */

public class RTSPRequestEncoderTest {

    private static final String SEP = RTSPProtocol.LINE_SEPARATOR;

    @Test
    public void test_when_RTSPRequest_is_encoded_then_result_equals_expected_value() throws RTSPRequestError, MalformedURLException {

        //given
        final Version version = new Version(1, 0);
        final HeaderFields headerFields = new HeaderFields();
        headerFields.add(new HeaderField("User-Agent", "LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)"));
        final Header header = new Header(2, headerFields);
        final Method method = Method.OPTIONS;
        final URI requestUri = URI.decodeFromString("rtsp://192.168.1.106:8554/s1");
        final RTSPRequest rtspRequest = new RTSPRequest(requestUri, version, header, method);

        String expectedRequest =
                "OPTIONS rtsp://192.168.1.106:8554/s1 RTSP/1.0\r\n"+
                "CSeq: 2\r\n"+
                "User-Agent: LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)\r\n"+
                "\r\n";

        //when
        String actualRequest = RTSPRequestEncoder.encode(rtspRequest);

        //then
        assertEquals(expectedRequest, actualRequest);
    }
}
