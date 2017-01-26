package com.example.zebul.cameraservice.av_streaming.rtp.aac;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by zebul on 12/21/16.
 */

public class AACPayloadTest {

    @Test
    public void compare_serizalization_with_vlc__sample1() throws Exception {

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

        byte [] vlcAACHeader = new byte[]{(byte)0x00 ,(byte)0x10 ,(byte)0x0C ,(byte)0x60};
        byte [] vlcAccessUnit = new byte[]{

                 (byte)0x21 ,(byte)0x0C ,(byte)0x14 ,(byte)0x4D ,(byte)0xEA ,(byte)0x94
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
                ,(byte)0x8F ,(byte)0x03 ,(byte)0xDC ,(byte)0x00 ,(byte)0x00 ,(byte)0xB8
        };

        byte [] vlcPayload = new byte[vlcAACHeader.length+vlcAccessUnit.length];
        System.arraycopy(vlcAACHeader, 0, vlcPayload, 0, vlcAACHeader.length);
        System.arraycopy(vlcAccessUnit, 0, vlcPayload, vlcAACHeader.length, vlcAccessUnit.length);

        AccessUnit accessUnit = new AccessUnit(vlcAccessUnit);
        AACPayload aacPayload = new AACPayload(accessUnit);

        byte [] outputPayload = new byte[aacPayload.computeLenght()];
        aacPayload.toBytes(outputPayload, 0);
        assertArrayEquals(vlcPayload, outputPayload);

    }
}
