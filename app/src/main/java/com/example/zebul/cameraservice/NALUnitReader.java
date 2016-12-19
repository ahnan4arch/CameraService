package com.example.zebul.cameraservice;


import com.example.zebul.cameraservice.av_streaming.rtp.nal_unit.NALUnit;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class NALUnitReader {

	private String fileFullPath;

	private byte[] data;
	private int endExclusive = 0;
	private int shift = 0;
	private static final int NOT_FOUND = -1; 
	
	public NALUnitReader(String fileFullPath){
		
		this.fileFullPath = fileFullPath;
	}

	public NALUnitReader(byte [] data){

		this.data = data;
	}
	
	public NALUnit read(){
		
		if(data == null){
			data = readDataFromFile(fileFullPath);
		}
		if(data == null){
			return null;
		}

		if(endExclusive == 0){
			moveEnd();
		}
		int begInclusive = endExclusive;
		moveEnd();
		byte [] nalUnitData = new byte[endExclusive-begInclusive];
		System.arraycopy(data, begInclusive, nalUnitData, 0, nalUnitData.length);
		return new NALUnit(begInclusive, nalUnitData);
	}

	private void moveEnd(){

		endExclusive = findPosOfPattern(data, NALUnit.START_CODES, endExclusive+shift);
		shift = 1;
		if(0 < endExclusive){
			if(data[endExclusive-1]==0){
				endExclusive--;
				shift=2;
			}
		}
	}

	public static byte[] readDataFromFile(String fileFullPath) {
		
		try {
			File file = new File(fileFullPath);
		    byte [] data = new byte[(int) file.length()];
		    DataInputStream dis;
			dis = new DataInputStream(new FileInputStream(file));
			dis.readFully(data);
		    dis.close();
		    return data;
		} catch (FileNotFoundException exc_) {
			exc_.printStackTrace();
			return null;
		} catch (IOException exc_) {
			exc_.printStackTrace();
			return null;
		}
	}

	/*
	private byte[] readData(String fileFullPath) {

		try {
			AssetManager assetManager = context.getAssets();
			InputStream inputStream = assetManager.open(fileFullPath);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int readCount = 0;
			byte[] data = new byte[1024];
			while ((readCount = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, readCount);
			}
			buffer.flush();
			return buffer.toByteArray();

		} catch (FileNotFoundException exc_) {
			exc_.printStackTrace();
			return null;
		} catch (IOException exc_) {
			exc_.printStackTrace();
			return null;
		}
	}*/
	
	public static int findPosOfPattern(byte[] data_, byte[] pattern_, int offset_) {
		
		for(int i = offset_; i < data_.length - pattern_.length+1; ++i) {
			boolean found = true;
			for(int j = 0; j < pattern_.length; ++j) {
				if (data_[i+j] != pattern_[j]) {
					found = false;
					break;
				}
			}
			if (found) return i;
		}
		return NOT_FOUND;  
	}

    public byte[] getData() {
        return data;
    }
}
