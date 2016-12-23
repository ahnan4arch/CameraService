package com.example.zebul.cameraservice.av_streaming.rtp.h264.payload.fragmentation_unit;

import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnitType;

/**
 * Created by zebul on 11/27/16.
 */

public class FUHeader {

    public static final int LENGTH = 1;
    private boolean isStart;
    private boolean isEnd;
    private NALUnitType nalUnitType;

    public FUHeader(
            boolean isStart,
            boolean isEnd,
            NALUnitType nalUnitType){

        this.isStart    = isStart;
        this.isEnd      = isEnd;
        this.nalUnitType= nalUnitType;
    }

    public void toByte(byte[] rtpPayloadBytes, int position){

        rtpPayloadBytes[position] = toByte();
    }

    /*
    The FU header has the following format:
    +---------------+
    |0|1|2|3|4|5|6|7|
    +-+-+-+-+-+-+-+-+
    |S|E|R|  Type   |
    +---------------+
    */
    public byte toByte(){

        byte S = 0;//start
        byte E = 0;//end
        byte R = 0;//reserved bit
        if(isStart){
            S = (byte)0b10000000;
        }
        if(isEnd){
            E = (byte)0b01000000;
        }
        byte type = (byte)nalUnitType.ordinal();
        return (byte)(S|E|R|type);
    }
}
