package com.example.zebul.cameraservice.av_protocols.rtsp.request;

import com.example.zebul.cameraservice.av_protocols.rtsp.Method;
import com.example.zebul.cameraservice.av_protocols.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_protocols.rtsp.URI;
import com.example.zebul.cameraservice.av_protocols.rtsp.error.RTSPError;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderField;
import com.example.zebul.cameraservice.av_protocols.rtsp.message.header.HeaderFields;
import com.example.zebul.cameraservice.av_protocols.rtsp.version.Version;

import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

/**
 * Created by zebul on 1/15/17.
 */

public class RTSPRequestEncoderTest {

    private static final String SEP = RTSPProtocol.LINE_SEPARATOR;

    @Test
    public void test_when_RTSPRequest_is_encoded_then_result_equals_expected_value() throws RTSPError, MalformedURLException {

        //given
        final Version version = new Version(1, 0);
        final HeaderFields headerFields = new HeaderFields();
        headerFields.add(new HeaderField(HeaderField.KnownName.CSeq, 2));
        headerFields.add(new HeaderField(HeaderField.KnownName.User_Agent,
                "LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)"));
        final Header header = new Header(headerFields);
        final Method method = Method.OPTIONS;
        final URI requestUri = URI.fromString("rtsp://192.168.1.106:8554/s1");
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
