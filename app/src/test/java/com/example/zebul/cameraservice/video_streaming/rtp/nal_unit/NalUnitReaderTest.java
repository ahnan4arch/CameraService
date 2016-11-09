package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

import org.junit.Assert;
import org.junit.Test;

public class NalUnitReaderTest {

	@Test
	public void test0() {

		byte[] data = NALUnitReader.readDataFromFile("/home/zebul/Videos/sample_iPod.m4v");
		byte [] pattern = null;

		byte [] pattern39 = new byte[]{
				(byte)0x61, (byte)0xa2, (byte)0xd8, (byte)0xa8, (byte)0xd0, (byte)0x20, (byte)0x4b, (byte)0xc0,
				(byte)0x3a, (byte)0x03, (byte)0xad, (byte)0x30, (byte)0xbd, (byte)0x39, (byte)0x5d, (byte)0xdc};
		pattern = pattern39;

		/*
		byte [] pattern40 = new byte[]{
				(byte)0x77, (byte)0xd0, (byte)0xca, (byte)0x81, (byte)0x00, (byte)0xdf, (byte)0xff, (byte)0xda,
				(byte)0x08, (byte)0x4f, (byte)0x00, (byte)0x0b, (byte)0xd2, (byte)0x28, (byte)0x0c, (byte)0x58};
		pattern = pattern40;
		*/

		/*
		byte [] pattern41 = new byte[]{
				(byte)0x80, (byte)0x27, (byte)0xcb, (byte)0xbf, (byte)0x71, (byte)0x28, (byte)0x7a, (byte)0x7e,
				(byte)0xe2, (byte)0x50, (byte)0x19, (byte)0xdc, (byte)0xd2, (byte)0x27, (byte)0x1f, (byte)0x17};
		pattern = pattern41;
		*/

		/*
		byte [] pattern42 = new byte[]{
			(byte)0x61, (byte)0xa1, (byte)0x58, (byte)0xa9, (byte)0x06, (byte)0x20, (byte)0x3b, (byte)0xf2,
			(byte)0x0f, (byte)0x67, (byte)0x0b, (byte)0x16, (byte)0xa5, (byte)0xe9, (byte)0x36, (byte)0x38};
		pattern = pattern42;
		*/

		/*
		byte [] pattern43 = new byte[]
				{(byte)0x61, (byte)0xa3, (byte)0x59, (byte)0x20, (byte)0x22, (byte)0x10, (byte)0x3b, (byte)0x60,
				 (byte)0xf2, (byte)0xe8, (byte)0xc5, (byte)0x0b, (byte)0x59, (byte)0x86, (byte)0xfa, (byte)0xd6};
		pattern = pattern43;
		*/
		int pos = NALUnitReader.findPosOfPattern(data, pattern, 0);
		int foo = 1;
		int bar = foo;
	}

	@Test
	public void test1() {
		
		NALUnitReader reader = new NALUnitReader("/home/zebul/Videos/H264_artifacts_motion.h264"/*sample_iPod.m4v"*//*, null*/);
		int beg = 0;
		while(true){

			NALUnit nalUnit = reader.read();
			byte [] data = reader.getData();

			byte [] seq9218 = new byte[100];
			System.arraycopy(data, 51700, seq9218, 0, seq9218.length);
			print("offset in file: 51700: ", 0, seq9218.length, seq9218);

			byte [] seq9219 = new byte[100];
			System.arraycopy(data, 52100, seq9219, 0, seq9218.length);
			print("offset in file: 52100: ", 0, seq9219.length, seq9219);

			byte [] seq9220 = new byte[100];
			System.arraycopy(data, 52500, seq9220, 0, seq9220.length);
			print("offset in file: 52500: ", 0, seq9220.length, seq9220);

			Assert.assertEquals(beg, nalUnit.getBeg());
			beg = nalUnit.getEnd();
			System.out.println("--> beg:"+beg);
			if(beg == -1){
				return;
			}
			if((beg-20) > 0){
				print("left from: "+beg, beg-20, 20, data);
			}
			else if((beg+20)<data.length){
				print("right from: "+beg, beg, 20, data);
			}
			System.out.println("----------------------\n");
		}
	}

	private void print(String text, int beg, int len, byte[] data) {

		StringBuilder sb = new StringBuilder(text);
		for (int i=0; i<len; i++) {
			sb.append(String.format("%02x ", data[beg+i]));
		}
		String value = sb.toString();
		System.out.println(value);
	}

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
