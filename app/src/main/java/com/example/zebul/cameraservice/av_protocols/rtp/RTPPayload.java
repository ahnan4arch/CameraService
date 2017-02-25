package com.example.zebul.cameraservice.av_protocols.rtp;

/**
 * Created by zebul on 12/21/16.
 */

public interface RTPPayload {

    void toBytes(byte[] rtpPacketBytes, int position);
    int computeLenght();
}
