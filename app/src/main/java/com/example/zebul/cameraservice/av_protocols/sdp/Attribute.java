package com.example.zebul.cameraservice.av_protocols.sdp;

/**
 * Created by zebul on 1/27/17.
 */

public class Attribute {

    private String type;
    private String value;

    public Attribute(String type) {

        this(type, "");
    }

    public Attribute(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }


}
