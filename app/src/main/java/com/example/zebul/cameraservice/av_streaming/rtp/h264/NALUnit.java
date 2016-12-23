package com.example.zebul.cameraservice.av_streaming.rtp.h264;

import com.example.zebul.cameraservice.av_streaming.rtp.basic.DataUnit;

public class NALUnit extends DataUnit {

	public static final byte [] START_CODES = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01};

	private int beg;
	
	public NALUnit(int beg, byte[] data) {
		super(data);
		this.beg = beg;
	}

	public int getBeg(){
		return beg;
	}
	public int getEnd(){
		return (beg+data.length);
	}
}
