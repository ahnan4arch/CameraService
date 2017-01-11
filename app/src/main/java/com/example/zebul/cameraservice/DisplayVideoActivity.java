package com.example.zebul.cameraservice;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.packet_producers.PacketProductionException;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraVideoH264PacketProducer;
import com.example.zebul.cameraservice.packet_producers.video.file.AssetFileAVPacketProducer;

public class DisplayVideoActivity extends AppCompatActivity
implements Runnable, TextureView.SurfaceTextureListener, MoviePlayer.PlayerFeedback {

    private TextureView textureView;
    private Button stopPlayingButton;
    private Button startPlayingButton;

    private boolean keepPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_video);

        startPlayingButton = (Button)findViewById(R.id.startPlayingButton);
        stopPlayingButton = (Button)findViewById(R.id.stopPlayingButton);
        textureView = (TextureView)findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);

        startPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying();
            }
        });

        stopPlayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });
    }

    private void startPlaying() {

        keepPlaying = true;
        new Thread(this).start();
    }

    private void stopPlaying() {

        keepPlaying = false;
    }


    @Override
    public void run() {

        MediaCodec mediaCodec = null;
        try {

            MediaFormat format = MediaFormat.createVideoFormat(
                    CameraVideoH264PacketProducer.MIME_TYPE,
                    704, 400);

            format.setInteger(MediaFormat.KEY_BIT_RATE, 400000);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            SurfaceTexture st = textureView.getSurfaceTexture();
            Surface surface = new Surface(st);

            mediaCodec = MediaCodec.createDecoderByType(CameraVideoH264PacketProducer.MIME_TYPE);
            mediaCodec.configure(format, surface, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();

            final AssetFileAVPacketProducer filePacketProducer =
                    new AssetFileAVPacketProducer("H264_artifacts_motion.h264");
            while(keepPlaying){

                pumpDataFromFileToSurface(filePacketProducer, mediaCodec);
            }
        }
        catch(Exception exc){

            int foo = 1;
            int bar = foo;
        }
        finally {

            if(mediaCodec != null){

                mediaCodec.release();
            }
        }
    }

    private void pumpDataFromFileToSurface(
            AssetFileAVPacketProducer filePacketProducer,
            MediaCodec mediaCodec) throws PacketProductionException {

        final H264Packets h264Packets = filePacketProducer.produceH264Packets();

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
    public void playbackStopped() {

    }
}
