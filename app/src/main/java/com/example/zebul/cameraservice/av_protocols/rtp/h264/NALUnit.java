package com.example.zebul.cameraservice.av_protocols.rtp.h264;

import com.example.zebul.cameraservice.av_protocols.rtp.basic.DataUnit;

public class NALUnit extends DataUnit {

	public static final byte [] START_CODES = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01};

	public NALUnit(byte[] data) {
		super(data);
	}
}
