package com.example.zebul.cameraservice;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zebul.cameraservice.av_streaming.Resolution;
import com.example.zebul.cameraservice.av_streaming.VideoSettings;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.av_streaming.rtsp.URI;
import com.example.zebul.cameraservice.communication.client.RTPClientSessionController;
import com.example.zebul.cameraservice.communication.client.RTSPClientSession;
import com.example.zebul.cameraservice.communication.client.RTSPClientSessionController;
import com.example.zebul.cameraservice.communication.client.ClientSessionSettings;
import com.example.zebul.cameraservice.communication.udp.Event;
import com.example.zebul.cameraservice.packet_producers.video.H264PacketConsumer;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraVideoH264PacketProducer;
import com.example.zebul.cameraservice.packet_producers.video.file.AssetFileAVPacketProducer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class DisplayVideoFromRemoteSourceActivity extends AppCompatActivity
        implements TextureView.SurfaceTextureListener, H264PacketConsumer, Runnable {

    private static final int TIMEOUT_USEC = 15000;
    private static final String TAG = DisplayVideoFromRemoteSourceActivity.class.getSimpleName();
    private MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    private EditText rtspEditText;
    private Button connectButton;
    private TextView statusTextView;
    private TextView statisticTextView;
    private TextureView textureView;
    private MediaCodec mediaCodec;

    private RTSPClientSession rtspClientSession;
    private Thread feedCodecThread;
    private ManualResetEvent event = new ManualResetEvent(false);
    private List<H264Packet> h264Packets = new LinkedList<>();

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

        feedCodecThread = new Thread(this);
        feedCodecThread.start();
    }

    private void connect() {

        try {

            final Resolution res = Resolution._640x480;
            MediaFormat format = MediaFormat.createVideoFormat(
                    CameraVideoH264PacketProducer.MIME_TYPE, res.getWidth(), res.getHeight());

            format.setInteger(MediaFormat.KEY_BIT_RATE, VideoSettings.DEFAULT_BIT_RATE);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, VideoSettings.DEFAULT_FRAME_RATE);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            SurfaceTexture st = textureView.getSurfaceTexture();
            Surface surface = new Surface(st);

            mediaCodec = MediaCodec.createDecoderByType(CameraVideoH264PacketProducer.MIME_TYPE);
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();

            URI uri = URI.fromString(rtspEditText.getText().toString());

            ClientSessionSettings sessionSettings = new ClientSessionSettings("CameraService", uri, 4555, 4556);
            RTPClientSessionController rtpClientSessionController =
                    new RTPClientSessionController(sessionSettings, this);
            RTSPClientSessionController rtspClientSessionController =
                    new RTSPClientSessionController(sessionSettings, rtpClientSessionController);
            rtspClientSession =
                    new RTSPClientSession(uri, rtspClientSessionController, rtspClientSessionController);
            rtspClientSessionController.begin();
            rtspClientSession.start();

            /*
            final AssetFileAVPacketProducer filePacketProducer =
                    new AssetFileAVPacketProducer(this, "H264_artifacts_motion.h264");
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try{

                        while(true){

                            final H264Packets h264Packets = filePacketProducer.produceH264Packets();
                            for(H264Packet h264Packet: h264Packets){

                                consumeH264Packet(h264Packet);
                            }
                        }
                    }
                    catch(Exception e){

                        int foo = 1;
                        int bar = foo;
                    }
                }
            }).start();
            */
        } catch (MalformedURLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    public void run(){

        while(true){

            List<H264Packet> currentH264Packets = null;
            synchronized (this){
                currentH264Packets = h264Packets;
                h264Packets = new LinkedList<>();
            }

            for(H264Packet h264Packet: currentH264Packets){

                processH264Packet(h264Packet);
            }

            try {
                event.waitOne();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void consumeH264Packet(H264Packet h264Packet) {

        synchronized (this) {
            h264Packets.add(h264Packet);
        }
        event.set();
    }

    private void processH264Packet(H264Packet h264Packet) {

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

        int inputBufIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
        if (inputBufIndex >= 0) {

            byte [] nalUnitData = nalUnit.getData();
            ByteBuffer[] decoderInputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
            inputBuf.put(nalUnitData, 0, nalUnitData.length);
            mediaCodec.queueInputBuffer(inputBufIndex, 0, nalUnitData.length,
                    timestamp.getTimestampInMillis(), 0 /*flags*/);
        }
        else{

            int foo = 0;
            int bar = foo;
        }

        int decoderStatus = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
        if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // no output available yet
            Log.d(TAG, "no output from mediaCodec available");
        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            // not important for us, since we're using Surface
            Log.d(TAG, "mediaCodec output buffers changed");
        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            MediaFormat newFormat = mediaCodec.getOutputFormat();
            Log.d(TAG, "mediaCodec output format changed: " + newFormat);
        } else if (decoderStatus < 0) {
            throw new RuntimeException(
                    "unexpected result from mediaCodec.dequeueOutputBuffer: " +
                            decoderStatus);
        } else { // decoderStatus >= 0

            Log.d(TAG, "surface mediaCodec given buffer " + decoderStatus +
                    " (size=" + bufferInfo.size + ")");
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.d(TAG, "output EOS");
            }

            boolean doRender = (bufferInfo.size != 0);

            // As soon as we call releaseOutputBuffer, the buffer will be forwarded
            // to SurfaceTexture to convert to a texture.  We can't control when it
            // appears on-screen, but we can manage the pace at which we release
            // the buffers.
            /*
            if (doRender && frameCallback != null) {
                frameCallback.preRender(bufferInfo.presentationTimeUs);
            }*/

            mediaCodec.releaseOutputBuffer(decoderStatus, doRender);

            /*
            if (doRender && frameCallback != null) {
                frameCallback.postRender();
            }

            if (doLoop) {
                Log.d(TAG, "Reached EOS, looping");
                extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                inputDone = false;
                mediaCodec.flush();    // reset mediaCodec state
                frameCallback.loopReset();
            }*/
        }
    }
}
