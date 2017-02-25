package com.example.zebul.cameraservice.av_protocols.rtsp.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionDecoder {

	private static final Pattern RTSPPattern = Pattern.compile(".*RTSP/(\\d+).(\\d+).*");
	public static Version decode(String versionAsText) throws VersionDecodeException{
		
		Matcher matcher = RTSPPattern.matcher(versionAsText);
		if(!matcher.find()){
			throw new VersionDecodeException("Bad version format: "+versionAsText);
		}
		
		int majorVersion = 0;
		String value = matcher.group(1);
		try{
			majorVersion = Integer.parseInt(value);
		}
		catch (NumberFormatException error) {
			throw new VersionDecodeException("Bad format of major version: "+value);
		}
		
		int minorVersion = 0;
		value = matcher.group(2);
		try{
			minorVersion = Integer.parseInt(value);
		}
		catch (NumberFormatException error) {
			throw new VersionDecodeException("Bad format of minor version: "+value);
		}
		
		return new Version(majorVersion, minorVersion);
	}
}
