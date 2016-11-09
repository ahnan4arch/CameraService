package com.example.zebul.cameraservice.video_streaming.rtsp.version;

public class VersionEncoder {

	public static String encode(Version version){
		
		return String.format("RTSP/%d.%d", version.getMajorVersion(), version.getMinorVersion());
	}
}
