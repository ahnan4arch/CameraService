package com.example.zebul.cameraservice.video_streaming.video_data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 11/15/16.
 */

public class AVPackets implements Iterable<AVPacket>{

    private List<AVPacket> avPackets = new LinkedList<AVPacket>();

    public void addAVPacket(AVPacket avPacket){

        avPackets.add(avPacket);
    }

    public List<AVPacket> getAVPackets() {
        return avPackets;
    }

    @Override
    public Iterator<AVPacket> iterator() {
        return avPackets.iterator();
    }

    public int getNumberOfPackets(){

        return avPackets.size();
    }
}
