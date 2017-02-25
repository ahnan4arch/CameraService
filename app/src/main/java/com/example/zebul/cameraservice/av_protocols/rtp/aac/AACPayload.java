package com.example.zebul.cameraservice.av_protocols.rtp.aac;

import com.example.zebul.cameraservice.av_protocols.rtp.RTPPayload;

/**
 * Created by zebul on 12/21/16.
 */

public class AACPayload implements RTPPayload{

    public static final int HEADER_LENGHT = 4;

    private final AccessUnit accessUnit;

    public AACPayload(AccessUnit accessUnit) {

        this.accessUnit = accessUnit;
    }

    @Override
    public int computeLenght() {
        return accessUnit.getData().length+HEADER_LENGHT;
    }

    @Override
    public void toBytes(byte[] rtpPacketBytes, int startPosition) {

        byte [] accessUnitData = accessUnit.getData();

        int position = startPosition;
        rtpPacketBytes[position++] = 0x00;
        rtpPacketBytes[position++] = 0x10;
        rtpPacketBytes[position++] = (byte) (((int) accessUnitData.length & 0x1FE0) >> 5);
        rtpPacketBytes[position++] = (byte) (((int) accessUnitData.length & 0x001F) << 3);

        System.arraycopy(accessUnitData, 0, rtpPacketBytes, position, accessUnitData.length);
    }

    public static AACPayload fromBytes(byte[] rtpPacketBytes, int startPosition) {

        int position = startPosition;
        if(rtpPacketBytes[position++] != 0x00){
            return null;
        }
        if(rtpPacketBytes[position++] != 0x10){
            return null;
        }

        int accessUnitLength =
            (0x1FE0 & (rtpPacketBytes[position++] << 5))|
            (0x001F & (rtpPacketBytes[position++] >> 3));

        int stopPosition = accessUnitLength+position;
        if(rtpPacketBytes.length < stopPosition){
            return null;
        }

        byte [] accessUnitData = new byte[accessUnitLength];
        System.arraycopy(rtpPacketBytes, position, accessUnitData, 0, accessUnitData.length);
        AccessUnit accessUnit = new AccessUnit(accessUnitData);
        return new AACPayload(accessUnit);
    }

    public AccessUnit getAccessUnit() {
        return accessUnit;
    }
}
