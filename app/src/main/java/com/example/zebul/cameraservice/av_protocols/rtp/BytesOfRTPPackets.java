package com.example.zebul.cameraservice.av_protocols.rtp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zebul on 1/31/17.
 */

public class BytesOfRTPPackets implements Iterable<byte[]>{

    List<byte[]> bytesOfRTPPackets = new LinkedList<byte[]>();
    @Override
    public Iterator<byte[]> iterator() {
        return bytesOfRTPPackets.iterator();
    }

    public int getNumberOfPackets() {

        return bytesOfRTPPackets.size();
    }

    public void addRTPPacketBytes(byte[] bytes) {

        bytesOfRTPPackets.add(bytes);
    }
}
