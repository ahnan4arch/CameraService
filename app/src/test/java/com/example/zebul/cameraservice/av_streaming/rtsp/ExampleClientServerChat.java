package com.example.zebul.cameraservice.av_streaming.rtsp;

/*
//--------BEG OF OPTIONS--------
Frame 1022: 192 bytes on wire (1536 bits), 192 bytes captured (1536 bits) on interface 0
Ethernet II, Src: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb), Dst: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8)
Internet Protocol Version 4, Src: 192.168.1.22, Dst: 192.168.1.106
Transmission Control Protocol, Src Port: 53951 (53951), Dst Port: 8554 (8554), Seq: 1, Ack: 1, Len: 126
Real Time Streaming Protocol
    Request: OPTIONS rtsp://192.168.1.106:8554/s1 RTSP/1.0\r\n
    CSeq: 2\r\n
    User-Agent: LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)\r\n
    \r\n

Frame 1024: 190 bytes on wire (1520 bits), 190 bytes captured (1520 bits) on interface 0
Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb)
Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.22
Transmission Control Protocol, Src Port: 8554 (8554), Dst Port: 53951 (53951), Seq: 1, Ack: 127, Len: 124
Real Time Streaming Protocol
    Response: RTSP/1.0 200 OK\r\n
    Server: VLC/2.2.2\r\n
    Content-length: 0
    Cseq: 2\r\n
    Public: DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE,GET_PARAMETER\r\n
    \r\n
//--------END OF OPTIONS--------

//--------BEG OF DESCRIBE--------

RTSP/1.0 200 OK\r\n
    Server: VLC/2.2.2\r\n
    Date: Wed, 25 Jan 2017 16:28:29 GMT\r\n
    Content-type: application/sdp
    Content-Base: rtsp://192.168.1.106:8554/s1\r\n
    Content-length: 691
    Cache-Control: no-cache\r\n
    Cseq: 3\r\n
    \r\n
v=0\r\n
o=- 15867114619150279987 15867114619150279987 IN IP4 zebul-NV78\r\n
s=Unnamed\r\n
i=N/A\r\n
c=IN IP4 0.0.0.0\r\n
t=0 0\r\n
a=tool:vlc 2.2.2\r\n
a=recvonly\r\n
a=type:broadcast\r\n
a=charset:UTF-8\r\n
a=control:rtsp://192.168.1.106:8554/s1\r\n
m=audio 0 RTP/AVP 96\r\n
b=RR:0\r\n
a=rtpmap:96 mpeg4-generic/44100/2\r\n
a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1210; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;\r\n
a=control:rtsp://192.168.1.106:8554/s1/trackID=4\r\n
m=video 0 RTP/AVP 96\r\n
b=RR:0\r\n
a=rtpmap:96 H264/90000\r\n
a=fmtp:96 packetization-mode=1;profile-level-id=42e00d;sprop-parameter-sets=J0LgDakYKD9gDUGAQa23oC8B6XvfAQ==,KM4JiA==;\r\n
a=control:rtsp://192.168.1.106:8554/s1/trackID=5\r\n


--- VERSION 2 --paresd dump
Frame 1026: 218 bytes on wire (1744 bits), 218 bytes captured (1744 bits) on interface 0
Ethernet II, Src: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb), Dst: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8)
Internet Protocol Version 4, Src: 192.168.1.22, Dst: 192.168.1.106
Transmission Control Protocol, Src Port: 53951 (53951), Dst Port: 8554 (8554), Seq: 127, Ack: 125, Len: 152
Real Time Streaming Protocol
    Request: DESCRIBE rtsp://192.168.1.106:8554/s1 RTSP/1.0\r\n
    CSeq: 3\r\n
    User-Agent: LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)\r\n
    Accept: application/sdp\r\n
    \r\n

Frame 1029: 739 bytes on wire (5912 bits), 739 bytes captured (5912 bits) on interface 0
Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb)
Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.22
Transmission Control Protocol, Src Port: 8554 (8554), Dst Port: 53951 (53951), Seq: 330, Ack: 279, Len: 673
[2 Reassembled TCP Segments (878 bytes): #1027(205), #1029(673)]
Real Time Streaming Protocol
    Response: RTSP/1.0 200 OK\r\n
        Status: 200
    Server: VLC/2.2.2\r\n
    Date: Sat, 10 Dec 2016 15:18:10 GMT\r\n
    Content-type: application/sdp
    Content-Base: rtsp://192.168.1.106:8554/s1\r\n
    Content-length: 673
    Cache-Control: no-cache\r\n
    Cseq: 3\r\n
    \r\n
    Session Description Protocol
        Session Description Protocol Version (v): 0
        Owner/Creator, Session Id (o): - 15850026581053542000 15850026581053542000 IN IP4 zebul-NV78
            Owner Username: -
            Session ID: 15850026581053542000
            Session Version: 15850026581053542000
            Owner Network KnownName: IN
            Owner Address KnownName: IP4
            Owner Address: zebul-NV78
        Session Name (s): Unnamed
        Session Information (i): N/A
        Connection Information (c): IN IP4 0.0.0.0
            Connection Network KnownName: IN
            Connection Address KnownName: IP4
            Connection Address: 0.0.0.0
        Time Description, active time (t): 0 0
            Session Start Time: 0
            Session Stop Time: 0
        Session Attribute (a): tool:vlc 2.2.2
            Session Attribute Fieldname: tool
            Session Attribute Value: vlc 2.2.2
        Session Attribute (a): recvonly
        Session Attribute (a): type:broadcast
            Session Attribute Fieldname: type
            Session Attribute Value: broadcast
        Session Attribute (a): charset:UTF-8
            Session Attribute Fieldname: charset
            Session Attribute Value: UTF-8
        Session Attribute (a): control:rtsp://192.168.1.106:8554/s1
            Session Attribute Fieldname: control
            Session Attribute Value: rtsp://192.168.1.106:8554/s1
        Media Description, name and address (m): audio 0 RTP/AVP 96
            Media KnownName: audio
            Media Port: 0
            Media Protocol: RTP/AVP
            Media Format: DynamicRTP-KnownName-96
        Bandwidth Information (b): RR:0
            Bandwidth Modifier: RR
            Bandwidth Value: 0
        Media Attribute (a): rtpmap:96 mpeg4-generic/44100
            Media Attribute Fieldname: rtpmap
            Media Format: 96
            MIME KnownName: mpeg4-generic
            Sample Rate: 44100
        Media Attribute (a): fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1208; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;
            Media Attribute Fieldname: fmtp
            Media Format: 96 [mpeg4-generic]
            Media format specific parameters: streamtype=5
            Media format specific parameters: profile-level-id=15
            Media format specific parameters: mode=AAC-hbr
            Media format specific parameters: config=1208
            Media format specific parameters: SizeLength=13
            Media format specific parameters: IndexLength=3
            Media format specific parameters: IndexDeltaLength=3
            Media format specific parameters: Profile=1
            Media format specific parameters:
        Media Attribute (a): control:rtsp://192.168.1.106:8554/s1/trackID=6
            Media Attribute Fieldname: control
            Media Attribute Value: rtsp://192.168.1.106:8554/s1/trackID=6
        Media Description, name and address (m): video 0 RTP/AVP 96
            Media KnownName: video
            Media Port: 0
            Media Protocol: RTP/AVP
            Media Format: DynamicRTP-KnownName-96
        Bandwidth Information (b): RR:0
            Bandwidth Modifier: RR
            Bandwidth Value: 0
        Media Attribute (a): rtpmap:96 H264/90000
            Media Attribute Fieldname: rtpmap
            Media Format: 96
            MIME KnownName: H264
            Sample Rate: 90000
        Media Attribute (a): fmtp:96 packetization-mode=1;profile-level-id=42802a;sprop-parameter-sets=Z0KAKpWgHgCJeVA=,aM48gA==;
            Media Attribute Fieldname: fmtp
            Media Format: 96 [H264]
            Media format specific parameters: packetization-mode=1
                [Packetization mode: Non-interleaved mode (1)]
            Media format specific parameters: profile-level-id=42802a
                Profile: 42802a
                    0100 0010 = Profile_idc: Baseline profile (66)
                    1... .... = Constraint_set0_flag: 1
                    .0.. .... = Constraint_set1_flag: 0
                    ..0. .... = Constraint_set2_flag: 0
                    ...0 .... = Constraint_set3_flag: 0
                    .... 0... = Constraint_set4_flag: 0
                    .... .0.. = Constraint_set5_flag: 0
                    .... ..00 = Reserved_zero_2bits: 0
                    0010 1010 = Level_id: 42 [Level 4,2 50 Mb/s]
            Media format specific parameters: sprop-parameter-sets=Z0KAKpWgHgCJeVA=,aM48gA==
                NAL unit 1 string: Z0KAKpWgHgCJeVA=
                NAL unit: 6742802a95a01e00897950
                    0... .... = Forbidden_zero_bit: 0
                    .11. .... = Nal_ref_idc: 3
                    ...0 0111 = Nal_unit_type: Sequence parameter set (7)
                    0100 0010 = Profile_idc: Baseline profile (66)
                    1... .... = Constraint_set0_flag: 1
                    .0.. .... = Constraint_set1_flag: 0
                    ..0. .... = Constraint_set2_flag: 0
                    ...0 .... = Constraint_set3_flag: 0
                    .... 0... = Constraint_set4_flag: 0
                    .... .0.. = Constraint_set5_flag: 0
                    .... ..00 = Reserved_zero_2bits: 0
                    0010 1010 = Level_id: 42 [Level 4,2 50 Mb/s]
                    1... .... = seq_parameter_set_id: 0
                    .001 01.. = log2_max_frame_num_minus4: 4
                    .... ..01  1... .... = pic_order_cnt_type: 2
                    .010 .... = num_ref_frames: 1
                    .... 0... = gaps_in_frame_num_value_allowed_flag: 0
                    .... .000  0001 1110  00.. .... = pic_width_in_mbs_minus1: 119
                    ..00 0000  1000 100. = pic_height_in_map_units_minus1: 67
                    .... ...1 = frame_mbs_only_flag: 1
                    0... .... = direct_8x8_inference_flag: 0
                    .1.. .... = frame_cropping_flag: 1
                    ..1. .... = frame_crop_left_offset: 0
                    ...1 .... = frame_crop_left_offset: 0
                    .... 1... = frame_crop_top_offset: 0
                    .... .001  01.. .... = frame_crop_bottom_offset: 4
                    ..0. .... = vui_parameters_present_flag: 0
                    ...1 .... = rbsp_stop_bit: 1
                    .... 0000 = rbsp_trailing_bits: 0
                NAL unit 2 string: aM48gA==
                NAL unit: 68ce3c80
                    0... .... = Forbidden_zero_bit: 0
                    .11. .... = Nal_ref_idc: 3
                    ...0 1000 = Nal_unit_type: Picture parameter set (8)
                    1... .... = pic_parameter_set_id: 0
                    .1.. .... = seq_parameter_set_id: 0
                    ..0. .... = entropy_coding_mode_flag: 0
                    ...0 .... = pic_order_present_flag: 0
                    .... 1... = num_slice_groups_minus1: 0
                    .... .1.. = num_ref_idx_l0_active_minus1: 0
                    .... ..1. = num_ref_idx_l1_active_minus1: 0
                    .... ...0 = weighted_pred_flag: 0
                    00.. .... = weighted_bipred_idc: 0
                    ..1. .... = pic_init_qp_minus26: 0
                    ...1 .... = pic_init_qs_minus26: 0
                    .... 1... = chroma_qp_index_offset: 0
                    .... .1.. = deblocking_filter_control_present_flag: 1
                    .... ..0. = constrained_intra_pred_flag: 0
                    .... ...0 = redundant_pic_cnt_present_flag: 0
                    1... .... = rbsp_stop_bit: 1
                    .000 0000 = rbsp_trailing_bits: 0
            Media format specific parameters:
        Media Attribute (a): control:rtsp://192.168.1.106:8554/s1/trackID=7
            Media Attribute Fieldname: control
            Media Attribute Value: rtsp://192.168.1.106:8554/s1/trackID=7


RTSP/1.0 200 OK
Server: VLC/2.2.2
Date: Sat, 10 Dec 2016 15:18:10 GMT
Content-KnownName: application/sdp
Content-Base: rtsp://192.168.1.106:8554/s1
Content-Length: 673
Cache-Control: no-cache
Cseq: 3

v=0
o=- 15850026581053542000 15850026581053542000 IN IP4 zebul-NV78
s=Unnamed
i=N/A
c=IN IP4 0.0.0.0
t=0 0
a=tool:vlc 2.2.2
a=recvonly
a=type:broadcast
a=charset:UTF-8
a=control:rtsp://192.168.1.106:8554/s1
m=audio 0 RTP/AVP 96
b=RR:0
a=rtpmap:96 mpeg4-generic/44100
a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config=1208; SizeLength=13; IndexLength=3; IndexDeltaLength=3; Profile=1;
a=control:rtsp://192.168.1.106:8554/s1/trackID=6
m=video 0 RTP/AVP 96
b=RR:0
a=rtpmap:96 H264/90000
a=fmtp:96 packetization-mode=1;profile-level-id=42802a;sprop-parameter-sets=Z0KAKpWgHgCJeVA=,aM48gA==;
a=control:rtsp://192.168.1.106:8554/s1/trackID=7

//--------END OF DESCRIBE--------


//--------BEG OF SETUP TRACK=6---
Frame 1031: 252 bytes on wire (2016 bits), 252 bytes captured (2016 bits) on interface 0
Ethernet II, Src: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb), Dst: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8)
Internet Protocol Version 4, Src: 192.168.1.22, Dst: 192.168.1.106
Transmission Control Protocol, Src Port: 53951 (53951), Dst Port: 8554 (8554), Seq: 279, Ack: 1003, Len: 186
Real Time Streaming Protocol
    Request: SETUP rtsp://192.168.1.106:8554/s1/trackID=6 RTSP/1.0\r\n
        Method: SETUP
        URL: rtsp://192.168.1.106:8554/s1/trackID=6
    CSeq: 4\r\n
    User-Agent: LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)\r\n
    Transport: RTP/AVP;unicast;client_port=60842-60843
    \r\n

Frame 1032: 336 bytes on wire (2688 bits), 336 bytes captured (2688 bits) on interface 0
Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb)
Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.22
Transmission Control Protocol, Src Port: 8554 (8554), Dst Port: 53951 (53951), Seq: 1003, Ack: 465, Len: 270
Real Time Streaming Protocol
    Response: RTSP/1.0 200 OK\r\n
        Status: 200
    Server: VLC/2.2.2\r\n
    Date: Sat, 10 Dec 2016 15:18:10 GMT\r\n
    Transport: RTP/AVP/UDP;unicast;client_port=60842-60843;server_port=44573-44574;ssrc=785BF592;mode=play
    Session: 57327121a50863c8;timeout=60
    Content-length: 0
    Cache-Control: no-cache\r\n
    Cseq: 4\r\n
    \r\n

//--------END OF SETUP TRACK=6---

//--------BEG OF SETUP TRACK=7---
Frame 1033: 279 bytes on wire (2232 bits), 279 bytes captured (2232 bits) on interface 0
Ethernet II, Src: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb), Dst: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8)
Internet Protocol Version 4, Src: 192.168.1.22, Dst: 192.168.1.106
Transmission Control Protocol, Src Port: 53951 (53951), Dst Port: 8554 (8554), Seq: 465, Ack: 1273, Len: 213
Real Time Streaming Protocol
    Request: SETUP rtsp://192.168.1.106:8554/s1/trackID=7 RTSP/1.0\r\n
        Method: SETUP
        URL: rtsp://192.168.1.106:8554/s1/trackID=7
    CSeq: 5\r\n
    User-Agent: LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)\r\n
    Transport: RTP/AVP;unicast;client_port=48858-48859
    Session: 57327121a50863c8
    \r\n

Frame 1034: 336 bytes on wire (2688 bits), 336 bytes captured (2688 bits) on interface 0
Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb)
Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.22
Transmission Control Protocol, Src Port: 8554 (8554), Dst Port: 53951 (53951), Seq: 1273, Ack: 678, Len: 270
Real Time Streaming Protocol
    Response: RTSP/1.0 200 OK\r\n
        Status: 200
    Server: VLC/2.2.2\r\n
    Date: Sat, 10 Dec 2016 15:18:10 GMT\r\n
    Transport: RTP/AVP/UDP;unicast;client_port=48858-48859;server_port=49232-49233;ssrc=25ED8B41;mode=play
    Session: 57327121a50863c8;timeout=60
    Content-length: 0
    Cache-Control: no-cache\r\n
    Cseq: 5\r\n
    \r\n

//--------END OF SETUP TRACK=7---

//--------BEG OF PLAY------------
Frame 1048: 235 bytes on wire (1880 bits), 235 bytes captured (1880 bits) on interface 0
Ethernet II, Src: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb), Dst: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8)
Internet Protocol Version 4, Src: 192.168.1.22, Dst: 192.168.1.106
Transmission Control Protocol, Src Port: 53951 (53951), Dst Port: 8554 (8554), Seq: 678, Ack: 1543, Len: 169
Real Time Streaming Protocol
    Request: PLAY rtsp://192.168.1.106:8554/s1 RTSP/1.0\r\n
        Method: PLAY
        URL: rtsp://192.168.1.106:8554/s1
    CSeq: 6\r\n
    User-Agent: LibVLC/3.0.0-git (LIVE555 Streaming Media v2016.02.22)\r\n
    Session: 57327121a50863c8
    Range: npt=0.000-\r\n
    \r\n

Frame 1050: 407 bytes on wire (3256 bits), 407 bytes captured (3256 bits) on interface 0
Ethernet II, Src: IntelCor_a4:9c:a8 (00:1e:65:a4:9c:a8), Dst: SamsungE_53:fe:eb (5c:0a:5b:53:fe:eb)
Internet Protocol Version 4, Src: 192.168.1.106, Dst: 192.168.1.22
Transmission Control Protocol, Src Port: 8554 (8554), Dst Port: 53951 (53951), Seq: 1543, Ack: 847, Len: 341
Real Time Streaming Protocol
    Response: RTSP/1.0 200 OK\r\n
        Status: 200
    Server: VLC/2.2.2\r\n
    Date: Sat, 10 Dec 2016 15:18:10 GMT\r\n
    RTP-Info: url=rtsp://192.168.1.106:8554/s1/trackID=6;seq=21321;rtptime=88848738, url=rtsp://192.168.1.106:8554/s1/trackID=7;seq=516;rtptime=181323956\r\n
    Range: npt=316.360772-\r\n
    Session: 57327121a50863c8;timeout=60
    Content-length: 0
    Cache-Control: no-cache\r\n
    Cseq: 6\r\n
    \r\n

//--------END OF PLAY------------
 */
public class ExampleClientServerChat {
}
