package com.example.zebul.cameraservice.av_streaming.packetization;

import com.example.zebul.cameraservice.av_streaming.rtp.header.RTPHeader;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by zebul on 11/5/16.
 */

public class PacketizerTest {

    @Test
    public void test_when_NALUnit_has_grater_length_than_MTU_then_make_fragmentation_units() {

        Packetizer packetizer = new Packetizer();
        int maxLengthOfPacket = 2;
        packetizer.setMaxPayloadLength(maxLengthOfPacket);
        byte NALUnitHeader = 0x01;
        byte [] NALUnit = new byte[]{NALUnitHeader, 0x11, 0x22, 0x33, 0x44, 0x55};
        int NALUnitOffset = 0;
        List<byte[]> packets = packetizer.makePackets(NALUnit, NALUnitOffset);
        assertEquals(3, packets.size());

        //packet0
        byte [] packet0 = packets.get(0);
        int expectedLengthOfpacket0 =
                RTPHeader.LENGTH+Packetizer.FU_INDICATOR_LENGTH+Packetizer.FU_HEADER_LENGTH+maxLengthOfPacket;
        assertEquals(expectedLengthOfpacket0, packet0.length);

        byte encodedFUHeader = packet0[RTPHeader.LENGTH+Packetizer.FU_INDICATOR_LENGTH];
        byte S = (byte)0b10000000;
        byte E = (byte)0b01000000;
        assertTrue((encodedFUHeader&S)==S);//start must be set
        assertTrue((encodedFUHeader&E)==0);//end must not be set

        assertEquals(0x11, packet0[packet0.length-2]);
        assertEquals(0x22, packet0[packet0.length-1]);

        //packet1
        byte [] packet1 = packets.get(1);
        int expectedLengthOfpacket1 =
                RTPHeader.LENGTH+Packetizer.FU_INDICATOR_LENGTH+Packetizer.FU_HEADER_LENGTH+maxLengthOfPacket;
        assertEquals(expectedLengthOfpacket1, packet1.length);

        encodedFUHeader = packet1[RTPHeader.LENGTH+Packetizer.FU_INDICATOR_LENGTH];
        assertTrue((encodedFUHeader&S)==0);//start must not be set
        assertTrue((encodedFUHeader&E)==0);//end must not be set

        assertEquals(0x33, packet1[packet1.length-2]);
        assertEquals(0x44, packet1[packet1.length-1]);

        //packet2
        byte [] packet2 = packets.get(2);
        int expectedLengthOfpacket2 =
                RTPHeader.LENGTH+Packetizer.FU_INDICATOR_LENGTH+Packetizer.FU_HEADER_LENGTH+1;
        assertEquals(expectedLengthOfpacket2, packet2.length);

        encodedFUHeader = packet2[RTPHeader.LENGTH+Packetizer.FU_INDICATOR_LENGTH];
        assertTrue((encodedFUHeader&S)==0);//start must not be set
        assertTrue((encodedFUHeader&E)==E);//end must be set

        assertEquals(0x55, packet2[packet2.length-1]);
    }
}

