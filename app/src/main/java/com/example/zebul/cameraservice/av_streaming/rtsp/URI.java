package com.example.zebul.cameraservice.av_streaming.rtsp;

import java.net.URL;

/**
 * Created by zebul on 12/11/16.
 */

public class URI {

    private int trackId = -1;

    public URI(String uriString){

        fromString(uriString);
    }

    public void fromString(String uriString){

        String [] uriTokens = uriString.split("/");
        for(String uriToken: uriTokens){

            String [] nameValuePair = uriToken.split("=");
            if(nameValuePair.length==2){

                setParameter(nameValuePair[0], nameValuePair[1]);
            }
        }
    }

    private void setParameter(String paramName, String paramValue) {

        if(paramName.toLowerCase().equals("trackid")){
            trackId = Integer.parseInt(paramValue);
        }
    }

    @Override
    public String toString(){

        return "";
    }

    public int getTrackId() {
        return trackId;
    }
}
