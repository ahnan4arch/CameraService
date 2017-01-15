package com.example.zebul.cameraservice.av_streaming.rtsp;

import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionEncoder;

import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;

/**
 * Created by zebul on 12/11/16.
 */

public class URITest {

    @Test
    public void test_when_uri_contains_track_id_then_it_is_retrievable__scenario_uri_does_not_contain_user_data() throws MalformedURLException {

        URI uri = URI.decodeFromString("rtsp://192.168.1.21:9999/trackID=2");
        Assert.assertEquals(2, uri.getTrackId());
    }

    @Test
    public void test_when_uri_contains_track_id_then_it_is_retrievable__scenario_uri_contains_user_data() throws MalformedURLException {

        URI uri = URI.decodeFromString("rtsp://richard:myPassword@192.168.1.21:9999/trackID=5");
        Assert.assertEquals(5, uri.getTrackId());
    }

    @Test
    public void test_whether_uri_fromString_equals_uri_toString() throws MalformedURLException {

        String expectedUriString = "rtsp:192.168.1.21:9999/trackID=5";
        URI uri = URI.decodeFromString(expectedUriString);
        String actualUriString = uri.toString();
        Assert.assertEquals(expectedUriString, expectedUriString);
    }
}
