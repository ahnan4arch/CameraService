package com.example.zebul.cameraservice;

import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;

import org.junit.Assert;
import org.junit.Test;

public class NalUnitReaderTest {

	@Test
	public void test2() {
		
		byte [] data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		byte [] pattern = new byte[]   {2, 3};
		int pos = NALUnitReader.findPosOfPattern(data, pattern, 0);
		Assert.assertEquals(2, pos);
	}
	
	@Test
	public void test3() {
		
		byte [] data = new byte[]{0, 1, 2, 3, 4, 5, 2, 3, 8, 9};
		byte [] pattern = new byte[]               {2, 3};
		int pos = NALUnitReader.findPosOfPattern(data, pattern, 3);
		Assert.assertEquals(6, pos);
	}

}
