package com.example.zebul.cameraservice.av_streaming.av_packet.basic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 11/15/16.
 */

public abstract class DataPackets<DATA_PACKET extends DataPacket> implements Iterable<DATA_PACKET>{

    private List<DATA_PACKET> avPackets = new LinkedList<DATA_PACKET>();

    public void addPacket(DATA_PACKET packet){

        avPackets.add(packet);
    }

    public List<DATA_PACKET> getPackets() {
        return avPackets;
    }

    @Override
    public Iterator<DATA_PACKET> iterator() {
        return avPackets.iterator();
    }

    public int getNumberOfPackets(){

        return avPackets.size();
    }

    public boolean containsPackets() {

        return !avPackets.isEmpty();
    }
}
