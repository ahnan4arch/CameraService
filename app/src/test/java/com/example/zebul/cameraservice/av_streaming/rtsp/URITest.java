package com.example.zebul.cameraservice.av_streaming.rtsp;

import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionEncoder;

import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zebul on 12/11/16.
 */

public class URITest {

    @Test
    public void test_whether_uri_fromString_equals_uri_toString() throws MalformedURLException {

        String expectedUriString = "rtsp:192.168.1.21:9999/trackID=5";
        URI uri = URI.decodeFromString(expectedUriString);
        String actualUriString = uri.toString();
        Assert.assertEquals(expectedUriString, expectedUriString);
    }

    @Test
    public void test_when_file_exists_then_it_can_be_obtained_via_getFile() throws MalformedURLException {

        String expectedFile = "trackID=5";
        String expectedUriString = "rtsp://192.168.1.21:9999/"+expectedFile;
        URI uri = URI.decodeFromString(expectedUriString);
        Assert.assertEquals(expectedFile, uri.getFileWithoutSpecialLeadingChars());
    }
}
