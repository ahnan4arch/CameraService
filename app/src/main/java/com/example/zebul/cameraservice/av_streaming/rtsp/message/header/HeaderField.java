package com.example.zebul.cameraservice.av_streaming.rtsp.message.header;

public class HeaderField {

	private String name;
	private String value;
	
	public HeaderField(String name, String value){
		
		this.name = name; 
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isEmpty(){
		
		return name.length()==0; 
	}
}
