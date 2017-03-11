package com.example.zebul.cameraservice.av_protocols.sdp;


import com.example.zebul.cameraservice.av_protocols.rtsp.RTSPProtocol;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class SessionDescription {

	private String identifier;
	private List<MediaDescription> mediaDescriptions = new LinkedList<MediaDescription>();
	private List<Attribute> attributes = new LinkedList<Attribute>();
	public int version;
	public String name;
	public String information;

	public SessionDescription(int destinationPort){
		
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
		
		return SessionDescriptionProtocol.encode(this);
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
