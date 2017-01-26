package com.example.zebul.cameraservice.av_streaming.rtsp.response;

import com.example.zebul.cameraservice.av_streaming.rtsp.RTSPProtocol;
import com.example.zebul.cameraservice.av_streaming.rtsp.StatusCode;
import com.example.zebul.cameraservice.av_streaming.rtsp.error.RTSP5xxServerResponseError;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.body.Body;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.Header;
import com.example.zebul.cameraservice.av_streaming.rtsp.message.header.HeaderDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.request.RTSPRequestDecoder;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.Version;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionDecodeException;
import com.example.zebul.cameraservice.av_streaming.rtsp.version.VersionDecoder;

/**
 * Created by zebul on 1/15/17.
 */

public class RTSPResponseDecoder {

    private static final int POS_OF_STATUS_LINE = 0;
    private static final int POS_OF_HEADER_LINE = 1;

    private static final int POS_OF_RTSP_VERSION_IN_STATUS_LINE = 0;
    private static final int POS_OF_CODE_IN_STATUS_LINE = 1;
    private static final int POS_OF_REASON_PHRASE_IN_STATUS_LINE = 2;

    /*
    Response    =     Status-Line         ; Section 7.1
                 *(    general-header      ; Section 5
                 |     response-header     ; Section 7.1.2
                 |     entity-header )     ; Section 8.1
                       CRLF
                       [ message-body ]    ; Section 4.3
    */
    public static RTSPResponse decode(String responseRepresentationAsText)
            throws RTSP5xxServerResponseError {

        String[] responseRepresentationAsTextLines = splitByLines(responseRepresentationAsText);
        RTSPResponseDecoder.StatusLine statusLine = decodeStatusLine(responseRepresentationAsTextLines);
        Header header = decodeHeader(responseRepresentationAsTextLines);
        Body body = decodeBody(responseRepresentationAsTextLines, header.getNumberOfFields());
        return new RTSPResponse(statusLine.statusCode, statusLine.version, header, body);
    }

    private static String[] splitByLines(String responseRepresentaionAsText) {

        String[] requestRepresentaionAsTextLines = responseRepresentaionAsText.split(
                RTSPProtocol.LINE_SEPARATOR);
        return requestRepresentaionAsTextLines;
    }

    /*
    7.1 Status-Line

    The first line of a Response message is the Status-Line, consisting
    of the protocol version followed by a numeric status code, and the
    textual phrase associated with the status code, with each element
    separated by SP characters. No CR or LF is allowed except in the
    final CRLF sequence.

    Status-Line =   RTSP-Version SP Status-Code SP Reason-Phrase CRLF
    */

    public static class StatusLine {

        Version version;
        StatusCode statusCode;
        String reason;
    }

    private static RTSPResponseDecoder.StatusLine decodeStatusLine(
            String [] responseRepresentaionAsTextLines)
            throws RTSP5xxServerResponseError {

        String statusLineAsText = responseRepresentaionAsTextLines[POS_OF_STATUS_LINE];
        final int minStatusLineTokensNumber = POS_OF_REASON_PHRASE_IN_STATUS_LINE +1;

        String[] responseLineTokens = statusLineAsText.split(RTSPProtocol.SP);
		if(responseLineTokens.length < minStatusLineTokensNumber){
			String errorMessage = String.format(
					"Request line should contains %d parts: RTSP-Version Status-Code Reason-Phrase, separated by '%s'",
                    minStatusLineTokensNumber, RTSPProtocol.SP);
			throw new RTSP5xxServerResponseError(StatusCode.INTERNAL_SERVER_ERROR, errorMessage);
		}

        StatusLine statusLine = new StatusLine();
        String versionAsText = responseLineTokens[POS_OF_RTSP_VERSION_IN_STATUS_LINE];
        try {
            statusLine.version = VersionDecoder.decode(versionAsText);
        } catch (VersionDecodeException exc) {
            throw new RTSP5xxServerResponseError(StatusCode.RTSP_VERSION_NOT_SUPPORTED, exc.getMessage());
        }

        String codeAsText = responseLineTokens[POS_OF_CODE_IN_STATUS_LINE];
        String reason = responseLineTokens[POS_OF_REASON_PHRASE_IN_STATUS_LINE];

        try{

            int code = Integer.parseInt(codeAsText);

            /*
            The first digit of the Status-Code defines the class of response. The
           last two digits do not have any categorization role. There are 5
           values for the first digit:

             * 1xx: Informational - Request received, continuing process
             * 2xx: Success - The action was successfully received, understood,
               and accepted
             * 3xx: Redirection - Further action must be taken in order to
               complete the request
             * 4xx: Client Error - The request contains bad syntax or cannot be
               fulfilled
             * 5xx: Server Error - The server failed to fulfill an apparently
               valid request
            */

            if(code != StatusCode.OK.getCode()){
                String errorMessage = String.format("Expected status code 200 received: %d (reason: %s)", code, reason);
                throw new RTSP5xxServerResponseError(StatusCode.INTERNAL_SERVER_ERROR, errorMessage);
            }
            statusLine.statusCode = StatusCode.OK;
            statusLine.reason = reason;
        }
        catch(NumberFormatException exc_){

            String errorMessage = String.format("Unsupported status code format '%s'", codeAsText);
            throw new RTSP5xxServerResponseError(StatusCode.INTERNAL_SERVER_ERROR, errorMessage);
        }

        return statusLine;
    }

    /*
    The response-header fields allow the request recipient to pass
    additional information about the response which cannot be placed in
    the Status-Line. These header fields give information about the
    server and about further access to the resource identified by the
    Request-URI.

    response-header  =     Location             ; Section 12.25
                    |     Proxy-Authenticate   ; Section 12.26
                    |     Public               ; Section 12.28
                    |     Retry-After          ; Section 12.31
                    |     Server               ; Section 12.36
                    |     Vary                 ; Section 12.42
                    |     WWW-Authenticate     ; Section 12.44
    */
    private static Header decodeHeader(String[] responseRepresentationAsTextLines) {

        return HeaderDecoder.decode(responseRepresentationAsTextLines);
    }

    private static Body decodeBody(String[] responseRepresentationAsTextLines,
                                   int numberOfHeaderFieldsInResponse) {

        int lineOffset = numberOfHeaderFieldsInResponse + POS_OF_HEADER_LINE;
        for(int pos=lineOffset; pos<responseRepresentationAsTextLines.length; pos++){

            String value = responseRepresentationAsTextLines[pos];

            int foo = 1;
            int bar = foo;
        }

        return null;
    }
}
