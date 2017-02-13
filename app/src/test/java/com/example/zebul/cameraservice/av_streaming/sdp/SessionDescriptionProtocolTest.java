package com.example.zebul.cameraservice.av_streaming.sdp;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by zebul on 1/26/17.
 */

public class SessionDescriptionProtocolTest {

    @Test
    public void test1() {

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
                "a=control:rtsp://192.168.1.106:8554/s1\r\n"+

                "m=audio 0 RTP/AVP 96\r\n"+
                "b=RR:0\r\n"+
                "a=rtpmap:96 mpeg4-generic/44100/2\r\n"+
                "a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;\r\n"+
                "a=control:rtsp://192.168.1.106:8554/s1/trackID=4\r\n"+

                "m=video 0 RTP/AVP 96\r\n"+
                "b=RR:0\r\n"+
                "a=rtpmap:96 H264/90000\r\n"+
                "a=fmtp:96 packetization-mode=1;profile-level-id=42e00d;sprop-parameter-sets=J0LgDakYKD9gDUGAQa23oC8B6XvfAQ==,KM4JiA==;\r\n"+
                "a=control:rtsp://192.168.1.106:8554/s1/trackID=5\r\n";
        final SessionDescription sessionDescription = SessionDescriptionProtocol.decode(sessionDescriptionAsText);
        assertNotNull(sessionDescription);

        assertEquals("Unnamed", sessionDescription.name);

        final List<MediaDescription> mediaDescriptions = sessionDescription.getMediaDescriptions();
        assertTrue(mediaDescriptions.size()>0);

        for(MediaDescription mediaDescription: mediaDescriptions){

            assertNotNull(mediaDescription);
            for(Attribute attribute: mediaDescription.getAttributes()){

                assertNotNull(attribute);
            }
        }

        final String valueOfControlAttribute = sessionDescription.findVideoMediaValueOfAttribute("control");
        assertEquals("rtsp://192.168.1.106:8554/s1/trackID=5", valueOfControlAttribute);

    }
}
