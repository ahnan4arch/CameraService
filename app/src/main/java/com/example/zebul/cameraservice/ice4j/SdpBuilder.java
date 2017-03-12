package com.example.zebul.cameraservice.ice4j;

import android.javax.sdp.Attribute;
import android.javax.sdp.Connection;
import android.javax.sdp.MediaDescription;
import android.javax.sdp.Origin;
import android.javax.sdp.SdpConstants;
import android.javax.sdp.SdpException;
import android.javax.sdp.SdpFactory;
import android.javax.sdp.SessionDescription;

import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.Candidate;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;

import java.util.List;
import java.util.Vector;

import static org.ice4j.ice.sdp.IceSdpUtils.ICE_OPTIONS;
import static org.ice4j.ice.sdp.IceSdpUtils.ICE_OPTION_TRICKLE;
import static org.ice4j.ice.sdp.IceSdpUtils.ICE_PWD;
import static org.ice4j.ice.sdp.IceSdpUtils.ICE_UFRAG;
import static org.ice4j.ice.sdp.IceSdpUtils.MID;

/**
 * Created by zebul on 3/6/17.
 */

public class SdpBuilder {

    private SdpFactory sdpFactory;
    public String buildSDPDescription(Agent agent) throws Throwable
    {
        sdpFactory = SdpFactory.getInstance();
        SessionDescription sdess = sdpFactory.createSessionDescription();
        initSessionDescription(sdess, agent);
        return sdess.toString();
    }

    private void initSessionDescription(SessionDescription sDes, Agent agent)
            throws IllegalArgumentException, SdpException {
        //now add ICE options
        StringBuilder allOptionsBuilder = new StringBuilder();

        //if(agent.supportsTrickle())
        allOptionsBuilder.append(ICE_OPTION_TRICKLE).append(" ");

        String allOptions = allOptionsBuilder.toString().trim();

        try
        {
            if (allOptions.length() > 0)
            {
                //get the attributes so that we could easily modify them
                Vector<Attribute> sessionAttributes = sDes.getAttributes(true);

                sessionAttributes.add(
                        sdpFactory.createAttribute(ICE_OPTIONS, allOptions));
            }

            //take care of the origin: first extract one of the default
            // addresses so that we could set the origin.
            TransportAddress defaultAddress = agent.getStreams().get(0)
                    .getComponent(Component.RTP).getDefaultCandidate()
                    .getTransportAddress();

            String addressFamily = defaultAddress.isIPv6()
                    ? Connection.IP6
                    : Connection.IP4;

            //origin
            Origin o = sDes.getOrigin();

            if( o == null || "user".equals(o.getUsername()))
            {
                //looks like there wasn't any origin set: jain-sdp creates a
                //default origin that has "user" as the user name so we use this
                //to detect it. it's quite hacky but couldn't fine another way.
                o = sdpFactory.createOrigin("ice4j.org", 0, 0, "IN",
                        addressFamily, defaultAddress.getHostAddress());
            }
            else
            {
                //if an origin existed, we just make sure it has the right
                // address now and are care ful not to touch anything else.
                o.setAddress(defaultAddress.getHostAddress());
                o.setAddressType(addressFamily);
            }

            sDes.setOrigin(o);

            //m lines
            List<IceMediaStream> streams = agent.getStreams();
            Vector<MediaDescription> mDescs = new Vector<MediaDescription>(
                    agent.getStreamCount());
            for(IceMediaStream stream : streams)
            {
                MediaDescription mLine = sdpFactory.createMediaDescription(
                        stream.getName(), 0, //default port comes later
                        1, SdpConstants.RTP_AVP, new int[]{0});

                initMediaDescription(mLine, stream);

                mDescs.add(mLine);
            }

            sDes.setMediaDescriptions(mDescs);
        }
        catch (SdpException exc)
        {
            //this shouldn't happen but let's rethrow an SDP exception just
            //in case.
            throw new IllegalArgumentException(
                    "Something went wrong when setting ICE options",
                    exc);
        }

        //first set credentials
        setIceCredentials(sDes, agent.getLocalUfrag(), agent.getLocalPassword());
    }

    private void initMediaDescription(MediaDescription mediaDescription,
                                            IceMediaStream   iceMediaStream)
    {
        try
        {
            //set mid-s
            mediaDescription.setAttribute(MID, iceMediaStream.getName());

            Component firstComponent = null;

            //add candidates
            for(Component component : iceMediaStream.getComponents())
            {
                //if this is the first component, remember it so that we can
                //later use it for default candidates.
                if(firstComponent == null)
                    firstComponent = component;

                for(Candidate<?> candidate : component.getLocalCandidates())
                {
                    mediaDescription.addAttribute(
                            new CandidateAttribute(candidate));
                }
            }

            //set the default candidate
            TransportAddress defaultAddress = firstComponent
                    .getDefaultCandidate().getTransportAddress();

            mediaDescription.getMedia().setMediaPort(
                    defaultAddress.getPort());

            String addressFamily = defaultAddress.isIPv6()
                    ? Connection.IP6
                    : Connection.IP4;

            mediaDescription.setConnection(sdpFactory.createConnection(
                    "IN", defaultAddress.getHostAddress(), addressFamily));

            //now check if the RTCP port for the default candidate is different
            //than RTP.port +1, in which case we need to mention it.
            Component rtcpComponent
                    = iceMediaStream.getComponent(Component.RTCP);

            if( rtcpComponent != null )
            {
                TransportAddress defaultRtcpCandidate = rtcpComponent
                        .getDefaultCandidate().getTransportAddress();

                if(defaultRtcpCandidate.getPort() != defaultAddress.getPort()+1)
                {
                    mediaDescription.setAttribute(
                            "rtcp", Integer.toString(defaultRtcpCandidate.getPort()));
                }
            }
        }
        catch (SdpException exc)
        {
            //this shouldn't happen but let's rethrow an SDP exception just
            //in case.
            throw new IllegalArgumentException(
                    "Something went wrong when setting default candidates",
                    exc);
        }
    }

    private void setIceCredentials(SessionDescription sDes,
                                   String uFrag,
                                   String pwd)
            throws NullPointerException, SdpException {
        if (sDes == null || uFrag == null || pwd == null)
        {
            throw new NullPointerException(
                    "sDes, uFrag and pwd, cannot be null");
        }

        Vector<Attribute> sessionAttributes = sDes.getAttributes(true);

        //ice u-frag and password
        sessionAttributes.add(sdpFactory.createAttribute(ICE_UFRAG, uFrag));
        sessionAttributes.add(sdpFactory.createAttribute(ICE_PWD, pwd));

        sDes.setAttributes(sessionAttributes);
    }
}
