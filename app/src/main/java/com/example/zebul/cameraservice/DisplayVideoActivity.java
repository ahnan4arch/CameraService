package com.example.zebul.cameraservice;

import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.H264Packets;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.packet_producers.PacketProductionException;
import com.example.zebul.cameraservice.packet_producers.video.camera.CameraVideoH264PacketProducer;
import com.example.zebul.cameraservice.packet_producers.video.file.AssetFileAVPacketProducer;

import java.nio.ByteBuffer;

public class DisplayVideoActivity extends AppCompatActivity
implements Runnable, TextureView.SurfaceTextureListener, MoviePlayer.PlayerFeedback {

    private static final String TAG = DisplayVideoActivity.class.getSimpleName();
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
            mediaCodec.configure(format, surface, null, 0);
            mediaCodec.start();

            final AssetFileAVPacketProducer filePacketProducer =
                    new AssetFileAVPacketProducer(this, "H264_artifacts_motion.h264");
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

    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private void pumpDataFromFileToSurface(
            AssetFileAVPacketProducer filePacketProducer,
            MediaCodec decoder) throws PacketProductionException {

        final int TIMEOUT_USEC = 10000;
        final H264Packets h264Packets = filePacketProducer.produceH264Packets();
        for(H264Packet h264Packet: h264Packets){

            final NALUnit nalUnit = h264Packet.getNALUnit();
            byte [] nalUnitData = nalUnit.getData();

            int inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
            if (inputBufIndex >= 0) {

                ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
                ByteBuffer inputBuf = decoderInputBuffers[inputBufIndex];
                inputBuf.put(nalUnitData, 0, nalUnitData.length);

                final Timestamp timestamp = h264Packet.getTimestamp();
                decoder.queueInputBuffer(inputBufIndex, 0, nalUnitData.length,
                        timestamp.getTimestampInMillis(), 0 /*flags*/);
            }
            else{

                int foo = 0;
                int bar = foo;
            }
        }

        int decoderStatus = decoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
        if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // no output available yet
            Log.d(TAG, "no output from decoder available");
        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            // not important for us, since we're using Surface
            Log.d(TAG, "decoder output buffers changed");
        } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            MediaFormat newFormat = decoder.getOutputFormat();
            Log.d(TAG, "decoder output format changed: " + newFormat);
        } else if (decoderStatus < 0) {
            throw new RuntimeException(
                    "unexpected result from decoder.dequeueOutputBuffer: " +
                            decoderStatus);
        } else { // decoderStatus >= 0

            Log.d(TAG, "surface decoder given buffer " + decoderStatus +
                    " (size=" + mBufferInfo.size + ")");
            if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                Log.d(TAG, "output EOS");
            }

            boolean doRender = (mBufferInfo.size != 0);

            // As soon as we call releaseOutputBuffer, the buffer will be forwarded
            // to SurfaceTexture to convert to a texture.  We can't control when it
            // appears on-screen, but we can manage the pace at which we release
            // the buffers.
            /*
            if (doRender && frameCallback != null) {
                frameCallback.preRender(mBufferInfo.presentationTimeUs);
            }*/

            decoder.releaseOutputBuffer(decoderStatus, doRender);

            /*
            if (doRender && frameCallback != null) {
                frameCallback.postRender();
            }

            if (doLoop) {
                Log.d(TAG, "Reached EOS, looping");
                extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
                inputDone = false;
                decoder.flush();    // reset decoder state
                frameCallback.loopReset();
            }*/
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
    public void playbackStopped() {

    }
}
