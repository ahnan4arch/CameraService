package com.example.zebul.cameraservice.ice4j;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bartek on 16.03.17.
 */

public class ClientConnections {

    public static String STREAM_NAME_VIDEO = "video";
    public static String STREAM_NAME_AUDIO = "audio";
    public static String STREAM_NAME_CONTROL = "control";

    public static String [] STREAM_NAMES =
            new String[]{STREAM_NAME_VIDEO, STREAM_NAME_AUDIO, STREAM_NAME_CONTROL};

    private Map<String, ClientStreamConnection> mapOfClientStreamConnections = new HashMap<>();

    public void putClientStreamConnection(String streamName,
                                          ClientStreamConnection clientStreamConnection) {

        mapOfClientStreamConnections.put(streamName, clientStreamConnection);
    }

    public ClientStreamConnection getClientStreamConnection(String streamName) {

        return mapOfClientStreamConnections.get(streamName);
    }
}
