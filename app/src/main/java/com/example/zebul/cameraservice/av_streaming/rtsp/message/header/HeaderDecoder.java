package com.example.zebul.cameraservice.av_streaming.rtsp.message.header;

import com.example.zebul.cameraservice.av_streaming.rtsp.RTSPProtocol;

import java.util.Arrays;

/**
 * Created by zebul on 1/25/17.
 */

public class HeaderDecoder {

    public static final int POS_OF_HEADER_LINE = 1;

    public static Header decode(String [] requestRepresentaionAsTextLines) {

        HeaderFields headerFields = new HeaderFields();
        int numberOfLines = requestRepresentaionAsTextLines.length;
        for (int lineIndex = POS_OF_HEADER_LINE; lineIndex <numberOfLines ; lineIndex++) {

            String headerFieldAsTextLine = requestRepresentaionAsTextLines[lineIndex];
            boolean emptyLine = headerFieldAsTextLine.length() == 0;
            if (emptyLine) {
                break;
            }
            boolean lineSeparator = headerFieldAsTextLine.startsWith(RTSPProtocol.LINE_SEPARATOR);
            if (lineSeparator) {
                break;
            }
            int notFound = -1;
            int begIndex = 0;
            int endIndex = headerFieldAsTextLine.length() - 1;
            int[] forbiddenIndexValues = new int[]{notFound, begIndex, endIndex};
            int indexOfColon = headerFieldAsTextLine.indexOf(':');
            if (Arrays.asList(forbiddenIndexValues).contains(indexOfColon)) {
                break;
            }

            String fieldName = headerFieldAsTextLine.substring(0, indexOfColon).trim();
            String fieldValue = headerFieldAsTextLine.substring(indexOfColon + 1, headerFieldAsTextLine.length()).trim();
            HeaderField headerField = new HeaderField(fieldName, fieldValue);
            headerFields.add(headerField);
        }

        Header header = new Header(headerFields);
        return header;
    }
}
