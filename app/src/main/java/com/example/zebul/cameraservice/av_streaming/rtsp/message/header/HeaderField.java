package com.example.zebul.cameraservice.av_streaming.rtsp.message.header;

public class HeaderField {

	public enum KnownName {

		Accept,
		Accept_Encoding,
		Accept_Language,
		Allow,
		Authorization,
		Bandwidth,
		Blocksize,
		Cache_Control,
		Conference,
		Connection,
		Content_Base,
		Content_Encoding,
		Content_Language,
		Content_Length,
		Content_Location,
		Content_Type,
		CSeq,
		Date,
		Expires,
		From,
		Host,
		If_Match,
		If_Modified_Since,
		Last_Modified,
		Location,
		Proxy_Authenticate,
		Proxy_Require,
		Public,
		Range,
		Referer,
		Retry_After,
		Require,
		RTP_Info,
		Scale,
		Speed,
		Server,
		Session,
		Timestamp,
		Transport,
		Unsupported,
		User_Agent,
		Vary,
		Via,
		WWW_Authenticate,
	}

	public static boolean isKnownFieldName(String nameOfFieldCandidate) {
		return findFieldKnownName(nameOfFieldCandidate)!=null;
	}

	public static KnownName findFieldKnownName(String fieldTypeNameCandidate) {

		KnownName[] knownNames = KnownName.values();
		String fieldTypeNameCandidateWithEnumPrefix = fieldTypeNameCandidate.replace('-', '_');
		for(KnownName knownName : knownNames){
			if(knownName.toString().compareToIgnoreCase(fieldTypeNameCandidateWithEnumPrefix)==0){
				return knownName;
			}
		}
		return null;
	}

	private String name;
	private String value;

	public HeaderField(KnownName knownName, String value) {

		this(knownNameToString(knownName), value);
	}

	public HeaderField(KnownName knownName, int value) {

		this(knownNameToString(knownName), value);
	}

	public HeaderField(String name, int value) {

		this(name, value+"");
	}

	public HeaderField(String name, String value) {

		this.name = name;
		this.value = value;
	}

	private static String knownNameToString(KnownName knownName){

		return knownName.toString().replace('_', '-');
	}
	
	public KnownName getKnownType() {

		return findFieldKnownName(name);
	}

	public boolean isWellKnownType() {
		return findFieldKnownName(name)!=null;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString(){

		return name+": "+value;
	}
}
