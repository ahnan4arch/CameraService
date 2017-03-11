package com.example.zebul.cameraservice.av_protocols.sdp;

/**
 * Created by zebul on 1/26/17.
 */

public class SessionDescriptionProtocol {

    public static final String LINE_SEPARATOR = "\r\n";

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
                case 'i'://session information
                    decodeSessionInformation(sessionDescription, value);
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
        sdBuilder.append(version+LINE_SEPARATOR);

        String origin = encodeOrigin(sd);
        sdBuilder.append(origin+LINE_SEPARATOR);

        String sessionName = encodeSessionName(sd);
        sdBuilder.append(sessionName+LINE_SEPARATOR);

        String sessionInformation = encodeSessionInformation(sd);
        sdBuilder.append(sessionInformation+LINE_SEPARATOR);

        String connectionData = encodeConnectionData(sd);
        sdBuilder.append(connectionData+LINE_SEPARATOR);

        String time = encodeTime(sd);
        sdBuilder.append(time+LINE_SEPARATOR);

        for(MediaDescription md: sd.getMediaDescriptions()){

            sdBuilder.append(encodeMediaDescription(md)+LINE_SEPARATOR);
            for(Attribute attribute: md.getAttributes()){

                sdBuilder.append(encodeAttribute(attribute)+LINE_SEPARATOR);
            }
        }

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
        return String.format("o=%s %s %d %s %s %s", userName, sessionId, ver, networkType, addressType, address);
    }

    private static void decodeOrigin(SessionDescription sd, String value){

    }

    private static String encodeVersion(SessionDescription sd){

        /*
        Protocol Version
        The "v=" field gives the version of the SessionDescription Description Protocol.
        There is no minor version number.
        */
        return String.format("v=%d", sd.version);
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
        String sessionName = String.format("s=%s", sd.name);
        return sessionName;
    }

    private static void decodeSessionName(SessionDescription sd, String value) {

        sd.name = value;
    }

    private static String encodeSessionInformation(SessionDescription sd) {

        /*
		i=<session description>

		The "i=" field is information about the session.  There may be at
		most one session-level "i=" field per session description, and at
		most one "i=" field per media. Although it may be omitted, this is
		discouraged for session announcements, and user interfaces for
		composing sessions should require text to be entered.  If it is
		present it must contain ISO 10646 characters (but see also the
		`charset' attribute below).

		A single "i=" field can also be used for each media definition.  In
		media definitions, "i=" fields are primarily intended for labeling
		media streams. As such, they are most likely to be useful when a
		*/
        String sessionInformation = "i=N/A";
        return sessionInformation;
    }

    private static void decodeSessionInformation(SessionDescription sd, String value) {

        sd.information = value;
    }

    private static String encodeConnectionData(SessionDescription sd){

		/*
		c=<network type> <address type> <connection address>

		The "c=" field contains connection data.

		A session announcement must contain one "c=" field in each media
		description (see below) or a "c=" field at the session-level.  It may
		contain a session-level "c=" field and one additional "c=" field per
		media description, in which case the per-media values override the
		session-level settings for the relevant media.

		The first sub-field is the network type, which is a text string
		giving the type of network.  Initially "IN" is defined to have the
		meaning "Internet".

		The second sub-field is the address type.  This allows SDP to be used
		for sessions that are not IP based.  Currently only IP4 is defined.

		The third sub-field is the connection address.  Optional extra
		subfields may be added after the connection address depending on the
		value of the <address type> field.

		For IP4 addresses, the connection address is defined as follows:

		Typically the connection address will be a class-D IP multicast
		group address.  If the session is not multicast, then the
		connection address contains the fully-qualified domain name or the
		unicast IP address of the expected data source or data relay or
		data sink as determined by additional attribute fields. It is not
		expected that fully-qualified domain names or unicast addresses
		will be given in a session description that is communicated by a
		multicast announcement, though this is not prohibited.  If a
		unicast data stream is to pass through a network address
		translator, the use of a fully-qualified domain name rather than an
		unicast IP address is RECOMMENDED.  In other cases, the use of an
		IP address to specify a particular interface on a multi-homed host
		might be required.  Thus this specification leaves the decision as
		to which to use up to the individual application, but all
		applications MUST be able to cope with receiving both formats.

		Conferences using an IP multicast connection address must also have
		a time to live (TTL) value present in addition to the multicast
		address.  The TTL and the address together define the scope with
		which multicast packets sent in this conference will be sent. TTL
		values must be in the range 0-255.

		The TTL for the session is appended to the address using a slash as
		a separator.  An example is:

		                       c=IN IP4 224.2.1.1/127

		Hierarchical or layered encoding schemes are data streams where the
		encoding from a single media source is split into a number of
		layers.  The receiver can choose the desired quality (and hence
		bandwidth) by only subscribing to a subset of these layers.  Such
		layered encodings are normally transmitted in multiple multicast
		groups to allow multicast pruning.  This technique keeps unwanted
		traffic from sites only requiring certain levels of the hierarchy.
		For applications requiring multiple multicast groups, we allow the
		following notation to be used for the connection address:

		        <base multicast address>/<ttl>/<number of addresses>

		If the number of addresses is not given it is assumed to be one.
		Multicast addresses so assigned are contiguously allocated above
		the base address, so that, for example:

		                      c=IN IP4 224.2.1.1/127/3

		would state that addresses 224.2.1.1, 224.2.1.2 and 224.2.1.3 are
		to be used at a ttl of 127.  This is semantically identical to
		including multiple "c=" lines in a media description:

		                       c=IN IP4 224.2.1.1/127
		                       c=IN IP4 224.2.1.2/127
		                       c=IN IP4 224.2.1.3/127
		Multiple addresses or "c=" lines can only be specified on a per-
		media basis, and not for a session-level "c=" field.

		It is illegal for the slash notation described above to be used for
		IP unicast addresses.
		*/

        String connectionData = String.format("c=IN IP4 %s", "0.0.0.0");
        return connectionData;
    }

    private static String encodeTime(SessionDescription sd) {
		/*
		Time of availability
		Times, Repeat Times and Time Zones
		t=<doStart time>  <stop time>
		*/

        String startTime = "0";
        String stopTime = "0";
        String time = String.format("t=%s %s", startTime, stopTime);
        return time;
    }

    private static String encodeMediaDescription(MediaDescription mediaDescription) {
        /*
        m=<media> <port> <proto> <fmt> ...

        A session description may contain a number of media descriptions.
        Each media description starts with an "m=" field and is terminated by
        either the next "m=" field or by the end of the session description.
        A media field has several sub-fields:

        <media> is the media type.  Currently defined media are "audio",
        "video", "text", "application", and "message", although this list
        may be extended in the future (see Section 8).

        <port> is the videoTransport port to which the media stream is sent.  The
        meaning of the videoTransport port depends on the network being used as
        specified in the relevant "c=" field, and on the videoTransport
        protocol defined in the <proto> sub-field of the media field.
        Other ports used by the media application (such as the RTP Control
        Protocol (RTCP) port [19]) MAY be derived algorithmically from the
        base media port or MAY be specified in a separate attribute (for
        example, "a=rtcp:" as defined in [22]).

        If non-contiguous ports are used or if they don't follow the
        parity rule of even RTP ports and odd RTCP ports, the "a=rtcp:"
        attribute MUST be used.  Applications that are requested to send
        media to a <port> that is odd and where the "a=rtcp:" is present
        MUST NOT subtract 1 from the RTP port: that is, they MUST send the
        RTP to the port indicated in <port> and send the RTCP to the port
        indicated in the "a=rtcp" attribute.

        For applications where hierarchically encoded streams are being
        sent to a unicast address, it may be necessary to specify multiple
        videoTransport ports.  This is done using a similar notation to that
        used for IP multicast addresses in the "c=" field:

        m=<media> <port>/<number of ports> <proto> <fmt> ...

        In such a case, the ports used depend on the videoTransport protocol.
        For RTP, the default is that only the even-numbered ports are used
        for data with the corresponding one-higher odd ports used for the
        RTCP belonging to the RTP session, and the <number of ports>
        denoting the number of RTP sessions.  For example:

        m=video 49170/2 RTP/AVP 31

        would specify that ports 49170 and 49171 form one RTP/RTCP pair
        and 49172 and 49173 form the second RTP/RTCP pair.  RTP/AVP is the
        videoTransport protocol and 31 is the format (see below).  If non-
        contiguous ports are required, they must be signalled using a
        separate attribute (for example, "a=rtcp:" as defined in [22]).

        If multiple addresses are specified in the "c=" field and multiple
        ports are specified in the "m=" field, a one-to-one mapping from
        port to the corresponding address is implied.  For example:

        c=IN IP4 224.2.1.1/127/2
        m=video 49170/2 RTP/AVP 31

        would imply that address 224.2.1.1 is used with ports 49170 and
        49171, and address 224.2.1.2 is used with ports 49172 and 49173.

        The semantics of multiple "m=" lines using the same videoTransport
        address are undefined.  This implies that, unlike limited past
        practice, there is no implicit grouping defined by such means and
        an explicit grouping framework (for example, [18]) should instead
        be used to express the intended semantics.

        <proto> is the videoTransport protocol.  The meaning of the videoTransport
        protocol is dependent on the address type field in the relevant
        "c=" field.  Thus a "c=" field of IP4 indicates that the videoTransport
        protocol runs over IP4.  The following videoTransport protocols are
        defined, but may be extended through registration of new protocols
        with IANA (see Section 8):

        *  udp: denotes an unspecified protocol running over UDP.

        *  RTP/AVP: denotes RTP [19] used under the RTP Profile for Audio
        and Video Conferences with Minimal Control [20] running over
        UDP.

        *  RTP/SAVP: denotes the Secure Real-time Transport Protocol [23]
        running over UDP.

        The main reason to specify the videoTransport protocol in addition to
        the media format is that the same standard media formats may be
        carried over different videoTransport protocols even when the network
        protocol is the same -- a historical example is vat Pulse Code
        Modulation (PCM) audio and RTP PCM audio; another might be TCP/RTP
        PCM audio.  In addition, relays and monitoring tools that are
        videoTransport-protocol-specific but format-independent are possible.

        <fmt> is a media format description.  The fourth and any subsequent
        sub-fields describe the format of the media.  The interpretation
        of the media format depends on the value of the <proto> sub-field.

        If the <proto> sub-field is "RTP/AVP" or "RTP/SAVP" the <fmt>
        sub-fields contain RTP payload type numbers.  When a list of
        payload type numbers is given, this implies that all of these
        payload formats MAY be used in the session, but the first of these
        formats SHOULD be used as the default format for the session.  For
        dynamic payload type assignments the "a=rtpmap:" attribute (see
        Section 6) SHOULD be used to map from an RTP payload type number
        to a media encoding name that identifies the payload format.  The
        "a=fmtp:"  attribute MAY be used to specify format parameters (see
        Section 6).

        If the <proto> sub-field is "udp" the <fmt> sub-fields MUST
        reference a media type describing the format under the "audio",
        "video", "text", "application", or "message" top-level media
        types.  The media type registration SHOULD define the packet
        format for use with UDP videoTransport.

        For media using other videoTransport protocols, the <fmt> field is
        protocol specific.  Rules for interpretation of the <fmt> sub-
        field MUST be defined when registering new protocols (see Section
        8.2.2).

        m=<media> <port> <proto> <fmt> ...
		*/

        String mediaDescriptions = String.format("m=%s %d %s %s",
                mediaDescription.getMediaType().toString().toLowerCase(),
                mediaDescription.getPort(),
                mediaDescription.getProtocol(),
                mediaDescription.getFormat());
        return mediaDescriptions;
    }

    private static void decodeMediaDescription(SessionDescription sessionDescription, String value) {

        //m=<media> <port> <proto> <fmt> ...
        String [] mValues = value.split(" ");
        String mediaCandidate = mValues[0];
        final MediaDescription.MediaType mediaType = findMediaTypeFor(mediaCandidate);
        int port = Integer.parseInt(mValues[1]);
        final MediaDescription mediaDescription =
                new MediaDescription(mediaType, port, mValues[2], mValues[3]);

        sessionDescription.addMediaDescription(mediaDescription);
    }

    private static String encodeAttribute(Attribute attribute) {

        String type = attribute.getType();
        String value = attribute.getValue();
        if(value.length()>0){
            return "a="+type+":"+value;
        }
        else{
            return "a="+type;
        }
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
