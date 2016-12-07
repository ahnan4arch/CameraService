package com.example.zebul.cameraservice.video_streaming.rtp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 11/20/16.
 */

public class RTPPackets implements Iterable<RTPPacket>{

    private List<RTPPacket> rtpPackets = new LinkedList<>();

    @Override
    public Iterator<RTPPacket> iterator() {
        return rtpPackets.iterator();
    }

    public void addRTPPacket(RTPPacket rtpPacket) {

        rtpPackets.add(rtpPacket);
    }

    public int getNumberOfPackets() {

        return rtpPackets.size();
    }

    public void addRTPPackets(List<RTPPacket> rtpPackets) {

        this.rtpPackets.addAll(rtpPackets);
    }
}
