package com.example.zebul.cameraservice.video_streaming.rtsp.version;

public class Version {

	private int majorVersion;
	private int minorVersion;
	
	public Version(int majorVersion, int minorVersion){
		
		this.majorVersion = majorVersion; 
		this.minorVersion = minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}
}
