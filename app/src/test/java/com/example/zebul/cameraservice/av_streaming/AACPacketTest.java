package com.example.zebul.cameraservice.av_streaming;

import com.example.zebul.cameraservice.av_streaming.rtp.header.RTPHeader;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AACPacketTest {

    /*
    audio rtp samples

    No      time
    51689	*REF*		192.168.1.106	192.168.1.22	RTP	425	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53722, Time=39580386, Mark
    51690	0.023279601	192.168.1.106	192.168.1.22	RTP	401	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53723, Time=39581410, Mark
    51691	0.046517996	192.168.1.106	192.168.1.22	RTP	433	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53724, Time=39582434, Mark
    51692	0.069636827	192.168.1.106	192.168.1.22	RTP	428	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53725, Time=39583458, Mark
    51693	0.092865585	192.168.1.106	192.168.1.22	RTP	428	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53726, Time=39584482, Mark
    51694	0.116065988	192.168.1.106	192.168.1.22	RTP	444	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53727, Time=39585506, Mark
    51713	0.557258321	192.168.1.106	192.168.1.22	RTP	559	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53746, Time=39604962, Mark
    51714	0.580504888	192.168.1.106	192.168.1.22	RTP	549	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53747, Time=39605986, Mark

    51736	1.021674314	192.168.1.106	192.168.1.22	RTP	466	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53766, Time=39625442, Mark
    51737	1.044861098	192.168.1.106	192.168.1.22	RTP	512	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53767, Time=39626466, Mark
    51738	1.068068066	192.168.1.106	192.168.1.22	RTP	433	PT=DynamicRTP-Type-96, SSRC=0x859F1D00, Seq=53768, Time=39627490, Mark

    51738 <-> 51737
    39627490-39626466 = 1024
    1.068068066-1.044861098 = 0.0232


    51693 <-> 51692
    39584482-39583458 = 1024
    0.092865585-0.069636827 = 0.0232
    */

    @Test
    public void testSerizalizingAAC() throws Exception {

        /**
        Response: RTSP/1.0 200 OK\r\n
        Status: 200
        Server: VLC/2.2.2\r\n
        Date: Wed, 14 Dec 2016 17:06:04 GMT\r\n
        Content-type: application/sdp
        Content-Base: rtsp://192.168.1.106:8554/s1\r\n
        Content-length: 691
        Cache-Control: no-cache\r\n
        Cseq: 3\r\n
        \r\n
        Session Description Protocol
        Session Description Protocol Version (v): 0
        Owner/Creator, Session Id (o): - 15851538726191655449 15851538726191655449 IN IP4 zebul-NV78
        Session Name (s): Unnamed
        Session Information (i): N/A
        Connection Information (c): IN IP4 0.0.0.0
        Time Description, active time (t): 0 0
        Session Attribute (a): tool:vlc 2.2.2
        Session Attribute (a): recvonly
        Session Attribute (a): type:broadcast
        Session Attribute (a): charset:UTF-8
        Session Attribute (a): control:rtsp://192.168.1.106:8554/s1
        Media Description, name and address (m): audio 0 RTP/AVP 96
        Bandwidth Information (b): RR:0
        Media Attribute (a): rtpmap:96 mpeg4-generic/44100/2
        Media Attribute (a): fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;
        Media Attribute (a): control:rtsp://192.168.1.106:8554/s1/trackID=0
        Media Description, name and address (m): video 0 RTP/AVP 96
        Bandwidth Information (b): RR:0
        Media Attribute (a): rtpmap:96 H264/90000
        Media Attribute (a): fmtp:96 packetization-mode=1;profile-level-id=42e00d;sprop-parameter-sets=J0LgDakYKD9gDUGAQa23oC8B6XvfAQ==,KM4JiA==;
        Media Attribute (a): control:rtsp://192.168.1.106:8554/s1/trackID=1


        Request: SETUP rtsp://192.168.1.106:8554/s1/trackID=0 RTSP/1.0\r\n
        Method: SETUP
        URL: rtsp://192.168.1.106:8554/s1/trackID=0
        CSeq: 4\r\n
        User-Agent: LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)\r\n
        Transport: RTP/AVP;unicast;client_port=37838-37839
        \r\n


        Frame 1157: 419 bytes on wire (3352 bits), 419 bytes captured (3352 bits) on interface 0
        Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SonyMobi_17:2c:31 (c4:3a:be:17:2c:31)
        Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.21
        User Datagram Protocol, Src Port: 50512 (50512), Dst Port: 37838 (37838)
        Real-Time Transport Protocol
        [Stream setup by RTSP (frame 1124)]
        10.. .... = Version: RFC 1889 Version (2)
        ..0. .... = Padding: False
        ...0 .... = Extension: False
        .... 0000 = Contributing source identifiers count: 0
        1... .... = Marker: True
        Payload type: DynamicRTP-Type-96 (96)
        Sequence number: 55487
        [Extended sequence number: 55487]
        Timestamp: 134654475
        Synchronization Source identifier: 0x407e7ea5 (1082031781)
        Payload: 00100b48210c145dde8b61a2b8602a1018840ea1d983ae3d...

        0000   80 e0 d8 bf 08 06 aa 0b 40 7e 7e a5 00 10 0b 48
        0010   21 0c 14 5d de 8b 61 a2 b8 60 2a 10 18 84 0e a1
        0020   d9 83 ae 3d 94 eb 9d d3 ae ad ab de eb 9e 38 be
        0030   43 f7 db be a2 8a 47 01 c2 6a 42 9e 27 02 2c bb
        0040   3b 87 e4 07 91 48 a3 ce e3 26 59 7f b5 ee 09 45
        0050   84 69 8f 68 eb c2 14 a5 56 40 eb 22 08 be 4e 49
        0060   67 97 2e 87 b2 2b fc 0c 31 56 1f 2a bb 41 6c db
        0070   1d bb c0 24 7e 33 fb 2d b1 ee 9f fe f3 0e 15 9f
        0080   64 47 7d 1b 16 69 39 14 9a 48 da df 31 eb f7 33
        0090   c4 32 29 cf 3f be bc ba 4f dd b1 69 1e 14 7a 8c
        00a0   cb 17 ee 03 04 e7 9c fb 96 a3 fb 89 cd b8 ec 27
        00b0   55 26 f0 bb d0 72 42 3d d7 0c 49 9b 3f 19 96 60
        00c0   e9 ab 34 70 53 8a cb 54 5c 8e 54 f6 f7 ae 75 cd
        00d0   46 47 a3 e5 5e 95 98 de bb bd af 5c 32 76 d6 6a
        00e0   71 a4 9e 4c 4c 01 ef db 4d 1d 7b 8c 5c 64 99 c6
        00f0   f7 c2 8e 6b 87 b6 b8 66 5d 39 e1 28 ff 24 f4 4b
        0100   9e 71 fc bf d0 75 33 7f e4 a2 d2 0d d6 be 03 37
        0110   f2 6f 14 67 c5 53 7e c5 72 59 96 7d ca 0a 59 8c
        0120   74 62 b1 5e 85 e7 c4 9b 65 da 28 df b3 cb ea 5a
        0130   8a e6 fb 2e 26 5a aa a3 f3 f3 4f f5 bb c6 f0 f9
        0140   8c 7b 06 a5 89 bc e2 5e f9 5e 15 bc 48 be c4 66
        0150   08 cb f8 66 3f 43 5b d0 78 1c 48 00 1c e8 23 c8
        0160   ae 14 68 35 bd 14 ee 73 2e b3 7e bf 66 7b 1f 78
        0170   1f b3 f2 17 01 b0 3f 11 c0

        */
        byte []rtpData1 =
        /*0000*/{(byte)0x80, (byte)0xE0, (byte)0xD8, (byte)0xBF, (byte)0x08, (byte)0x06, (byte)0xAA, (byte)0x0B
                ,(byte)0x40, (byte)0x7E, (byte)0x7E, (byte)0xA5, (byte)0x00, (byte)0x10, (byte)0x0B, (byte)0x48

        /*0010*/,(byte)0x21, (byte)0x0C, (byte)0x14, (byte)0x5D, (byte)0xDE, (byte)0x8B, (byte)0x61, (byte)0xA2
                ,(byte)0xB8, (byte)0x60, (byte)0x2A, (byte)0x10, (byte)0x18, (byte)0x84, (byte)0x0E, (byte)0xA1};
        RTPHeader rtpHeader1 = new RTPHeader();
        rtpHeader1.fromBytes(rtpData1);

        assertEquals(134654475, rtpHeader1.getTimestamp());
        assertEquals(55487, rtpHeader1.getSequenceNumber());

        int rtpLen1 = (16*0x16)+9;

        int len1 = (0x1FE0&(rtpData1[14]<<5));
        len1 |= (0x001F&(rtpData1[15]>>3));

        /*
        data[0] = 0;
        data[1] = 0x10;
        */
        /*
        // AU-size
        data[2] = (byte) (mBufferInfo.size>>5);
        data[3] = (byte) (mBufferInfo.size<<3);

        // AU-Index
        data[3] &= 0xF8;
        data[3] |= 0x00;
        */
        /*
        data[2] = (byte) (((int) mBufferInfo.size & 0x1FE0) >> 5);
        data[3] = (byte) (((int) mBufferInfo.size & 0x001F) << 3);
        */
        //419 bytes on wire
        //RTPHeader.LENGTH


        /*
        Frame 1174: 454 bytes on wire (3632 bits), 454 bytes captured (3632 bits) on interface 0
        Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SonyMobi_17:2c:31 (c4:3a:be:17:2c:31)
        Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.21
        User Datagram Protocol, Src Port: 50512 (50512), Dst Port: 37838 (37838)
        Real-Time Transport Protocol
        [Stream setup by RTSP (frame 1124)]
        10.. .... = Version: RFC 1889 Version (2)
        ..0. .... = Padding: False
        ...0 .... = Extension: False
        .... 0000 = Contributing source identifiers count: 0
        1... .... = Marker: True
        Payload type: DynamicRTP-Type-96 (96)
        Sequence number: 55504
        [Extended sequence number: 55504]
        Timestamp: 134671883
        Synchronization Source identifier: 0x407e7ea5 (1082031781)
        Payload: 00 10 0c 60210c144dea94a11201db03b0d75c1d50be2af515...


        0000   c4 3a be 17 2c 31 00 1e 65 a4 9c a8 08 00 45 00
        0010   01 b8 fe 6d 40 00 40 11 b6 f7 c0 a8 01 6a c0 a8
        0020   01 15 c5 50 93 ce 01 a4 32 8e
                                             80 e0 d8 d0 08 06
        0030   ee 0b 40 7e 7e a5 00 10 0c 60 21 0c 14 4d ea 94
        0040   a1 12 01 db 03 b0 d7 5c 1d 50 be 2a f5 15 b1 78
        0050   0b 79 8f 03 dc 00 00 a4 7b 40 92 86 f4 9b 49 3e
        0060   08 8d a3 78 3f 0e a9 e1 ca c4 94 4f 2d 0a b9 01
        0070   36 2c 8c 03 d4 72 09 0d 17 71 c9 a8 e4 01 06 5e
        0080   80 4e 3a 48 c2 2e 0e c2 47 79 18 14 48 a2 ce 56
        0090   85 51 97 21 ab 82 79 9c bc cb 14 5f 73 dc de 37
        00a0   bc fb 33 67 5b cc a3 f9 72 89 2f 1b 7f 43 2e e4
        00b0   c1 54 43 ac 81 78 4c 32 31 cc e5 a4 78 cf 45 7d
        00c0   cd e7 ca 5f 5f c0 eb 2a 63 64 cb c4 cd f5 09 db
        00d0   be b9 a4 1c 3b ca be 6e 68 ae 6c fa f7 85 8f 7a
        00e0   67 cc 77 ef da 74 42 a4 dd 45 07 4e ab 77 fe 72
        00f0   d0 20 94 6b 14 c7 65 40 eb d6 dd cb 04 e6 c4 d7
        0100   b2 5c cd da 4f 85 6b 0a ac 81 ea b9 0e 39 44 7f
        0110   8f ed 0c 1b e2 f7 7b 32 d7 d0 26 07 8d be 9a 97
        0120   80 69 85 a7 ab 48 29 c4 1f 62 b2 b9 78 22 28 f6
        0130   77 30 f2 4f 94 98 1d 28 37 ca e5 84 78 37 cb 03
        0140   24 af 7a 0d 58 95 28 cb b8 3e 2f a8 cd 37 53 bd
        0150   bb f6 34 58 71 96 df 25 fb 46 58 06 96 fe b5 b1
        0160   b3 f4 46 0f 31 95 fd b9 38 03 6c 1f b8 84 f6 b5
        0170   cf 0b ed eb d2 66 ed 97 3e 16 a2 9d ae 32 fd e1
        0180   e3 c3 df b6 d6 18 0e c3 3e f9 9b 68 4e 39 dd 9d
        0190   ef 00 92 67 06 10 36 72 d0 ab 28 ba ae e8 d1 08
        01a0   c0 2b e2 3c e0 34 f3 67 2a 1e a9 f8 f2 be 88 b8
        01b0   98 11 76 2e 51 70 49 d6 21 22 c4 03 48 7e 2b 79
        01c0   8f 03 dc 00 00 b8
        */

        byte []rtpData2 =
                {(byte)0x80, (byte)0xE0, (byte)0xD8, (byte)0xD0, (byte)0x08, (byte)0x06, (byte)0xEE
                ,(byte)0x0B, (byte)0x40, (byte)0x7E, (byte)0x7E, (byte)0xA5, (byte)0x00, (byte)0x10
                ,(byte)0x0C, (byte)0x60, (byte)0x21, (byte)0x0C, (byte)0x14, (byte)0x4D, (byte)0xEA};

        RTPHeader rtpHeader2 = new RTPHeader();
        rtpHeader2.fromBytes(rtpData2);

        assertEquals(134671883, rtpHeader2.getTimestamp());
        assertEquals(55504, rtpHeader2.getSequenceNumber());

        int rtpLen2 = ((16*(0x1C))+6)-((16*2)+10);

        int len2 = (0x1FE0&(rtpData2[14]<<5));
        len2 |= (0x001F&(rtpData2[15]>>3));

        /*
        Frame 121: 561 bytes on wire (4488 bits), 561 bytes captured (4488 bits) on interface 0
        Interface id: 0 (wlp4s0)
        Encapsulation type: Ethernet (1)
        Arrival Time: Dec 15, 2016 18:55:26.792786043 CET
        [Time shift for this packet: 0.000000000 seconds]
        Epoch Time: 1481824526.792786043 seconds
        [Time delta from previous captured frame: 0.023235761 seconds]
        [Time delta from previous displayed frame: 0.023235761 seconds]
        [Time since reference or first frame: 18.556815740 seconds]
        Frame Number: 121
        Frame Length: 561 bytes (4488 bits)
        Capture Length: 561 bytes (4488 bits)
        [Frame is marked: False]
        [Frame is ignored: False]
        [Protocols in frame: eth:ethertype:ip:udp:rtp]
        [Coloring Rule Name: UDP]
        [Coloring Rule String: udp]
        Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SonyMobi_17:2c:31 (c4:3a:be:17:2c:31)
        Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.21
        User Datagram Protocol, Src Port: 35323 (35323), Dst Port: 48844 (48844)
        Real-Time Transport Protocol
        [Stream setup by RTSP (frame 18)]
        10.. .... = Version: RFC 1889 Version (2)
        ..0. .... = Padding: False
        ...0 .... = Extension: False
        .... 0000 = Contributing source identifiers count: 0
        1... .... = Marker: True
        Payload type: DynamicRTP-Type-96 (96)
        Sequence number: 59056
        [Extended sequence number: 59056]
        Timestamp: 408230870
        Synchronization Source identifier: 0x35e3a341 (904110913)
        Payload: 00100fb8210c143dfc54604817d18d0d76a0b22524c05bc3...

        0000   c4 3a be 17 2c 31 00 1e 65 a4 9c a8 08 00 45 00
        0010   02 23 fb 35 40 00 40 11 b9 c4 c0 a8 01 6a c0 a8
        0020   01 15 89 fb be cc 02 0f 82 51
                                             80 e0 e6 b0 18 55
        0030   1b d6 35 e3 a3 41 00 10 0f b8 21 0c 14 3d fc 54
        0040   60 48 17 d1 8d 0d 76 a0 b2 25 24 c0 5b c3 69 e2
        0050   1c db c4 c4 3c 3b c9 08 66 20 90 2a 08 28 84 98
        0060   ea bf f8 dc 21 7f 05 ff ca 04 3f f9 56 ea 25 05
        0070   24 26 a8 99 8c 41 4e 20 0b 64 2a a8 98 62 13 9d
        0080   78 9e 0f 06 4f 00 3c 80 92 17 72 78 06 14 9b ce
        0090   4d b3 09 e0 af 93 a1 28 9c 19 24 a9 96 c4 9b a6
        00a0   f3 bd 0c 0a c1 09 32 09 82 89 27 20 89 c9 45 c9
        00b0   a1 01 c5 64 28 62 c8 c1 80 4c f2 49 e1 e3 90 9b
        00c0   77 00 8f 5a 46 21 39 44 c7 0e 5f 39 03 49 21 1c
        00d0   99 de 51 38 51 c9 ce 79 35 bb 3b 07 27 c2 d1 d4
        00e0   59 88 41 9b 80 a2 a3 3d 4e 4b 78 64 10 1f e2 73
        00f0   05 4e 02 41 05 72 eb cf 33 77 0f 1f 11 18 ae f0
        0100   d9 e3 f4 eb 50 29 77 2d 64 17 de 00 1a 57 8c fe
        0110   cd 74 83 0e cc 73 8b ce 84 14 0f 7d d7 03 b5 01
        0120   67 86 0f 9e bc 97 57 ee 7b cb 6f 5a 20 cf c9 37
        0130   bd 51 97 33 f6 3c 2d b7 cc bd d5 f7 ce b2 70 fc
        0140   c2 14 5c bd c1 5b 1c 6d 85 fa 83 f7 7a 7f aa c9
        0150   74 23 ef c6 26 de d9 85 c7 ea ba 5e 32 8f 37 7f
        0160   13 ef c9 b7 85 07 1b e8 ef 57 c4 2c 8f af ec cd
        0170   45 54 4d fc c1 fd 8b 07 ba e4 5f a9 d1 23 e7 6a
        0180   e4 3d e7 cb 3f 35 24 64 c0 e8 9d 1b c1 33 16 6c
        0190   cf f1 09 e9 ab e2 25 40 c4 aa db c7 de 34 75 1b
        01a0   fe 30 77 43 72 f5 7f df f4 75 19 98 f8 bd fb 3e
        01b0   0d 81 f9 71 b9 b1 6a 7b e9 94 cc 0b d2 e3 6f a8
        01c0   dc 10 46 d9 cf 58 58 df 63 8d ad 71 14 86 6d f6
        01d0   88 dd bb fd 3d 57 0b fc bd ff 0f df 1c ef 7e 38
        01e0   9a 76 3a 58 3a 0f bb ef 78 3e 92 6b 5e bc 56 1d
        01f0   52 ee 86 3d 8f b1 e6 11 83 e2 f4 8c 85 bb 49 c6
        0200   97 52 f4 4d f4 79 fb b9 80 99 9b af b2 f5 af d1
        0210   7e 8a f6 bd 08 2b 40 c8 ac 34 38 24 1a e7 ab cc
        0220   52 08 0b b6 4b 66 38 88 f8 a2 6e 0f c5 6f 0d a7
        0230   b8
        */

        byte []rtpData3 =
                {(byte)0x80, (byte)0xE0, (byte)0xE6, (byte)0xB0, (byte)0x18, (byte)0x55, (byte)0x1B, (byte)0xD6
                ,(byte)0x35, (byte)0xE3, (byte)0xA3, (byte)0x41, (byte)0x00, (byte)0x10, (byte)0x0F, (byte)0xB8
                ,(byte)0x21, (byte)0x0C, (byte)0x14, (byte)0x3D, (byte)0xFC, (byte)0x54};

        RTPHeader rtpHeader3 = new RTPHeader();
        rtpHeader3.fromBytes(rtpData3);

        assertEquals(408230870, rtpHeader3.getTimestamp());
        assertEquals(59056, rtpHeader3.getSequenceNumber());

        int rtpLen3 = ((16*(0x23))+1)-((16*2)+10);

        int len3 = (0x1FE0&(rtpData3[14]<<5));
        len3 |= (0x001F&(rtpData3[15]>>3));


        /*
        Frame 875: 527 bytes on wire (4216 bits), 527 bytes captured (4216 bits) on interface 0
        Ethernet II, Src: SonyMobi_17:2c:31 (c4:3a:be:17:2c:31), Dst: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8)
        Internet Protocol Version 4, Src: 192.168.1.21, Dst: 192.168.1.106
        User Datagram Protocol, Src Port: 47360 (47360), Dst Port: 47360 (47360)
        Data (485 bytes)
            Data: 80e0002e00148f980d4096e90146342cb518233602c10948...
            [Length: 485]

        0000   80 e0 00 60 00 29 ea be 81 d9 13 e8 00 10 0f e8
        0010   01 16 34 1c 50 fa 74 1a 86 9d 37 72 b9 9b 89 32
        0020   92 29 96 b2 54 80 07 e0 c0 d5 d5 89 ca 6b 34 c5
        0030   af 04 a8 ec 5e 4e 89 27 70 8a 4d 7b 6c 79 f5 74
        0040   72 59 ba b8 ad 8f 5f 8b 12 97 89 a4 25 97 d8 d1
        0050   13 e4 56 d1 51 14 8d 0a 5f 7a d9 de 28 f0 97 d7
        0060   2b 3e 4c ac 3a 82 75 99 dc 26 66 15 11 a7 32 26
        0070   a2 bb 72 b9 30 f9 36 56 58 9f 26 90 a2 b5 ff 3f
        0080   ab 0c e3 4e 0b 8d da 54 56 f0 58 3c 4e c5 c7 5a
        0090   ce a7 32 d4 83 10 02 5f 36 12 05 d9 b9 a4 bf 56
        00a0   b6 e9 f4 e3 92 44 05 49 ec 98 64 29 7c 12 4d 79
        00b0   db ec aa e3 95 79 ce 7c fb 7d 5e fb ee ac 72 90
        00c0   c1 5c 3a 01 17 ed 2d 55 47 5f e9 b5 e5 35 9c 8f
        00d0   59 c3 3f 25 24 42 cf 4e 85 4d 48 36 b9 a6 29 f4
        00e0   b7 f1 65 52 ad 75 9d ed 30 b4 30 68 c4 34 71 a3
        00f0   52 77 d2 4f cc 93 5c ac dc 11 b2 52 c6 5f 1e 44
        0100   23 05 8f 37 4c e6 31 70 e3 92 df 32 10 11 cc ac
        0110   58 a5 61 8c 6a 43 9d 3e 80 b6 25 b1 1e e6 5e a7
        0120   25 23 65 4f 6f c9 b5 68 95 4e cd 32 37 d9 7f 73
        0130   53 d9 4e ec 47 c3 c7 e5 e9 6e 6c c8 a5 8d 55 77
        0140   0a 24 a7 21 bc 2b d0 ef d6 da 71 63 db ea f5 3a
        0150   5d 06 87 9a c6 0f 1d 0f 1e 54 76 b6 0b 95 8d 65
        0160   85 ba 51 d9 22 2e 09 0c fa 35 98 bd e4 be 5f db
        0170   5d 11 31 33 b6 65 d7 5d 95 78 84 ff 97 17 7f 63
        0180   8a 8e 42 4b 08 c9 e5 47 ad 6f b1 b3 d5 d9 df 23
        0190   b4 d6 7f cd 66 86 8d 38 f5 7b f4 55 a8 e5 7a d4
        01a0   49 81 4c d2 75 61 b2 42 d3 78 46 49 ae ad aa d8
        01b0   6d af 61 e1 28 2b 0c 45 91 2f 06 65 2c d4 ac 16
        01c0   a8 64 a9 23 a6 6d ad 72 d8 bb 9d c6 97 73 af e1
        01d0   dd 82 d7 5d a8 f6 9b 4c 0e 7b 22 4e 91 a3 8c af
        01e0   5b 05 4e cc a2 d3 2d e7 99 69 fe 87 5d 4d 95 14
        01f0   b5 17 22 cd 76 29 c7 02 89 c8 84 f2 64 f4 4d 6c
        0200   10 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 a5 e0
        */

        byte []rtpData4 =
                {(byte)0x80, (byte)0xE0, (byte)0x00, (byte)0x60, (byte)0x00, (byte)0x29, (byte)0xEA, (byte)0xBE
                ,(byte)0x81, (byte)0xD9, (byte)0x13, (byte)0xE8, (byte)0x00, (byte)0x10, (byte)0x0F, (byte)0xE8};

        RTPHeader rtpHeader4 = new RTPHeader();
        rtpHeader4.fromBytes(rtpData4);

        int rtpLen4 = (16*(0x20))+13;

        int len4 = (0x1FE0&(rtpData4[14]<<5));
        len4 |= (0x001F&(rtpData4[15]>>3));

        int foo = 1;
        int bar = foo;
    }
}


