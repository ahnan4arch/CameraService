package com.example.zebul.cameraservice.av_streaming.rtsp.message.header;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class HeaderFields implements Iterable<HeaderField>{

	private List<HeaderField> headerFields = new LinkedList<HeaderField>();
	
	public void add(HeaderField headerField){
		
		headerFields.add(headerField);
	}

	@Override
	public Iterator<HeaderField> iterator() {
		
		return headerFields.iterator();
	}

	public HeaderField find(String fieldName) {
		
		String lowerCasedFieldName = fieldName.toLowerCase();
		for(HeaderField headerField: headerFields){
			
			if(headerField.getName().toLowerCase().equals(lowerCasedFieldName)){
				return headerField; 
			}
		}
		return new HeaderField("", "");
	}
}
