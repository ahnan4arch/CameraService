package com.example.zebul.cameraservice.video_streaming.rtp.payload;

import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnit;

/**
 * Created by zebul on 11/23/16.
 */

public enum RTPPayloadType {

    NALUnit,
    STAP_A,
    STAP_B,
    MTAP16,
    MTAP24,
    FU_A,
    FU_B
}
