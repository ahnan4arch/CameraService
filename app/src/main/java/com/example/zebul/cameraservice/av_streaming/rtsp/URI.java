package com.example.zebul.cameraservice.av_streaming.rtsp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zebul on 12/11/16.
 */

public class URI {

    private URL url;

    public URI(){
    }

    public static URI decodeFromString(String uriString) throws MalformedURLException {

        URI uri = new URI();
        uri.fromString(uriString);
        return uri;
    }

    public void fromString(String uriString) throws MalformedURLException {

        url = new URL(uriString.replace("rtsp", "http"));
    }

    @Override
    public String toString(){

        return url.toString().replace("http", "rtsp");
    }

    public int getTrackId() {

        if(url == null){
            return -1;
        }
        String paramValue = getParameterValue("trackid");
        if(paramValue != null){
            return Integer.parseInt(paramValue);
        }
        return -1;
    }

    private String getParameterValue(String parameterName) {

        String [] pathTokens = url.getPath().split("&");
        for(String pathToken: pathTokens){

            String [] nameValuePair = pathToken.split("=");
            if(nameValuePair.length==2){

                String name = nameValuePair[0].replace("/", "");
                if(name.compareToIgnoreCase(parameterName) == 0){
                    return nameValuePair[1];
                }
            }
        }
        return null;
    }
}
