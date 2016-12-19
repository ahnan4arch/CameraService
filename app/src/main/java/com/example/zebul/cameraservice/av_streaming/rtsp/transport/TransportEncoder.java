package com.example.zebul.cameraservice.av_streaming.rtsp.transport;


public class TransportEncoder {

	public static String encode(Transport transport){
		
		StringBuilder transportTextBuilder = new StringBuilder();
		
		String transportSpec = String.format("%s/%s", transport.getTransportProtocol(), transport.getProfile());
		Transport.LowerTransport lowerTransport = transport.getLowerTransport();
		if(!lowerTransport.equals(Transport.LowerTransport.UNSPECIFIED)){
			transportSpec += "/"+lowerTransport.toString();
		}
		transportTextBuilder.append(transportSpec+";");
		
		transportTextBuilder.append(transport.getTransmissionType()+";");
		
		String destination = String.format("destination=%s", transport.getDestination());
		transportTextBuilder.append(destination+";");
		
		String clientPort = ""; 
		if(transport.getMinClientPort()==transport.getMaxClientPort()){
			clientPort = String.format("client_port=%d", transport.getMinClientPort());
		}
		else{
			clientPort = String.format("client_port=%d-%d", transport.getMinClientPort(), transport.getMaxClientPort());
		}
		transportTextBuilder.append(clientPort+";");
		
		String serverPort = ""; 
		if(transport.getMinServerPort()==transport.getMaxServerPort()){
			serverPort = String.format("server_port=%d", transport.getMinServerPort());
		}
		else{
			serverPort = String.format("server_port=%d-%d", transport.getMinServerPort(), transport.getMaxServerPort());
		}
		transportTextBuilder.append(serverPort+";");
		
		String ssrc = String.format("ssrc=%s", transport.getSsrc());
		transportTextBuilder.append(ssrc+";");
		
		String mode = String.format("mode=\"%s\"", transport.getMode().toString());
		transportTextBuilder.append(mode);
		
		return transportTextBuilder.toString();
	}
}
