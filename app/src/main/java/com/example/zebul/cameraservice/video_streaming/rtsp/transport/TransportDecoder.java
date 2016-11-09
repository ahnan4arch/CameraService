package com.example.zebul.cameraservice.video_streaming.rtsp.transport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TransportDecoder {

	private static Pattern PORT_PATTERN_RANGE = Pattern.compile(".*_port=(\\d+)-(\\d+)", Pattern.CASE_INSENSITIVE);
	private static Pattern PORT_PATTERN = Pattern.compile(".*_port=(\\d+)", Pattern.CASE_INSENSITIVE);
	
	/*
	Transport           =    "Transport" ":"
                            1\#transport-spec
	transport-spec      =    transport-protocol/profile[/lower-transport]
                            *parameter
	transport-protocol  =    "RTP"
	profile             =    "AVP"
	lower-transport     =    "TCP" | "UDP"
	parameter           =    ( "unicast" | "multicast" )
                       |    ";" "destination" [ "=" address ]
                       |    ";" "interleaved" "=" channel [ "-" channel ]
                       |    ";" "append"
                       |    ";" "ttl" "=" ttl
                       |    ";" "layers" "=" 1*DIGIT
                       |    ";" "port" "=" port [ "-" port ]
                       |    ";" "client_port" "=" port [ "-" port ]
                       |    ";" "server_port" "=" port [ "-" port ]
                       |    ";" "ssrc" "=" ssrc
                       |    ";" "mode" = <"> 1\#mode <">
	ttl                 =    1*3(DIGIT)
	port                =    1*5(DIGIT)
	ssrc                =    8*8(HEX)
	channel             =    1*3(DIGIT)
	address             =    host
	mode                =    <"> *Method <"> | Method 
	*/
	
	public static Transport decode(String transportAsText){
		
		Transport transport = new Transport();
		String [] transportTokens = transportAsText.split(";");
		
		if(transportTokens.length == 0){
			return transport;
		}
		
		String transportSpec = transportTokens[0];
		String [] transportSpecTokens = transportSpec.split("/");
		for(int i=0; i<transportSpecTokens.length; i++){
			String transportSpecToken = transportSpecTokens[i];
			switch(i){
			case 0:
				Transport.TransportProtocol transportProtocol = Transport.TransportProtocol.valueOf(transportSpecToken);
				transport.setTransportProtocol(transportProtocol);
				break;
			case 1:
				Transport.Profile profile = Transport.Profile.valueOf(transportSpecToken);
				transport.setProfile(profile);
				break;
			case 2:
				Transport.LowerTransport lowerTransport = Transport.LowerTransport.valueOf(transportSpecToken);
				transport.setLowerTransport(lowerTransport);
				break;	
			}
		}
		
		for(String transportToken: transportTokens){
			
			if(transportToken.equals(Transport.TransmissionType.multicast.toString())){
				
				transport.setTransmissionType(Transport.TransmissionType.multicast);
			}
			else if(transportToken.equals(Transport.TransmissionType.unicast.toString())){
				
				transport.setTransmissionType(Transport.TransmissionType.unicast);
			}
			else if(transportToken.startsWith("client_port=")){
				
				Matcher matcher = PORT_PATTERN_RANGE.matcher(transportToken);
				if(matcher.find()){
					
					int minClientPort = Integer.parseInt(matcher.group(1));
					int maxClientPort = Integer.parseInt(matcher.group(2));
					transport.setClientPortRange(minClientPort, maxClientPort);
				}
				else{
					
					matcher = PORT_PATTERN.matcher(transportToken);
					if(matcher.find()){
						
						int minClientPort = Integer.parseInt(matcher.group(1));
						transport.setClientPortRange(minClientPort, minClientPort);
					}
				}
			}
			else if(transportToken.startsWith("server_port=")){
				
				Matcher matcher = PORT_PATTERN_RANGE.matcher(transportToken);
				if(matcher.find()){
					
					int minServerPort = Integer.parseInt(matcher.group(1));
					int maxServerPort = Integer.parseInt(matcher.group(2));
					transport.setServerPortRange(minServerPort, maxServerPort);
				}
				else{
					
					matcher = PORT_PATTERN.matcher(transportToken);
					if(matcher.find()){
						
						int minServerPort = Integer.parseInt(matcher.group(1));
						transport.setServerPortRange(minServerPort, minServerPort);
					}
				}
			}
			else if(transportToken.startsWith("mode=")){
				
				String modeAsText = transportToken.substring("mode=".length(), transportToken.length()).toLowerCase();
				for(Transport.Mode mode: Transport.Mode.values()){
					
					if(mode.toString().toLowerCase().equals(modeAsText)){
						transport.setMode(mode);
						break;
					}
					else if(("\""+mode.toString().toLowerCase()+"\"").equals(modeAsText)){
						transport.setMode(mode);
						break;
					}
				}
			}
			else if(transportToken.startsWith("ssrc=")){
				
				String ssrc = transportToken.substring("ssrc=".length(), transportToken.length()).toLowerCase();
				transport.setSsrc(ssrc);
			}
			else if(transportToken.startsWith("ttl=")){
				
				String ttl = transportToken.substring("ttl=".length(), transportToken.length()).toLowerCase();
				transport.setTimeToLive(Integer.parseInt(ttl));
			}
		}
		return transport;
	}
}
