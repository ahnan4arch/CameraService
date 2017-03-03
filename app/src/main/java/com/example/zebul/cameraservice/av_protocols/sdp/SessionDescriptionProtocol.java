package com.example.zebul.cameraservice.av_protocols.sdp;

/**
 * Created by zebul on 1/26/17.
 */

public class SessionDescriptionProtocol {

    public static final String LINE_SEPARATOR = "\r\n";
    public static final String SP = " ";

    public static SessionDescription decode(String sessionDescriptionAsText){

        return decode(sessionDescriptionAsText.split(LINE_SEPARATOR));
    }

    public static SessionDescription decode(String[] sessionDescriptionAsLines, int lineOffset) {

        SessionDescription sessionDescription = new SessionDescription();
        for(int i=lineOffset; i<sessionDescriptionAsLines.length; i++){

            String sdpLine = sessionDescriptionAsLines[i];
            /*
            An SDP session description consists of a number of lines of text of the form:
            <type>=<value>
            */
            boolean isValidSdpLine = sdpLine.length()>=3 && sdpLine.charAt(1)=='=';
            if(!isValidSdpLine){
                continue;
            }
            char type = sdpLine.charAt(0);
            String value = sdpLine.substring(2);
            switch(type){
                case 'v'://version
                    decodeVersion(sessionDescription, value);
                    break;
                case 'o'://origin
                    decodeOrigin(sessionDescription, value);
                    break;
                case 's'://session name
                    decodeSessionName(sessionDescription, value);
                    break;
                case 'a'://attrib
                    decodeAttribute(sessionDescription, value);
                    break;
                case 'm'://media
                    decodeMediaDescription(sessionDescription, value);
                    break;
            }
        }
        return sessionDescription;
    }

    public static SessionDescription decode(String [] sessionDescriptionAsLines){

        return decode(sessionDescriptionAsLines, 0);
    }

    public static String encode(SessionDescription sd){

        StringBuilder sdBuilder = new StringBuilder();

        String version = encodeVersion(sd);
        sdBuilder.append(version);

        String origin = encodeOrigin(sd);
        sdBuilder.append(origin);

        String sessionName = encodeSessionName(sd);
        sdBuilder.append(sessionName);

        return sdBuilder.toString();
    }

    private static String encodeOrigin(SessionDescription sd){

        /*
        Origin
        o=<username> <session id> <version> <network type> <address type> <address>

                The "o=" field gives the originator of the session (their username
                and the address of the user's host) plus a session id and session
        version number.

        <username> is the user's login on the originating host, or it is "-"
        if the originating host does not support the concept of user ids.
                <username> must not contain spaces.  <session id> is a numeric string
        such that the tuple of <username>, <session id>, <network type>,
        <address type> and <address> form a globally unique identifier for
        the session.

        The method of <session id> allocation is up to the creating tool, but
        it has been suggested that a Network Time Protocol (NTP) timestamp be
        used to ensure uniqueness [1].

        <version> is a version number for this announcement.  It is needed
        for proxy announcements to detect which of several announcements for
        the same session is the most recent.  Again its usage is up to the

        creating tool, so long as <version> is increased when a modification
        is made to the session data.  Again, it is recommended (but not
        mandatory) that an NTP timestamp is used.

        <network type> is a text string giving the type of network.
                Initially "IN" is defined to have the meaning "Internet".  <address
                type> is a text string giving the type of the address that follows.
                Initially "IP4" and "IP6" are defined.  <address> is the globally
        unique address of the machine from which the session was created.
                For an address type of IP4, this is either the fully-qualified domain
        name of the machine, or the dotted-decimal representation of the IP
        version 4 address of the machine.  For an address type of IP6, this
        is either the fully-qualified domain name of the machine, or the
        compressed textual representation of the IP version 6 address of the
        machine.  For both IP4 and IP6, the fully-qualified domain name is
        the form that SHOULD be given unless this is unavailable, in which
        case the globally unique address may be substituted.  A local IP
        address MUST NOT be used in any context where the SDP description
        might leave the scope in which the address is meaningful.

        In general, the "o=" field serves as a globally unique identifier for
        this version of this session description, and the subfields excepting
        the version taken together identify the session irrespective of any
        modifications.

        Origin
        o=<username> <session id> <version> <network type> <address type> <address>
        */
        String userName = sd.isConceptOfUserIdsSupported()?sd.getUserName():"-";
        String sessionId = ""+0;
        int ver = 0;
        String networkType = "IN";
        String addressType = "IP4";
        String address = "127.0.0.1";
        return String.format("o=%s %s %d %s %s %s", userName, sessionId, ver, networkType, addressType, address)+LINE_SEPARATOR;
    }

    private static void decodeOrigin(SessionDescription sd, String value){

    }

    private static String encodeVersion(SessionDescription sd){

        /*
        Protocol Version
        The "v=" field gives the version of the SessionDescription Description Protocol.
        There is no minor version number.
        */
        return String.format("v=%d", sd.version)+LINE_SEPARATOR;
    }

    private static void decodeVersion(SessionDescription sd, String value){

        sd.version = Integer.parseInt(value);
    }

    private static String encodeSessionName(SessionDescription sd){

        /*
		s=<session name>
		The "s=" field is the session name.  There must be one and only one
		"s=" field per session description, and it must contain ISO 10646
		characters (but see also the `charset' attribute below).
		 */
        String sessionName = String.format("s=%s", sd.name)+LINE_SEPARATOR;
        return sessionName;
    }

    private static void decodeSessionName(SessionDescription sd, String value) {

        sd.name = value;
    }

    private static void decodeMediaDescription(SessionDescription sessionDescription, String value) {

        //m=<media> <port> <proto> <fmt> ...
        String [] mValues = value.split(SP);
        String mediaCandidate = mValues[0];
        final MediaDescription.MediaType mediaType = findMediaTypeFor(mediaCandidate);
        int port = Integer.parseInt(mValues[1]);
        final MediaDescription mediaDescription =
                new MediaDescription(mediaType, port, mValues[2], mValues[3]);

        sessionDescription.addMediaDescription(mediaDescription);
    }

    private static void decodeAttribute(SessionDescription sessionDescription, String value) {

        //a=<attribute>
        //a=<attribute>:<value>
        int separatorIndex = value.indexOf(':');
        Attribute attribute = null;
        if(separatorIndex == -1){
            attribute = new Attribute(value);
        }
        else if(separatorIndex<(value.length()-1)){

            attribute = new Attribute(
                    value.substring(0, separatorIndex), value.substring(separatorIndex+1));
        }
        else{
            return;
        }

        int numberOfMediaDescriptions = sessionDescription.getMediaDescriptions().size();
        if(numberOfMediaDescriptions>0){

            int lastMediaDescriptionIndex = numberOfMediaDescriptions-1;
            final MediaDescription mediaDescription = sessionDescription.getMediaDescriptions().get(lastMediaDescriptionIndex);
            mediaDescription.addAttribute(attribute);
        }
        else{

            sessionDescription.addAttribute(attribute);
        }
    }

    private static MediaDescription.MediaType findMediaTypeFor(String mediaCandidate) {

        for(MediaDescription.MediaType mediaType: MediaDescription.MediaType.values()){

            if(mediaType.toString().compareToIgnoreCase(mediaCandidate)==0){
                return mediaType;
            }
        }
        return null;
    }
}
