package com.example.zebul.cameraservice.video_streaming.rtp.nal_unit;

/**
 * Created by zebul on 11/6/16.

 0      Unspecified                                                    non-VCL
 1      Coded slice of a non-IDR picture                               VCL
 2      Coded slice data partition A                                   VCL
 3      Coded slice data partition B                                   VCL
 4      Coded slice data partition C                                   VCL
 5      Coded slice of an IDR picture                                  VCL
 6      Supplemental enhancement information (SEI)                     non-VCL
 7      Sequence parameter set                                         non-VCL
 8      Picture parameter set                                          non-VCL
 9      Access unit delimiter                                          non-VCL
 10     End of sequence                                                non-VCL
 11     End of stream                                                  non-VCL
 12     Filler data                                                    non-VCL
 13     Sequence parameter set extension                               non-VCL
 14     Prefix NAL unit                                                non-VCL
 15     Subset sequence parameter set                                  non-VCL
 16     Depth parameter set                                            non-VCL
 17..18 Reserved                                                       non-VCL
 19     Coded slice of an auxiliary coded picture without partitioning non-VCL
 20     Coded slice extension                                          non-VCL
 21     Coded slice extension for depth view components                non-VCL
 22..23 Reserved                                                       non-VCL
 24..31 Unspecified                                                    non-VCL

 */

public enum NALUnitType {

    Unspecified,
    CodedSliceOfnon_IDRpicture,
    CodedSliceDataPartition_A,
    CodedSliceDataPartition_B,
    CodedSliceDataPartition_C,
    CodedSliceOfAnIDRpicture,
    SupplementalEnhancementInformation,
    SequenceParameterSet,
    PictureParameterSet,
    AccessUnitDelimiter,
    EndOfSequence,
    EndOfStream,
    FillerData,
    SequenceParameterSetExtension,
    PrefixNALUnit,
    SubsetSequenceParameterSet,
    DepthParameterSet,
    Reserved17,
    Reserved18,
    CodedSliceOfAnAuxiliaryCodedPictureWithoutPartitioning,
    CodedSliceExtension,
    CodedSliceExtensionForDepthViewComponents,
    Reserved22,
    Reserved23,
    STAP_A,
    STAP_B,
    MTAP16,
    MTAP24,
    FU_A,
    FU_B,
    Reserved30,
    Reserved31;


    public static NALUnitType [] NAL_UNIT_TYPES = NALUnitType.values();

    public static NALUnitType fromByte(byte value){

        return NAL_UNIT_TYPES[value];
    }
}
