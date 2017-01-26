package com.example.zebul.cameraservice.av_streaming.rtsp.response;

import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP5xxServerResponseError;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderField;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by zebul on 1/15/17.
 */

public class RTSPResponseDecoderTest {

    @Test
    public void test_when_response_given_as_text_is_decoded_then_fields_of_response_instance_have_expected_values__scenario_response_OPTIONS()
            throws RTSP5xxServerResponseError {

        //given
        String responseRepresentaionAsText =
                        "RTSP/1.0 200 OK\r\n"+
                        "Server: VLC/2.2.2\r\n"+
                        "Content-length: 0\r\n"+
                        "Cseq: 2\r\n"+
                        "Public: DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE,GET_PARAMETER\r\n"+
                        "\r\n";

        //when
        final RTSPResponse rtspResponse = RTSPResponseDecoder.decode(responseRepresentaionAsText);

        //then
        assertNotNull(rtspResponse);
        assertNotNull(rtspResponse.getStatusCode());
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.CSeq), "2");
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.Content_Length), "0");
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.Public), "DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE,GET_PARAMETER");
    }

    @Test
    public void test_when_response_given_as_text_is_decoded_then_fields_of_response_instance_have_expected_values__scenario_response_DESCRIBE()
            throws RTSP5xxServerResponseError {

        //given
        String responseRepresentaionAsText =
                "RTSP/1.0 200 OK\r\n"+
                "Server: VLC/2.2.2\r\n"+
                "Date: Wed, 25 Jan 2017 16:28:29 GMT\r\n"+
                "Content-type: application/sdp\r\n"+
                "Content-Base: rtsp://192.168.1.106:8554/s1\r\n"+
                "Content-length: 691\r\n"+
                "Cache-Control: no-cache\r\n"+
                "Cseq: 3\r\n"+
                "\r\n"+
                    "Session Description Protocol\r\n"+
                    "Session Description Protocol Version (v): 0\r\n"+
                    "Owner/Creator, Session Id (o): - 15867114619150279987 15867114619150279987 IN IP4 zebul-NV78\r\n"+
                    "Session Name (s): Unnamed\r\n"+
                    "Session Information (i): N/A\r\n"+
                    "Connection Information (c): IN IP4 0.0.0.0\r\n"+
                    "Time Description, active time (t): 0 0\r\n"+
                    "Session Attribute (a): tool:vlc 2.2.2\r\n"+
                    "Session Attribute (a): recvonly\r\n"+
                    "Session Attribute (a): type:broadcast\r\n"+
                    "Session Attribute (a): charset:UTF-8\r\n"+
                    "Session Attribute (a): control:rtsp://192.168.1.106:8554/s1\r\n"+
                    "Media Description, name and address (m): audio 0 RTP/AVP 96\r\n"+
                    "Bandwidth Information (b): RR:0\r\n"+
                    "Media Attribute (a): rtpmap:96 mpeg4-generic/44100/2\r\n"+
                    "Media Attribute (a): fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;\r\n"+
                    "Media Attribute (a): control:rtsp://192.168.1.106:8554/s1/trackID=4\r\n"+
                    "Media Description, name and address (m): video 0 RTP/AVP 96\r\n"+
                    "Bandwidth Information (b): RR:0\r\n"+
                    "Media Attribute (a): rtpmap:96 H264/90000\r\n"+
                    "Media Attribute (a): fmtp:96 packetization-mode=1;profile-level-id=42e00d;sprop-parameter-sets=J0LgDakYKD9gDUGAQa23oC8B6XvfAQ==,KM4JiA==;\r\n"+
                    "Media Attribute (a): control:rtsp://192.168.1.106:8554/s1/trackID=5\r\n";

        //when
        final RTSPResponse rtspResponse = RTSPResponseDecoder.decode(responseRepresentaionAsText);

        //then
        assertNotNull(rtspResponse);
        assertEquals(StatusCode.OK, rtspResponse.getStatusCode());

        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.Server), "VLC/2.2.2");
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.Date), "Wed, 25 Jan 2017 16:28:29 GMT");
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.Content_Type), "application/sdp");
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.Content_Base), "rtsp://192.168.1.106:8554/s1");
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.Content_Length), "691");
        assertEquals(rtspResponse.getHeaderFieldValue(HeaderField.KnownName.CSeq), "3");

    }
}