/*
seq: 9218, len: 451, pos in file: 51721

seq: 9219, len: 479, pos in file: 52114

seq: 9220, len: 527, pos in file: 52535

dist in file:
52535-52114 = 421
52114-51721 = 393

421-393 = 28

diff of packet len
479-451 = 28

offset in file: 51700: 80 2f e4 e6 8a eb 2b c4 ec 78 60 1b 43 f1 1c 21 0c 14 5d d2 87 61 a2 d8 a8 d0 20 4b c0 3a 03 ad 30 bd 39 5d dc ce d7 ab 48 1d 95 56 54 41 c8 5b 42 10 45 a2 72 0b 08 e0 a5 50 cd a0 00 4a cd 22 39 67 5a c7 f5 02 51 2c 11 31 b9 db 2a 96 80 39 33 0e 92 a6 ae 90 50 c2 e7 6f cf 7d 3e 39 22 12 d0 22 a0 81

39	8.642091821	192.168.1.106	192.168.1.4	RTP	451	PT=DynamicRTP-Type-96, SSRC=0xDE26B36, Seq=9218, Time=178889886, Mark
0000   5c 0a 5b 53 fe eb 00 1e 65 a4 9c a8 08 00 45 00
0010   01 b5 40 fa 40 00 40 11 74 7f c0 a8 01 6a c0 a8
0020   01 04 b3 bb ea 58 01 a1 e0 59 80 e0 24 02 0a a9
0030   a4 9e 0d e2 6b 36 P-> 00 10 0c 48 21 0c 14 5d d2 87
0040   61 a2 d8 a8 d0 20 4b c0 3a 03 ad 30 bd 39 5d dc  pos in file 51721
0050   ce d7 ab 48 1d 95 56 54 41 c8 5b 42 10 45 a2 72
0060   0b 08 e0 a5 50 cd a0 00 4a cd 22 39 67 5a c7 f5
0070   02 51 2c 11 31 b9 db 2a 96 80 39 33 0e 92 a6 ae
0080   90 50 c2 e7 6f cf 7d 3e 39 22 12 d0 22 a0 81 fb
0090   52 25 79 17 18 91 85 f2 9a 8f 3a 80 9a 89 e4 5d
00a0   1d c8 be bf ab 69 9e e7 e6 6c 3f b0 1d f9 e2 92
00b0   e5 95 7f 05 45 f1 7d 15 ea 3e 89 6b 0e 8a 24 45
00c0   c0 a7 24 39 e1 6e 7a 86 69 3d 44 f3 42 17 7c 67
00d0   42 0f 6c 7a e6 fb 85 86 be 6f c0 29 bf e2 12 06
00e0   9a 8b 6f 0e e5 73 d3 da 3e da 83 ae 4a ff 44 87
00f0   a8 55 e3 76 4b ad 09 97 18 d2 30 93 78 0f 95 a3
0100   b7 6f 9c 34 db 7e bb b1 9b e4 92 a7 07 1e 39 61
0110   00 4c 9e 1e a0 68 a8 d3 ef e1 b0 ee 1e ac db 75
0120   ae 33 7b d2 f5 e0 8f 88 0a 1c 00 d4 28 22 fb 17
0130   f9 a9 bb 6b 23 cf 12 11 e3 11 5c 6e 1d c5 31 cf
0140   a1 84 69 45 ed 23 c6 0f 4e 50 16 bc dc 95 e8 62
0150   56 9e bc 7a 1e e5 26 5a 93 60 62 a0 03 cb da 5e
0160   75 57 f7 c6 36 39 92 6b ed 5d 02 17 3f 67 47 a3
0170   d6 ca 79 ae 8e 11 21 22 11 89 b0 8b 18 49 ae a8
0180   1a b9 69 b0 8d e6 3c 64 0b a6 ae 9b 77 71 aa 01
0190   45 1e ae 0a bd 23 26 d0 e8 d1 6c 78 60 b8 55 8e
01a0   23 91 10 27 4f d7 70 ee 55 ca 3c 9d 26 04 46 e7
01b0   23 33 42 69 c4 be c5 54 c7 d9 9d 09 dc 67 39 cd
01c0   61 f8 8e

40	8.654050069	192.168.1.106	192.168.1.4	RTP	578	PT=DynamicRTP-Type-96, SSRC=0x13949E06, Seq=22085, Time=365082486
0000   5c 0a 5b 53 fe eb 00 1e 65 a4 9c a8 08 00 45 00
0010   02 34 40 fd 40 00 40 11 73 fd c0 a8 01 6a c0 a8
0020   01 04 84 54 d2 ee 02 20 2c aa 80 60 56 45 15 c2
0030   b7 76 13 94 9e 06 P-> 21 f2 48 01 3a 78 53 08 f8 53
0040   77 d0 ca 81 00 df ff da 08 4f 00 0b d2 28 0c 58  pos in file 59170
0050   8f 81 0e 1d e8 01 08 90 ae 84 78 f2 48 08 a8 24
0060   49 e4 03 00 90 30 11 94 a1 42 70 9c ba 76 0f d3
0070   a9 07 48 fc 51 ff 65 0b 07 13 ad 7b d8 ee ff 66
0080   c7 ff ed bd 13 2d e5 20 8e b4 3e d6 06 fa 5a fa
0090   b3 7f ff c5 ac e6 89 51 ef e6 ff bf f1 5d 83 4b
00a0   85 1f 4b 7c df fe fe 33 b3 d1 bd fc df ff f1 7e
00b0   6b 1b f3 ef 9c 82 d7 37 ff f1 8c f1 a6 47 a3 97
00c0   2d 1a ff 23 51 2a 6f e3 a8 ba 9e de 4f f0 38 ec
00d0   21 e0 5a 1a d6 7e 04 58 94 8b 26 24 fd 8b b4 ca
00e0   5a dd b0 d2 47 d2 7f f3 81 78 a7 21 6c 05 a7 8b
00f0   6a 64 bc c5 a8 1f a0 60 14 2b 2a 0e 43 8f 5d b1
0100   72 c9 61 dc 6b 2d e6 87 36 1f 8f 1d a1 24 a0 14
0110   c5 b9 9f 8e a6 5c a6 c6 76 a1 80 b1 32 34 fc 4d
0120   a2 01 70 a2 07 10 00 1d 48 65 3b 0e 5b 79 c6 21
0130   5f f1 80 01 37 79 61 48 60 38 38 00 48 af 2c 0e
0140   00 12 2b cb 7f 7f cd 8f ff e1 e6 4a b2 fc df ff
0150   f0 f4 94 f3 7f ff c9 37 ff fc 93 7c 3f 8f 14 44
0160   b9 28 00 f4 38 d8 62 79 0e 1a 57 f7 52 40 1f 18
0170   a9 54 ac 01 8d 35 a9 72 76 1e 21 53 f2 64 e2 01
0180   e3 89 59 7b 5d 3e 36 6b 63 0f 1c a7 81 04 0f ea
0190   84 8e b5 3e 0e 63 08 fa 01 8f c2 75 48 45 44 e8
01a0   3b 72 32 1f 4f c5 f8 0c 85 79 2f 45 93 c1 38 50
01b0   78 2e 1a 16 18 19 05 88 19 17 f1 2d e0 04 08 0d
01c0   0a 48 d8 4e e9 47 69 44 a6 74 36 40 cd 5a 6b 58
01d0   9a c9 f8 d0 59 b8 c5 56 b2 32 be d4 a1 2d ed 50
01e0   d6 42 1b d2 a4 04 18 f5 04 a3 80 9a 24 f9 c4 bd
01f0   57 d3 3e f5 f8 2d cd f1 84 31 24 5b 11 c0 01 0d
0200   8a 08 eb 87 96 ba 4a 88 18 45 1f 84 a8 0c e9 2c
0210   00 a5 b2 01 be a0 4f c6 bb a4 80 8e 8d cf a3 49
0220   d0 a9 75 7f 09 1f f7 f8 b1 a8 80 1f c8 29 6c 04
0230   1e f9 81 0f e2 be 2c 32 fd a4 8f a1 53 cc 0e 3e
0240   f7 40

41	8.654120397	192.168.1.106	192.168.1.4	RTP	945	PT=DynamicRTP-Type-96, SSRC=0x13949E06, Seq=22086, Time=365082486, Mark
0000   5c 0a 5b 53 fe eb 00 1e 65 a4 9c a8 08 00 45 00
0010   03 a3 40 fe 40 00 40 11 72 8d c0 a8 01 6a c0 a8
0020   01 04 84 54 d2 ee 03 8f a7 9e 80 e0 56 46 15 c2
0030   b7 76 13 94 9e 06 P-> 21 01 2f c9 20 0d dc 31 28 43
0040   80 27 cb bf 71 28 7a 7e e2 50 19 dc d2 27 1f 17  pos in file 59698
0050   6a ce c5 0f a9 1f e3 d7 61 84 01 43 04 42 3a b7
0060   1c f0 08 39 49 bc f8 aa e6 b9 6a a3 b1 8c 24 d0
0070   0f f7 d4 1a e2 91 29 df be 90 f0 42 24 32 28 f0
0080   16 7b 4b 20 e4 c8 d3 28 ee c9 8f fe 50 c0 24 08
0090   07 c3 ba 29 d1 c9 76 e8 6e 54 93 d7 a7 fb 92 2d
00a0   8b 01 52 f9 b8 7a 43 f1 76 c1 50 34 97 c8 31 f4
00b0   ff 22 c4 64 fe 3f 5f 87 d4 cc 9b bf 22 10 09 61
00c0   d1 84 81 13 5a 5c a8 da 7d b0 60 e8 92 29 e2 06
00d0   44 6d 4f 10 67 7d bf 7c 5f 95 45 09 05 16 2b 00
00e0   6c 95 dc 7a c8 31 33 04 db cf 1c 12 db 89 4f 0c
00f0   07 f8 64 20 28 bc 70 df 00 6c d2 21 ff 19 ce 74
0100   04 72 76 9c c5 fe f2 7c 64 b1 ac 58 c3 00 0e f4
0110   9b 91 3d 98 01 e2 1a 80 64 2e dc 6e b7 f0 04 a0
0120   01 0f 55 0b 09 82 11 9c 92 8d 51 47 a4 76 8f e9
0130   d4 61 da 4e e4 21 4a cf eb 24 98 4b a6 92 9f bf
0140   7a c4 f1 27 c6 d7 d3 fe 7c c0 32 3c 9c 0f 7d d6
0150   ac c0 c3 cc 34 95 bd 3d 5f fd ad 87 25 a7 ad b3
0160   28 c0 4a 3a e5 8c 00 45 25 b2 dc 72 36 6b 20 7d
0170   ae 96 24 2c 2d c7 fa f6 69 bd ce 86 61 81 64 be
0180   81 81 e9 3e 1b 95 1e 34 67 21 54 ac e0 4e 77 a0
0190   1d 23 7e 67 d8 5e fe f8 37 ff a4 35 b3 a4 cf 0e
01a0   a1 e0 13 b8 8a d5 49 7e 59 1f fd 9d 18 4d 52 01
01b0   fd f7 17 cb 92 40 f2 b1 4d 80 f5 fa 57 43 e3 fd
01c0   7e c9 f9 07 2f a9 0f 7d 0c cf f2 0f a7 a6 0f ff
01d0   ec 94 83 0c 10 0a 17 6f e3 54 f5 04 62 81 58 91
01e0   4e dc 64 a9 fe 0f b6 99 82 c1 7b 13 f8 e4 f6 54
01f0   6a 22 04 e7 5f 4e b8 0c 7f 35 ab 4c e9 e2 cd 0b
0200   7c 3b 74 48 1a ca b4 bb 87 b9 a0 2e 36 4b ce a8
0210   f6 ca 29 c5 47 f6 81 41 34 3e 97 36 c7 ae ea ac
0220   66 b3 7f fe 38 50 b0 1e 85 34 f5 5c 6e 26 1e 85
0230   7e cc 45 09 87 a7 4b b2 a1 29 0c b0 bd 2e 4d ff
0240   ff 25 47 c2 3a 07 50 71 cc 3d 7f fc 93 7f ff c9
0250   50 73 0c e6 0c bf f4 3a 20 31 c1 58 9f f6 06 00
0260   cb 35 57 7b dc ea 0d 00 6a 54 f8 c1 dc b4 86 79
0270   d8 5e b3 7e 38 c4 3d 14 6d 33 b1 9a 57 73 85 40
0280   36 92 f3 8d 71 57 df e2 cc 4e 3d 45 a8 1e 60 24
0290   c2 84 2f 10 b6 fd a5 e8 48 30 a6 6e c7 1c 19 c9
02a0   8e 67 70 0d a0 07 91 98 cd f3 22 b5 8b d4 6b 42
02b0   c0 84 1d 02 91 0a 03 7d f4 31 ac c3 9e db ea 18
02c0   d3 4e 60 7a e2 a1 1c 71 3e cb b5 2a ed 19 e9 20
02d0   3b ed 7d de 61 ff 8e 0a f1 9c f4 96 9b 54 b9 bf
02e0   f0 01 08 5f 98 8e e8 1d 26 bd e1 8c 34 31 1f fd
02f0   2e 95 61 8c d4 e9 ff 6b eb 0c 60 76 bd d3 d3 ff
0300   d6 84 03 72 8b 05 c5 23 9e 9c bf 20 af 1f 36 dd
0310   ed 76 a0 75 20 27 25 58 88 a1 9c f5 88 42 d0 f0
0320   22 30 2b 82 56 7d 73 a3 0c b4 27 82 03 da 3e d1
0330   46 82 18 55 b4 4c 0a 3e c9 8a ee b2 6f f5 1b 85
0340   6c c7 fc f6 a9 97 11 71 49 a4 df e4 f2 c1 01 48
0350   26 25 f0 b2 c8 b7 41 9a 41 5c 98 2d 0f f1 2c 43
0360   0d 54 e6 4e 6b fa 0f 21 30 af 53 e6 12 b4 5a 34
0370   82 a7 34 73 3f d1 60 92 3e 4a 32 e3 2c 78 51 68
0380   f2 67 48 7e 0b f3 11 29 05 b8 ab a9 cc 9c cf f5
0390   1f 82 5b 06 07 df 9b 91 72 e3 9c 59 80 a3 e3 fd
03a0   84 a1 6e 98 a8 82 56 76 29 b9 67 21 bf 3f 8d f0
03b0   9c

offset in file: 52100: 09 dc 67 39 cd 61 f8 8e 21 0c 14 55 da 89 61 a1 58 a9 06 20 3b f2 0f 67 0b 16 a5 e9 36 38 bc ed 77 71 70 1d b1 d9 2e 4b 7a 9d 09 5d c3 38 ad a7 93 96 2b ef 93 09 12 5d 06 86 1e e2 24 18 e4 d8 38 0f 4f 7e ba 57 3b 0b 9b 0d c0 83 b6 4f 36 e8 79 eb 54 f1 ae 55 17 f6 37 9f df 7f 53 9c 3e f7 ea 3c 5a 4c

42	8.665314043	192.168.1.106	192.168.1.4	RTP	479	PT=DynamicRTP-Type-96, SSRC=0xDE26B36, Seq=9219, Time=178890910, Mark
0000   5c 0a 5b 53 fe eb 00 1e 65 a4 9c a8 08 00 45 00
0010   01 d1 41 00 40 00 40 11 74 5d c0 a8 01 6a c0 a8
0020   01 04 b3 bb ea 58 01 bd bf 39 80 e0 24 03 0a a9
0030   a8 9e 0d e2 6b 36 P-> 00 10 0d 28 21 0c 14 55 da 89
0040   61 a1 58 a9 06 20 3b f2 0f 67 0b 16 a5 e9 36 38  pos in file 52114
0050   bc ed 77 71 70 1d b1 d9 2e 4b 7a 9d 09 5d c3 38
0060   ad a7 93 96 2b ef 93 09 12 5d 06 86 1e e2 24 18
0070   e4 d8 38 0f 4f 7e ba 57 3b 0b 9b 0d c0 83 b6 4f
0080   36 e8 79 eb 54 f1 ae 55 17 f6 37 9f df 7f 53 9c
0090   3e f7 ea 3c 5a 4c 22 b5 85 f1 af f2 65 75 dc 69
00a0   60 df 80 b1 cb 22 c4 62 bd 47 98 2c 3e c9 f0 67
00b0   66 7d 98 76 f5 eb dc 77 ae 58 bc 79 6d a3 c8 27
00c0   32 1c 8f a2 63 99 6c 30 b6 0a 53 b7 b3 07 42 51
00d0   be 76 cf 5d 23 9b ec 69 b6 9b de fb ad bc 2a da
00e0   a9 fd ed b7 41 4b eb c8 61 41 0e f8 06 19 8c d5
00f0   bb 04 f6 7c 8e e9 d0 f4 bf c8 9b bf ef 45 ab fd
0100   6f 59 4f c1 b9 2e 6f 52 7a 51 b2 68 f4 a9 06 92
0110   36 73 52 33 d4 27 3d 11 81 50 c4 5e 86 58 54 3b
0120   a9 5b ac 3c 38 65 ce 53 0a 59 48 e7 2a 19 2a 6d
0130   b8 7e bc cf a2 66 da f1 a9 d4 d7 b9 35 3e 59 b6
0140   6e a1 2f d1 5a da 6c a4 e1 ee 5d db 52 1a b1 62
0150   bd f0 c5 f9 56 a5 62 bc fd f7 bb aa d0 06 c9 e4
0160   b5 1d 7a e0 d6 3b 10 f7 37 2c b3 31 e3 cf be 8d
0170   dd 7a fd 86 d7 79 61 1e fd 3a c9 f7 53 bc 5d c7
0180   d4 72 66 2b fb a5 1f a8 69 cd f9 ed b2 b9 b1 e9
0190   14 b8 fe 41 a8 a5 9f b7 5c 38 3e b4 4a 6f a6 6d
01a0   f9 35 9a d7 28 0c ca 33 38 e0 11 d0 cc e1 1a 1b
01b0   10 47 41 08 d2 71 81 cc eb 88 eb 87 80 0b 2a 57
01c0   69 96 85 1c f2 a2 04 cd 82 bd 51 e0 16 49 00 21
01d0   8f cf ed 7d 3f 71 2e 45 79 01 20 c8 1f 88 e0

offset in file: 52500: 85 1c f2 a2 04 cd 82 bd 51 e0 16 49 00 21 8f cf ed 7d 3f 71 2e 45 79 01 20 c8 1f 88 e0 **** Seq=9220<- ****CIACH W PAKIECIE**** ->Seq=9220 **** 21 0c 14 5d ea 85 61 a3 59 20 22 10 3b 60 f2 e8 c5 0b 59 86 fa d6 6d 12 e5 c4 04 e0 56 24 06 75 69 2a 6c 23 71 11 b7 3a 92 87 64 96 16 85 8c 69 38 a4 85 27 21 13 4d 77 2d a0 52 50 e1 4a c1 d8 1c 8b 60 52 7c 8d cc
43	8.688503092	192.168.1.106	192.168.1.4	RTP	527	PT=DynamicRTP-Type-96, SSRC=0xDE26B36, Seq=9220, Time=178891934, Mark
0000   5c 0a 5b 53 fe eb 00 1e 65 a4 9c a8 08 00 45 00
0010   02 01 41 03 40 00 40 11 74 2a c0 a8 01 6a c0 a8
0020   01 04 b3 bb ea 58 01 ed 69 f7 80 e0 24 04 0a a9
0030   ac 9e 0d e2 6b 36 P-> 00 10 0e a8 21 0c 14 5d ea 85
0040   61 a3 59 20 22 10 3b 60 f2 e8 c5 0b 59 86 fa d6  pos in file 52535
0050   6d 12 e5 c4 04 e0 56 24 06 75 69 2a 6c 23 71 11
0060   b7 3a 92 87 64 96 16 85 8c 69 38 a4 85 27 21 13
0070   4d 77 2d a0 52 50 e1 4a c1 d8 1c 8b 60 52 7c 8d
0080   cc f4 08 ba c7 ba 2e 7e 91 c9 81 fe 87 11 a8 01
0090   f8 d9 8e 92 f1 8e a6 5a 7c 7c cd 32 ee cb 5e be
00a0   df f7 1c 36 e3 b6 3e 6d d3 3c e2 8c 74 9f 5c 5c
00b0   ba eb 62 7f 1a e9 1d 40 4d f5 f8 ab 2f 30 cb 4e
00c0   d5 b9 00 38 3a 65 f0 f2 76 00 3d 6f d9 1c e1 cd
00d0   dd 3b 5c 8a cf 0e 3f 45 40 1d 33 f9 fd dd f3 16
00e0   54 e1 da e8 9e f0 ae 4f a8 40 93 d3 bc e3 f8 be
00f0   bf 2f 93 c0 1d 98 bb 17 6f 9b 88 67 1e d7 91 64
0100   17 f4 65 dc 93 1d f5 b0 7e 56 0b bb c6 d6 7b b6
0110   b5 21 d7 df 8d 5a 29 d9 e3 17 2a d5 bc a7 5a f4
0120   74 d0 38 6a c4 9f 95 eb 52 07 6a 52 72 3c 95 c8
0130   ed ad c5 af 3c ee 48 fc 04 d9 c6 9c 63 b0 33 c1
0140   eb f1 f6 7b 5a 9c 9f f7 ca f3 cd e3 79 e0 18 e9
0150   e7 2d f0 fa 06 9f d5 eb fa 8f 79 e5 7d ab cf 27
0160   45 e3 19 ba f8 7e 73 c3 f3 bf b5 f5 70 6f b6 5a
0170   96 e3 9a bd 9b e2 a9 f0 15 3d 5f f4 e6 76 3c 2d
0180   8e b1 1d c9 71 ce d2 fc eb 1f 84 5f ab 6e 9f d0
0190   17 43 c7 ee 5f f8 4e 77 cd b2 76 02 64 6a 9d ff
01a0   ad ed fa e5 d3 ef 18 aa f1 87 99 fb 83 df fc 1f
01b0   39 c6 13 54 71 77 26 19 4a 75 cb 99 c7 78 2a b5
01c0   62 bf 27 e7 99 a3 43 97 54 c4 ee 2e 6c 87 cd f4
01d0   db a9 cf ce fd 21 64 ad 74 86 54 0e 98 75 a2 68
01e0   7f 1b 36 68 fc f5 f0 99 b3 5d f3 5c 0a 34 4c c0
01f0   ec ed ce 21 10 90 6f b7 dd 2f 8b d9 d2 62 f4 9f
0200   93 b8 e0 6d 30 62 a9 7d 6b ce 01 46 70 fc 47

*/