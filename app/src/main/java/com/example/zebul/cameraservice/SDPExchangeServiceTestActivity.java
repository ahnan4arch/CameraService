package com.example.zebul.cameraservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zebul.cameraservice.ice4j.SdpBuilder;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.harvest.StunCandidateHarvester;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SDPExchangeServiceTestActivity extends AppCompatActivity
implements Runnable{

    private static final String TAG = "SDPExchangeServiceTest";
    private Button startBuildSdpButton;
    private ProgressBar buildSdpProgressBar;
    private TextView sdpTextView;

    private Button sendUsingWebSocketButton;
    private TextView webSocketResponseTextView;
    //private StompClient mStompClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdpexchange_service_test);

        startBuildSdpButton = (Button)findViewById(R.id.startBuildSdpButton);
        buildSdpProgressBar = (ProgressBar)findViewById(R.id.buildSdpProgressBar);
        sdpTextView = (TextView)findViewById(R.id.sdpTextView);

        startBuildSdpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startBuildSdp();
            }
        });

        sendUsingWebSocketButton = (Button)findViewById(R.id.sendUsingWebSocketButton);
        sendUsingWebSocketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUsingWebSocket();
            }
        });
        webSocketResponseTextView = (TextView)findViewById(R.id.webSocketResponseTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{

            /*
            mStompClient = over(WebSocket.class, "ws://192.168.1.106:8888");
            mStompClient.lifecycle().subscribe(lifecycleEvent -> {
                switch (lifecycleEvent.getType()) {

                    case OPENED:
                        Log.d(TAG, "Stomp connection opened");
                        break;

                    case ERROR:
                        Log.e(TAG, "Error", lifecycleEvent.getException());
                        break;

                    case CLOSED:
                        Log.d(TAG, "Stomp connection closed");
                        break;
                }
            });
            mStompClient.connect();

            mStompClient.topic("/topic/greetings").subscribe(topicMessage -> {

                final String payload = topicMessage.getPayload();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        webSocketResponseTextView.setText(payload);
                    }
                });
            });*/
        }
        catch(Exception exc){

            Log.e(TAG, "Error", exc);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void startBuildSdp() {

        buildSdpProgressBar.setVisibility(View.VISIBLE);
        new Thread(this).start();
    }

    private void sendUsingWebSocket() {

    }

    @Override
    public void run() {

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

        IceMediaStream stream = agent.createMediaStream("audio");
        int port = 5000;
        try {
            agent.createComponent(stream, Transport.UDP, port, port, port+100);
            SdpBuilder sdpBuilder = new SdpBuilder();
            final String localPeerSdpMessage = sdpBuilder.buildSDPDescription(agent);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    sdpTextView.setText(localPeerSdpMessage);
                    buildSdpProgressBar.setVisibility(View.GONE);
                }
            });


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
