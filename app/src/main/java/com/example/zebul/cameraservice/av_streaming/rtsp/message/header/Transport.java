package com.example.zebul.cameraservice.av_streaming.rtsp.message.header;

public class Transport {

	public enum TransportProtocol{
		
		RTP,
		UNSPECIFIED,
	}
	
	public enum Profile{
		
		AVP,
		UNSPECIFIED,
	}

	public enum LowerTransport{
		
		UDP,
		TCP,
		UNSPECIFIED,
	}
	
	public enum TransmissionType{
		
		unicast,
		multicast,
		UNSPECIFIED,
	}
	
	public enum Mode{
		
		PLAY,
		RECORD,
		UNSPECIFIED,
	}
	
	private TransportProtocol transportProtocol	= TransportProtocol.UNSPECIFIED;
	private Profile profile						= Profile.UNSPECIFIED;
	private LowerTransport lowerTransport		= LowerTransport.UNSPECIFIED;
	private TransmissionType transmissionType	= TransmissionType.UNSPECIFIED;
	private Mode mode							= Mode.UNSPECIFIED;
	private String destination;
	private String source;
	private int minClientPort;
	private int maxClientPort;
	private int minServerPort;
	private int maxServerPort;
	private String ssrc;
	private int timeToLive;
	
	public TransmissionType getTransmissionType() {
		return transmissionType;
	}
	public void setTransmissionType(TransmissionType transmissionType) {
		this.transmissionType = transmissionType;
	}
	public LowerTransport getLowerTransport() {
		return lowerTransport;
	}
	public void setLowerTransport(LowerTransport lowerTransport) {
		this.lowerTransport = lowerTransport;
	}
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getMinClientPort() {
		return minClientPort;
	}
	public int getMaxClientPort() {
		return maxClientPort;
	}
	public void setClientPortRange(int minClientPort, int maxClientPort) {
		this.minClientPort = minClientPort;
		this.maxClientPort = maxClientPort;
	}
	public int getMinServerPort() {
		return minServerPort;
	}
	public int getMaxServerPort() {
		return maxServerPort;
	}
	public void setServerPortRange(int minServerPort, int maxServerPort) {
		this.minServerPort = minServerPort;
		this.maxServerPort = maxServerPort;
	}
	
	public TransportProtocol getTransportProtocol() {
		return transportProtocol;
	}
	
	public void setTransportProtocol(TransportProtocol transportProtocol) {
		this.transportProtocol = transportProtocol;
	}
	
	public Profile getProfile(){
		
		return profile;
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public String getSsrc() {
		return ssrc;
	}
	public void setSsrc(String ssrc) {
		this.ssrc = ssrc;
	}

	public int getTimeToLive() {
		
		return timeToLive;
	}
	public void setTimeToLive(int timeToLive) {
		
		this.timeToLive = timeToLive;
	}
}
