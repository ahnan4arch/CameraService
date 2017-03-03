package com.example.zebul.cameraservice.av_protocols.rtp.aac;

import com.example.zebul.cameraservice.av_protocols.rtp.RTPHeader;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AACDepacketizerTest {

    /*
    audio rtp samples

    No      time
    51689	*REF*		192.168.1.106	192.168.1.22	RTP	425	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53722, Time=39580386, Mark
    51690	0.023279601	192.168.1.106	192.168.1.22	RTP	401	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53723, Time=39581410, Mark
    51691	0.046517996	192.168.1.106	192.168.1.22	RTP	433	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53724, Time=39582434, Mark
    51692	0.069636827	192.168.1.106	192.168.1.22	RTP	428	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53725, Time=39583458, Mark
    51693	0.092865585	192.168.1.106	192.168.1.22	RTP	428	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53726, Time=39584482, Mark
    51694	0.116065988	192.168.1.106	192.168.1.22	RTP	444	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53727, Time=39585506, Mark
    51713	0.557258321	192.168.1.106	192.168.1.22	RTP	559	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53746, Time=39604962, Mark
    51714	0.580504888	192.168.1.106	192.168.1.22	RTP	549	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53747, Time=39605986, Mark

    51736	1.021674314	192.168.1.106	192.168.1.22	RTP	466	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53766, Time=39625442, Mark
    51737	1.044861098	192.168.1.106	192.168.1.22	RTP	512	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53767, Time=39626466, Mark
    51738	1.068068066	192.168.1.106	192.168.1.22	RTP	433	PT=DynamicRTP-KnownName-96, SSRC=0x859F1D00, Seq=53768, Time=39627490, Mark

    51738 <-> 51737
    39627490-39626466 = 1024
    1.068068066-1.044861098 = 0.0232


    51693 <-> 51692
    39584482-39583458 = 1024
    0.092865585-0.069636827 = 0.0232
    */

    @Test
    public void test_when_packet_is_created_by_VLC_then_AAC_lenght_is_depacketizable_sample1(){

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
         SessionDescription Description Protocol
         SessionDescription Description Protocol Version (v): 0
         Owner/Creator, SessionDescription Id (o): - 15851538726191655449 15851538726191655449 IN IP4 zebul-NV78
         SessionDescription Name (s): Unnamed
         SessionDescription Information (i): N/A
         Connection Information (c): IN IP4 0.0.0.0
         Time Description, active time (t): 0 0
         SessionDescription Attribute (a): tool:vlc 2.2.2
         SessionDescription Attribute (a): recvonly
         SessionDescription Attribute (a): type:broadcast
         SessionDescription Attribute (a): charset:UTF-8
         SessionDescription Attribute (a): control:rtsp://192.168.1.106:8554/s1
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
         Payload type: DynamicRTP-KnownName-96 (96)
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
        byte [] bytesOfRTPPacket = new byte[]
        {(byte)0x80 ,(byte)0xE0 ,(byte)0xD8 ,(byte)0xBF ,(byte)0x08 ,(byte)0x06 ,(byte)0xAA ,(byte)0x0B ,(byte)0x40 ,(byte)0x7E ,(byte)0x7E ,(byte)0xA5 ,(byte)0x00 ,(byte)0x10 ,(byte)0x0B ,(byte)0x48
        ,(byte)0x21 ,(byte)0x0C ,(byte)0x14 ,(byte)0x5D ,(byte)0xDE ,(byte)0x8B ,(byte)0x61 ,(byte)0xA2 ,(byte)0xB8 ,(byte)0x60 ,(byte)0x2A ,(byte)0x10 ,(byte)0x18 ,(byte)0x84 ,(byte)0x0E ,(byte)0xA1
        ,(byte)0xD9 ,(byte)0x83 ,(byte)0xAE ,(byte)0x3D ,(byte)0x94 ,(byte)0xEB ,(byte)0x9D ,(byte)0xD3 ,(byte)0xAE ,(byte)0xAD ,(byte)0xAB ,(byte)0xDE ,(byte)0xEB ,(byte)0x9E ,(byte)0x38 ,(byte)0xBE
        ,(byte)0x43 ,(byte)0xF7 ,(byte)0xDB ,(byte)0xBE ,(byte)0xA2 ,(byte)0x8A ,(byte)0x47 ,(byte)0x01 ,(byte)0xC2 ,(byte)0x6A ,(byte)0x42 ,(byte)0x9E ,(byte)0x27 ,(byte)0x02 ,(byte)0x2C ,(byte)0xBB
        ,(byte)0x3B ,(byte)0x87 ,(byte)0xE4 ,(byte)0x07 ,(byte)0x91 ,(byte)0x48 ,(byte)0xA3 ,(byte)0xCE ,(byte)0xE3 ,(byte)0x26 ,(byte)0x59 ,(byte)0x7F ,(byte)0xB5 ,(byte)0xEE ,(byte)0x09 ,(byte)0x45
        ,(byte)0x84 ,(byte)0x69 ,(byte)0x8F ,(byte)0x68 ,(byte)0xEB ,(byte)0xC2 ,(byte)0x14 ,(byte)0xA5 ,(byte)0x56 ,(byte)0x40 ,(byte)0xEB ,(byte)0x22 ,(byte)0x08 ,(byte)0xBE ,(byte)0x4E ,(byte)0x49
        ,(byte)0x67 ,(byte)0x97 ,(byte)0x2E ,(byte)0x87 ,(byte)0xB2 ,(byte)0x2B ,(byte)0xFC ,(byte)0x0C ,(byte)0x31 ,(byte)0x56 ,(byte)0x1F ,(byte)0x2A ,(byte)0xBB ,(byte)0x41 ,(byte)0x6C ,(byte)0xDB
        ,(byte)0x1D ,(byte)0xBB ,(byte)0xC0 ,(byte)0x24 ,(byte)0x7E ,(byte)0x33 ,(byte)0xFB ,(byte)0x2D ,(byte)0xB1 ,(byte)0xEE ,(byte)0x9F ,(byte)0xFE ,(byte)0xF3 ,(byte)0x0E ,(byte)0x15 ,(byte)0x9F
        ,(byte)0x64 ,(byte)0x47 ,(byte)0x7D ,(byte)0x1B ,(byte)0x16 ,(byte)0x69 ,(byte)0x39 ,(byte)0x14 ,(byte)0x9A ,(byte)0x48 ,(byte)0xDA ,(byte)0xDF ,(byte)0x31 ,(byte)0xEB ,(byte)0xF7 ,(byte)0x33
        ,(byte)0xC4 ,(byte)0x32 ,(byte)0x29 ,(byte)0xCF ,(byte)0x3F ,(byte)0xBE ,(byte)0xBC ,(byte)0xBA ,(byte)0x4F ,(byte)0xDD ,(byte)0xB1 ,(byte)0x69 ,(byte)0x1E ,(byte)0x14 ,(byte)0x7A ,(byte)0x8C
        ,(byte)0xCB ,(byte)0x17 ,(byte)0xEE ,(byte)0x03 ,(byte)0x04 ,(byte)0xE7 ,(byte)0x9C ,(byte)0xFB ,(byte)0x96 ,(byte)0xA3 ,(byte)0xFB ,(byte)0x89 ,(byte)0xCD ,(byte)0xB8 ,(byte)0xEC ,(byte)0x27
        ,(byte)0x55 ,(byte)0x26 ,(byte)0xF0 ,(byte)0xBB ,(byte)0xD0 ,(byte)0x72 ,(byte)0x42 ,(byte)0x3D ,(byte)0xD7 ,(byte)0x0C ,(byte)0x49 ,(byte)0x9B ,(byte)0x3F ,(byte)0x19 ,(byte)0x96 ,(byte)0x60
        ,(byte)0xE9 ,(byte)0xAB ,(byte)0x34 ,(byte)0x70 ,(byte)0x53 ,(byte)0x8A ,(byte)0xCB ,(byte)0x54 ,(byte)0x5C ,(byte)0x8E ,(byte)0x54 ,(byte)0xF6 ,(byte)0xF7 ,(byte)0xAE ,(byte)0x75 ,(byte)0xCD
        ,(byte)0x46 ,(byte)0x47 ,(byte)0xA3 ,(byte)0xE5 ,(byte)0x5E ,(byte)0x95 ,(byte)0x98 ,(byte)0xDE ,(byte)0xBB ,(byte)0xBD ,(byte)0xAF ,(byte)0x5C ,(byte)0x32 ,(byte)0x76 ,(byte)0xD6 ,(byte)0x6A
        ,(byte)0x71 ,(byte)0xA4 ,(byte)0x9E ,(byte)0x4C ,(byte)0x4C ,(byte)0x01 ,(byte)0xEF ,(byte)0xDB ,(byte)0x4D ,(byte)0x1D ,(byte)0x7B ,(byte)0x8C ,(byte)0x5C ,(byte)0x64 ,(byte)0x99 ,(byte)0xC6
        ,(byte)0xF7 ,(byte)0xC2 ,(byte)0x8E ,(byte)0x6B ,(byte)0x87 ,(byte)0xB6 ,(byte)0xB8 ,(byte)0x66 ,(byte)0x5D ,(byte)0x39 ,(byte)0xE1 ,(byte)0x28 ,(byte)0xFF ,(byte)0x24 ,(byte)0xF4 ,(byte)0x4B
        ,(byte)0x9E ,(byte)0x71 ,(byte)0xFC ,(byte)0xBF ,(byte)0xD0 ,(byte)0x75 ,(byte)0x33 ,(byte)0x7F ,(byte)0xE4 ,(byte)0xA2 ,(byte)0xD2 ,(byte)0x0D ,(byte)0xD6 ,(byte)0xBE ,(byte)0x03 ,(byte)0x37
        ,(byte)0xF2 ,(byte)0x6F ,(byte)0x14 ,(byte)0x67 ,(byte)0xC5 ,(byte)0x53 ,(byte)0x7E ,(byte)0xC5 ,(byte)0x72 ,(byte)0x59 ,(byte)0x96 ,(byte)0x7D ,(byte)0xCA ,(byte)0x0A ,(byte)0x59 ,(byte)0x8C
        ,(byte)0x74 ,(byte)0x62 ,(byte)0xB1 ,(byte)0x5E ,(byte)0x85 ,(byte)0xE7 ,(byte)0xC4 ,(byte)0x9B ,(byte)0x65 ,(byte)0xDA ,(byte)0x28 ,(byte)0xDF ,(byte)0xB3 ,(byte)0xCB ,(byte)0xEA ,(byte)0x5A
        ,(byte)0x8A ,(byte)0xE6 ,(byte)0xFB ,(byte)0x2E ,(byte)0x26 ,(byte)0x5A ,(byte)0xAA ,(byte)0xA3 ,(byte)0xF3 ,(byte)0xF3 ,(byte)0x4F ,(byte)0xF5 ,(byte)0xBB ,(byte)0xC6 ,(byte)0xF0 ,(byte)0xF9
        ,(byte)0x8C ,(byte)0x7B ,(byte)0x06 ,(byte)0xA5 ,(byte)0x89 ,(byte)0xBC ,(byte)0xE2 ,(byte)0x5E ,(byte)0xF9 ,(byte)0x5E ,(byte)0x15 ,(byte)0xBC ,(byte)0x48 ,(byte)0xBE ,(byte)0xC4 ,(byte)0x66
        ,(byte)0x08 ,(byte)0xCB ,(byte)0xF8 ,(byte)0x66 ,(byte)0x3F ,(byte)0x43 ,(byte)0x5B ,(byte)0xD0 ,(byte)0x78 ,(byte)0x1C ,(byte)0x48 ,(byte)0x00 ,(byte)0x1C ,(byte)0xE8 ,(byte)0x23 ,(byte)0xC8
        ,(byte)0xAE ,(byte)0x14 ,(byte)0x68 ,(byte)0x35 ,(byte)0xBD ,(byte)0x14 ,(byte)0xEE ,(byte)0x73 ,(byte)0x2E ,(byte)0xB3 ,(byte)0x7E ,(byte)0xBF ,(byte)0x66 ,(byte)0x7B ,(byte)0x1F ,(byte)0x78
        ,(byte)0x1F ,(byte)0xB3 ,(byte)0xF2 ,(byte)0x17 ,(byte)0x01 ,(byte)0xB0 ,(byte)0x3F ,(byte)0x11 ,(byte)0xC0};

        AACDepacketizer aacDepacketizer = new AACDepacketizer();
        AACPackets aacPackets = aacDepacketizer.createAACPackets(bytesOfRTPPacket);
        assertEquals(1, aacPackets.getNumberOfPackets());

        final AACPacket aacPacket = aacPackets.getPacket(0);
        final Timestamp timestamp = aacPacket.getTimestamp();
        assertEquals(134654475, timestamp.getTimestampInMillis());

        final AccessUnit accessUnit = aacPacket.getAccessUnit();
        int expectedAccessUnitLength = bytesOfRTPPacket.length-RTPHeader.LENGTH-AACPayload.HEADER_LENGHT;
        assertEquals(expectedAccessUnitLength, accessUnit.getData().length);
    }

    @Test
    public void test_when_packet_is_created_by_VLC_then_AAC_lenght_is_depacketizable_sample2() throws Exception {
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
        Payload type: DynamicRTP-KnownName-96 (96)
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

        byte [] bytesOfRTPPacket = new byte[]
        {(byte)0x80 ,(byte)0xE0 ,(byte)0xD8 ,(byte)0xD0 ,(byte)0x08 ,(byte)0x06
        ,(byte)0xEE ,(byte)0x0B ,(byte)0x40 ,(byte)0x7E ,(byte)0x7E ,(byte)0xA5 ,(byte)0x00 ,(byte)0x10 ,(byte)0x0C ,(byte)0x60 ,(byte)0x21 ,(byte)0x0C ,(byte)0x14 ,(byte)0x4D ,(byte)0xEA ,(byte)0x94
        ,(byte)0xA1 ,(byte)0x12 ,(byte)0x01 ,(byte)0xDB ,(byte)0x03 ,(byte)0xB0 ,(byte)0xD7 ,(byte)0x5C ,(byte)0x1D ,(byte)0x50 ,(byte)0xBE ,(byte)0x2A ,(byte)0xF5 ,(byte)0x15 ,(byte)0xB1 ,(byte)0x78
        ,(byte)0x0B ,(byte)0x79 ,(byte)0x8F ,(byte)0x03 ,(byte)0xDC ,(byte)0x00 ,(byte)0x00 ,(byte)0xA4 ,(byte)0x7B ,(byte)0x40 ,(byte)0x92 ,(byte)0x86 ,(byte)0xF4 ,(byte)0x9B ,(byte)0x49 ,(byte)0x3E
        ,(byte)0x08 ,(byte)0x8D ,(byte)0xA3 ,(byte)0x78 ,(byte)0x3F ,(byte)0x0E ,(byte)0xA9 ,(byte)0xE1 ,(byte)0xCA ,(byte)0xC4 ,(byte)0x94 ,(byte)0x4F ,(byte)0x2D ,(byte)0x0A ,(byte)0xB9 ,(byte)0x01
        ,(byte)0x36 ,(byte)0x2C ,(byte)0x8C ,(byte)0x03 ,(byte)0xD4 ,(byte)0x72 ,(byte)0x09 ,(byte)0x0D ,(byte)0x17 ,(byte)0x71 ,(byte)0xC9 ,(byte)0xA8 ,(byte)0xE4 ,(byte)0x01 ,(byte)0x06 ,(byte)0x5E
        ,(byte)0x80 ,(byte)0x4E ,(byte)0x3A ,(byte)0x48 ,(byte)0xC2 ,(byte)0x2E ,(byte)0x0E ,(byte)0xC2 ,(byte)0x47 ,(byte)0x79 ,(byte)0x18 ,(byte)0x14 ,(byte)0x48 ,(byte)0xA2 ,(byte)0xCE ,(byte)0x56
        ,(byte)0x85 ,(byte)0x51 ,(byte)0x97 ,(byte)0x21 ,(byte)0xAB ,(byte)0x82 ,(byte)0x79 ,(byte)0x9C ,(byte)0xBC ,(byte)0xCB ,(byte)0x14 ,(byte)0x5F ,(byte)0x73 ,(byte)0xDC ,(byte)0xDE ,(byte)0x37
        ,(byte)0xBC ,(byte)0xFB ,(byte)0x33 ,(byte)0x67 ,(byte)0x5B ,(byte)0xCC ,(byte)0xA3 ,(byte)0xF9 ,(byte)0x72 ,(byte)0x89 ,(byte)0x2F ,(byte)0x1B ,(byte)0x7F ,(byte)0x43 ,(byte)0x2E ,(byte)0xE4
        ,(byte)0xC1 ,(byte)0x54 ,(byte)0x43 ,(byte)0xAC ,(byte)0x81 ,(byte)0x78 ,(byte)0x4C ,(byte)0x32 ,(byte)0x31 ,(byte)0xCC ,(byte)0xE5 ,(byte)0xA4 ,(byte)0x78 ,(byte)0xCF ,(byte)0x45 ,(byte)0x7D
        ,(byte)0xCD ,(byte)0xE7 ,(byte)0xCA ,(byte)0x5F ,(byte)0x5F ,(byte)0xC0 ,(byte)0xEB ,(byte)0x2A ,(byte)0x63 ,(byte)0x64 ,(byte)0xCB ,(byte)0xC4 ,(byte)0xCD ,(byte)0xF5 ,(byte)0x09 ,(byte)0xDB
        ,(byte)0xBE ,(byte)0xB9 ,(byte)0xA4 ,(byte)0x1C ,(byte)0x3B ,(byte)0xCA ,(byte)0xBE ,(byte)0x6E ,(byte)0x68 ,(byte)0xAE ,(byte)0x6C ,(byte)0xFA ,(byte)0xF7 ,(byte)0x85 ,(byte)0x8F ,(byte)0x7A
        ,(byte)0x67 ,(byte)0xCC ,(byte)0x77 ,(byte)0xEF ,(byte)0xDA ,(byte)0x74 ,(byte)0x42 ,(byte)0xA4 ,(byte)0xDD ,(byte)0x45 ,(byte)0x07 ,(byte)0x4E ,(byte)0xAB ,(byte)0x77 ,(byte)0xFE ,(byte)0x72
        ,(byte)0xD0 ,(byte)0x20 ,(byte)0x94 ,(byte)0x6B ,(byte)0x14 ,(byte)0xC7 ,(byte)0x65 ,(byte)0x40 ,(byte)0xEB ,(byte)0xD6 ,(byte)0xDD ,(byte)0xCB ,(byte)0x04 ,(byte)0xE6 ,(byte)0xC4 ,(byte)0xD7
        ,(byte)0xB2 ,(byte)0x5C ,(byte)0xCD ,(byte)0xDA ,(byte)0x4F ,(byte)0x85 ,(byte)0x6B ,(byte)0x0A ,(byte)0xAC ,(byte)0x81 ,(byte)0xEA ,(byte)0xB9 ,(byte)0x0E ,(byte)0x39 ,(byte)0x44 ,(byte)0x7F
        ,(byte)0x8F ,(byte)0xED ,(byte)0x0C ,(byte)0x1B ,(byte)0xE2 ,(byte)0xF7 ,(byte)0x7B ,(byte)0x32 ,(byte)0xD7 ,(byte)0xD0 ,(byte)0x26 ,(byte)0x07 ,(byte)0x8D ,(byte)0xBE ,(byte)0x9A ,(byte)0x97
        ,(byte)0x80 ,(byte)0x69 ,(byte)0x85 ,(byte)0xA7 ,(byte)0xAB ,(byte)0x48 ,(byte)0x29 ,(byte)0xC4 ,(byte)0x1F ,(byte)0x62 ,(byte)0xB2 ,(byte)0xB9 ,(byte)0x78 ,(byte)0x22 ,(byte)0x28 ,(byte)0xF6
        ,(byte)0x77 ,(byte)0x30 ,(byte)0xF2 ,(byte)0x4F ,(byte)0x94 ,(byte)0x98 ,(byte)0x1D ,(byte)0x28 ,(byte)0x37 ,(byte)0xCA ,(byte)0xE5 ,(byte)0x84 ,(byte)0x78 ,(byte)0x37 ,(byte)0xCB ,(byte)0x03
        ,(byte)0x24 ,(byte)0xAF ,(byte)0x7A ,(byte)0x0D ,(byte)0x58 ,(byte)0x95 ,(byte)0x28 ,(byte)0xCB ,(byte)0xB8 ,(byte)0x3E ,(byte)0x2F ,(byte)0xA8 ,(byte)0xCD ,(byte)0x37 ,(byte)0x53 ,(byte)0xBD
        ,(byte)0xBB ,(byte)0xF6 ,(byte)0x34 ,(byte)0x58 ,(byte)0x71 ,(byte)0x96 ,(byte)0xDF ,(byte)0x25 ,(byte)0xFB ,(byte)0x46 ,(byte)0x58 ,(byte)0x06 ,(byte)0x96 ,(byte)0xFE ,(byte)0xB5 ,(byte)0xB1
        ,(byte)0xB3 ,(byte)0xF4 ,(byte)0x46 ,(byte)0x0F ,(byte)0x31 ,(byte)0x95 ,(byte)0xFD ,(byte)0xB9 ,(byte)0x38 ,(byte)0x03 ,(byte)0x6C ,(byte)0x1F ,(byte)0xB8 ,(byte)0x84 ,(byte)0xF6 ,(byte)0xB5
        ,(byte)0xCF ,(byte)0x0B ,(byte)0xED ,(byte)0xEB ,(byte)0xD2 ,(byte)0x66 ,(byte)0xED ,(byte)0x97 ,(byte)0x3E ,(byte)0x16 ,(byte)0xA2 ,(byte)0x9D ,(byte)0xAE ,(byte)0x32 ,(byte)0xFD ,(byte)0xE1
        ,(byte)0xE3 ,(byte)0xC3 ,(byte)0xDF ,(byte)0xB6 ,(byte)0xD6 ,(byte)0x18 ,(byte)0x0E ,(byte)0xC3 ,(byte)0x3E ,(byte)0xF9 ,(byte)0x9B ,(byte)0x68 ,(byte)0x4E ,(byte)0x39 ,(byte)0xDD ,(byte)0x9D
        ,(byte)0xEF ,(byte)0x00 ,(byte)0x92 ,(byte)0x67 ,(byte)0x06 ,(byte)0x10 ,(byte)0x36 ,(byte)0x72 ,(byte)0xD0 ,(byte)0xAB ,(byte)0x28 ,(byte)0xBA ,(byte)0xAE ,(byte)0xE8 ,(byte)0xD1 ,(byte)0x08
        ,(byte)0xC0 ,(byte)0x2B ,(byte)0xE2 ,(byte)0x3C ,(byte)0xE0 ,(byte)0x34 ,(byte)0xF3 ,(byte)0x67 ,(byte)0x2A ,(byte)0x1E ,(byte)0xA9 ,(byte)0xF8 ,(byte)0xF2 ,(byte)0xBE ,(byte)0x88 ,(byte)0xB8
        ,(byte)0x98 ,(byte)0x11 ,(byte)0x76 ,(byte)0x2E ,(byte)0x51 ,(byte)0x70 ,(byte)0x49 ,(byte)0xD6 ,(byte)0x21 ,(byte)0x22 ,(byte)0xC4 ,(byte)0x03 ,(byte)0x48 ,(byte)0x7E ,(byte)0x2B ,(byte)0x79
        ,(byte)0x8F ,(byte)0x03 ,(byte)0xDC ,(byte)0x00 ,(byte)0x00 ,(byte)0xB8};

        AACDepacketizer aacDepacketizer = new AACDepacketizer();
        AACPackets aacPackets = aacDepacketizer.createAACPackets(bytesOfRTPPacket);
        assertEquals(1, aacPackets.getNumberOfPackets());

        final AACPacket aacPacket = aacPackets.getPacket(0);
        final Timestamp timestamp = aacPacket.getTimestamp();
        assertEquals(134671883, timestamp.getTimestampInMillis());

        final AccessUnit accessUnit = aacPacket.getAccessUnit();
        int expectedAccessUnitLength = bytesOfRTPPacket.length-RTPHeader.LENGTH-AACPayload.HEADER_LENGHT;
        assertEquals(expectedAccessUnitLength, accessUnit.getData().length);
    }


}


