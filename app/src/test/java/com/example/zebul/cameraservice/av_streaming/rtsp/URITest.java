package com.example.zebul.cameraservice.av_streaming.rtsp;

import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionEncoder;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zebul on 12/11/16.
 */

public class URITest {

    @Test
    public void test_when_uri_contains_track_id_then_it_is_retrievable() {

        URI uri = new URI("rtsp://192.168.1.21:9999/trackID=2");
        Assert.assertEquals(2, uri.getTrackId());
    }
}
