package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

public class NALUnit {

	private int beg;
	private byte[] data;
	
	public NALUnit(int beg, byte[] data) {
		this.beg = beg;
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public int getBeg(){
		return beg;
	}
	public int getEnd(){
		return (beg+data.length);
	}
}
