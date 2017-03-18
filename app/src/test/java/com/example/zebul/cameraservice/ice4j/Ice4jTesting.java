package com.example.zebul.cameraservice.ice4j;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.harvest.StunCandidateHarvester;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by bartek on 19.02.17.
 */

public class Ice4jTesting {

    @Test
    public void test_creation_of_sdp_with_candidaes(){

        Agent agent = new Agent();
        String[] hostnames = new String[] {"jitsi.org", "numb.viagenie.ca", "stun.ekiga.net"};

        for(String hostname: hostnames) {
            try {
                TransportAddress address;

                address = new TransportAddress(InetAddress.getByName(hostname), 3478, Transport.UDP);
                agent.addCandidateHarvester(new StunCandidateHarvester(address));
            } catch (UnknownHostException ex) {

                int foo = 1;
                int bar = foo;
                //Logger.getLogger(SimpleStun.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        IceMediaStream audioStream = agent.createMediaStream("audio");
        IceMediaStream videoStream = agent.createMediaStream("video");
        int audioPort = 5000;
        int videoPort = 6000;
        try {
            agent.createComponent(audioStream, Transport.UDP, audioPort, audioPort, audioPort+100);
            agent.createComponent(videoStream, Transport.UDP, videoPort, videoPort, videoPort+100);
            SdpBuilder sdpBuilder = new SdpBuilder();
            String localPeerSdpMessage = sdpBuilder.buildSDPDescription(agent);

            int foo = 1;
            int bar = foo;
            // The three last arguments are: preferredPort, minPort, maxPort
        } catch (IllegalArgumentException | IOException ex) {

            int foo = 1;
            int bar = foo;
            
            //Logger.getLogger(SimpleStun.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}
