package com.example.zebul.cameraservice.av_streaming;

/**
 * Created by zebul on 12/27/16.
 */

public class Resolution {

    public static final Resolution _640x480  = new Resolution(640, 480);


    private int width;
    private int height;

    public Resolution(int width, int height){

        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
