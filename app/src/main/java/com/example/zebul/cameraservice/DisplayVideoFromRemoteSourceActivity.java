package com.example.zebul.cameraservice;

import android.graphics.SurfaceTexture;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zebul.cameraservice.av_processing.PacketProcessingException;
import com.example.zebul.cameraservice.av_processing.PacketProcessingExceptionListener;
import com.example.zebul.cameraservice.av_processing.audio.AudioSettings;
import com.example.zebul.cameraservice.av_processing.video.camera.Resolution;
import com.example.zebul.cameraservice.av_processing.video.camera.VideoSettings;
import com.example.zebul.cameraservice.av_processing.video.display.H264Display;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.av_protocols.rtsp.URI;
import com.example.zebul.cameraservice.communication.client.RTPClientSessionController;
import com.example.zebul.cameraservice.communication.client.RTSPClientSession;
import com.example.zebul.cameraservice.communication.client.RTSPClientSessionController;
import com.example.zebul.cameraservice.communication.client.ClientSessionSettings;
import com.example.zebul.cameraservice.av_processing.audio.speaker.AACSpeaker;
import com.example.zebul.cameraservice.av_processing.video.H264PacketConsumer;

import java.io.IOException;
import java.net.MalformedURLException;

public class DisplayVideoFromRemoteSourceActivity extends AppCompatActivity
        implements TextureView.SurfaceTextureListener, PacketProcessingExceptionListener
        , H264PacketConsumer {

    private static final String TAG = DisplayVideoFromRemoteSourceActivity.class.getSimpleName();

    private EditText rtspEditText;
    private Button connectButton;
    private TextView statusTextView;
    private TextView statisticTextView;
    private TextureView textureView;

    private RTSPClientSession rtspClientSession;

    private AACSpeaker aacSpeaker;
    private H264Display h264Display;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_video_from_remote_source);

        rtspEditText = (EditText) findViewById(R.id.rtspEditText);
        connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                connect();
            }
        });
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        statisticTextView = (TextView) findViewById(R.id.statisticTextView);
        textureView = (TextureView) findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPacketProductionException(PacketProcessingException exc) {

        int foo = 1;
        int bar = foo;
    }

    private void connect() {

        try {

            aacSpeaker = new AACSpeaker(this);
            AudioSettings audioSettings = AudioSettings.DEFAULT;
            aacSpeaker.start(audioSettings);

            SurfaceTexture st = textureView.getSurfaceTexture();
            Surface surface = new Surface(st);

            h264Display = new H264Display(this, surface);
            Resolution resolution = Resolution._640x480;
            int bitRate = VideoSettings.DEFAULT_BIT_RATE;
            int frameRate = VideoSettings.DEFAULT_FRAME_RATE;
            VideoSettings videoSettings = new VideoSettings(resolution, bitRate, frameRate);
            h264Display.start(videoSettings);

            URI uri = URI.fromString(rtspEditText.getText().toString());

            ClientSessionSettings sessionSettings = new ClientSessionSettings(
                    "CameraService", uri, 4555, 4556, 13221, 13222);
            RTPClientSessionController rtpClientSessionController =
                    new RTPClientSessionController(sessionSettings, this, aacSpeaker);
            RTSPClientSessionController rtspClientSessionController =
                    new RTSPClientSessionController(sessionSettings, rtpClientSessionController);
            rtspClientSession = new RTSPClientSession(uri, rtspClientSessionController, rtspClientSessionController);
            rtspClientSessionController.begin();

            rtspClientSession.start();

        } catch (MalformedURLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void consumeH264Packet(H264Packet h264Packet) {

        final NALUnit nalUnit = h264Packet.getNALUnit();
        final Timestamp timestamp = h264Packet.getTimestamp();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String stat = "Recv NALUnit len: "+nalUnit.getData().length+", timestamp: "+timestamp.getTimestampInMillis()+"\n";
                statisticTextView.append(stat);

                final int scrollAmount = statisticTextView.getLayout().getLineTop(statisticTextView.getLineCount()) - statisticTextView.getHeight();
                // if there is no need to scroll, scrollAmount will be <=0
                if (scrollAmount > 0)
                    statisticTextView.scrollTo(0, scrollAmount);
                else
                    statisticTextView.scrollTo(0, 0);
            }
        });

        h264Display.consumeH264Packet(h264Packet);
    }
}
