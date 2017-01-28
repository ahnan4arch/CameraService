package com.example.zebul.cameraservice.av_streaming.sdp;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 1/26/17.
 */

public class MediaDescription {

    public enum MediaType {
        Audio,
        Video,
        Text,
        Application,
        Message,
    }

    private MediaType mediaType;
    private int port;
    private String protocol;
    private String format;
    private List<Attribute> attributes = new LinkedList<Attribute>();

    public MediaDescription(MediaType mediaType, int port, String protocol, String format) {

        this.mediaType = mediaType;
        this.port = port;
        this.protocol = protocol;
        this.format = format;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public int getPort() {
        return port;
    }

    public void addAttribute(Attribute attribute) {

        attributes.add(attribute);
    }

    public List<Attribute> getAttributes() {

        return attributes;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getFormat() {
        return format;
    }

}
