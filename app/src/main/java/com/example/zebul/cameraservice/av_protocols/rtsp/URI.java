package com.example.zebul.cameraservice.av_protocols.rtsp;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zebul on 12/11/16.
 */

public class URI {

    private URL url;

    public URI(){
    }

    private URI(URL url){

        this.url = url;
    }

    public static URI fromString(String uriString) throws MalformedURLException {

        URL url = new URL(uriString.replace("rtsp", "http"));
        return new URI(url);
    }

    public String getHost(){

        return url.getHost();
    }

    public int getPort(){

        return url.getPort();
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
