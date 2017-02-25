package com.example.zebul.cameraservice.av_protocols.sdp;


import com.example.zebul.cameraservice.av_protocols.rtsp.RTSPProtocol;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class SessionDescription {

	private long timestamp;
	private int destinationPort;
	private int mediaFormatDescription = 96;
	
	private String identifier;

	private List<MediaDescription> mediaDescriptions = new LinkedList<MediaDescription>();
	private List<Attribute> attributes = new LinkedList<Attribute>();
	public int version;

	public String name;

	public SessionDescription(int destinationPort){
		
		long uptime = System.currentTimeMillis();
		timestamp = (uptime/1000)<<32 & (((uptime-((uptime/1000)*1000))>>32)/1000); // NTP timestamp
		this.destinationPort = destinationPort;
		identifier = generateIdentifier();
	}

	public SessionDescription(){

	}

	public List<MediaDescription> getMediaDescriptions() {

		return mediaDescriptions;
	}

	public void addMediaDescription(MediaDescription mediaDescription){

		mediaDescriptions.add(mediaDescription);
	}


	/*
	SessionDescription Identifiers

	SessionDescription identifiers are opaque strings of arbitrary length. Linear
	white space must be URL-escaped. A session identifier MUST be chosen
	randomly and MUST be at least eight octets long to make guessing it
	more difficult.
	session-id   =   1*( ALPHA | DIGIT | safe ) 
	*/
	
	public static String generateIdentifier() {
		
		Random random = new Random();
		return String.format("%05X%05X", random.nextInt(), random.nextInt());
	}
	
	public String getIdentifier(){
		
		return identifier;
	}
	
	public String getDescription(){
		
		StringBuilder descriptionBuilder = new StringBuilder();
		
		descriptionBuilder.append(SessionDescriptionProtocol.encode(this));
		descriptionBuilder.append(i_formatSessionDescription()	+RTSPProtocol.LINE_SEPARATOR);
		descriptionBuilder.append(c_formatConnectionData()		+RTSPProtocol.LINE_SEPARATOR);
		descriptionBuilder.append(t_formatTime()				+RTSPProtocol.LINE_SEPARATOR);

        final String SP = SessionDescriptionProtocol.SP;
        for(MediaDescription md: mediaDescriptions){

            final String mediaType = md.getMediaType().toString().toLowerCase();
            final String mediaDescription = mediaType+SP+md.getPort()+SP+md.getProtocol()+SP+md.getFormat();
            final String desc = "m="+mediaDescription+SessionDescriptionProtocol.LINE_SEPARATOR;
            descriptionBuilder.append(desc);
            for(Attribute attribute: md.getAttributes()){

                String type = attribute.getType();
                String value = attribute.getValue();
                if(value.length()>0){
                    descriptionBuilder.append("a="+type+":"+value+SessionDescriptionProtocol.LINE_SEPARATOR);
                }
                else{
                    descriptionBuilder.append("a="+type+SessionDescriptionProtocol.LINE_SEPARATOR);
                }
            }
            /*
            final MediaDescription.MediaType mediaType = findMediaTypeFor(mediaCandidate);
            int port = Integer.parseInt(mValues[1]);
            final MediaDescription mediaDescription =
            new MediaDescription(mediaType, port, mValues[2], mValues[3]);
            */

            //mediaDescription.SP
        }
		
		String description = descriptionBuilder.toString();
		return description;
	}
	
	private String m_formatMediaDescriptions(MediaDescription.MediaType media) {
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
		
		destinationPort = 0;
		String proto = "RTP/AVP";
		String mediaDescriptions = String.format("m=%s %d %s %d", media.toString().toLowerCase(),
				destinationPort, proto, mediaFormatDescription);
		return mediaDescriptions;
	}
	
	private String t_formatTime() {
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

	private String c_formatConnectionData(){
		
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
	
	private String i_formatSessionDescription(){
		
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
		String sessionDescription = "i=N/A";
		return sessionDescription;
	}


    public String getUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConceptOfUserIdsSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	public void addAttribute(Attribute attribute) {

		attributes.add(attribute);
	}

	public boolean hasInfoAboutVideoMedia() {

		return hasInfoAboutMediaType(MediaDescription.MediaType.Video);
	}

	public boolean hasInfoAboutAudioMedia() {

		return hasInfoAboutMediaType(MediaDescription.MediaType.Audio);
	}

	private boolean hasInfoAboutMediaType(MediaDescription.MediaType mediaType) {

		for(MediaDescription md: mediaDescriptions) {
			if (md.getMediaType().equals(mediaType)) {
				return true;
			}
		}
		return false;
	}

	public boolean videoMediaHasValueOfAttribute(String attributeType, String value) {

		return mediaHasValueOfAttribute(MediaDescription.MediaType.Video, attributeType, value);
	}

	public boolean audioMediaHasValueOfAttribute(String attributeType, String value) {

		return mediaHasValueOfAttribute(MediaDescription.MediaType.Audio, attributeType, value);
	}

	public boolean mediaHasValueOfAttribute(
			MediaDescription.MediaType mediaType, String attributeType, String value) {

		String foundValue = findMediaValueOfAttribute(mediaType, attributeType);
		if(foundValue == null) {

			return false;
		}
		return (foundValue.compareToIgnoreCase(value) == 0);
	}

	public String findVideoMediaValueOfAttribute(String attributeType){

		return findMediaValueOfAttribute(MediaDescription.MediaType.Video, attributeType);
	}

	public String findAudioMediaValueOfAttribute(String attributeType){

		return findMediaValueOfAttribute(MediaDescription.MediaType.Audio, attributeType);
	}

	public String findMediaValueOfAttribute(
			MediaDescription.MediaType mediaType, String attributeType) {

		for(MediaDescription md: mediaDescriptions){
			if(md.getMediaType().equals(mediaType)){
				for(Attribute attribute: md.getAttributes()){
					if(attribute.getType().compareToIgnoreCase(attributeType) == 0){
						return attribute.getValue();
					}
				}
			}
		}
		return null;
	}

}
