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

    public String getFile() {
        return url.getFile();
    }

    public String getFileWithoutSpecialLeadingChars() {
        String file = getFile();
        if(file == null){
            return null;
        }
        if(file.startsWith("/")){

            return file.substring(1);
        }
        return file;
    }
}
