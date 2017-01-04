package com.example.zebul.cameraservice.packet_producers.video.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.example.zebul.cameraservice.ManualResetEvent;
import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionExceptionListener;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264Packet;
import com.example.zebul.cameraservice.av_streaming.av_packet.h264.H264PacketListener;
import com.example.zebul.cameraservice.av_streaming.rtp.Clock;
import com.example.zebul.cameraservice.av_streaming.rtp.Timestamp;
import com.example.zebul.cameraservice.av_streaming.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.av_streaming.av_packet.PacketProductionException;
import com.example.zebul.cameraservice.av_streaming.rtsp.video.Resolution;
import com.example.zebul.cameraservice.av_streaming.rtsp.video.VideoSettings;
import com.example.zebul.cameraservice.packet_producers.HardwarePacketProducer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by zebul on 12/1/16.
 */

public class CameraVideoH264PacketProducer extends HardwarePacketProducer {

    private static final String TAG = "CameraPacketProducer";
    private static final boolean VERBOSE = false;           // lots of logging

    private Camera camera;
    private SurfaceTextureManager mStManager;

    // Fragment shader that swaps color channels around.
    private static final String SWAPPED_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord).gbra;\n" +
                    "}\n";

    private Clock clock = new Clock();

    private MediaCodec mediaCodec;
    private CodecInputSurface inputSurface;

    private SurfaceTexture surfaceTexture;
    private CameraSettings cameraSettings;

    private H264PacketListener h264PacketListener;

    private MediaCodec.BufferInfo bufferInfo;
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames

    private String sessionName;

    public CameraVideoH264PacketProducer(
            H264PacketListener h264PacketListener,
            PacketProductionExceptionListener packetProductionExceptionListener){

        super(packetProductionExceptionListener);
        this.h264PacketListener = h264PacketListener;
    }

    public void start(CameraSettings cameraSettings, String sessionName) {

        this.cameraSettings = cameraSettings;
        this.sessionName = sessionName;
        LogMe("before engine.start");
        super.start();
        LogMe("after engine.start");
    }

    private void LogMe(String message) {

        Log.d(TAG, "Session-"+sessionName+": "+message);
    }

    @Override
    protected void open() throws PacketProductionException {

        LogMe("before prepareCamera");
        prepareCamera();
        LogMe("after prepareCamera");

        LogMe("before prepareEncoder");
        prepareEncoder();
        LogMe("after prepareEncoder");

        LogMe("before inputSurface.makeCurrent");
        inputSurface.makeCurrent();
        LogMe("after inputSurface.makeCurrent");

        LogMe("before prepareSurfaceTexture");
        prepareSurfaceTexture();
        LogMe("after prepareSurfaceTexture");

        LogMe("before startPreview");
        camera.startPreview();
        LogMe("after startPreview");

        LogMe("before getSurfaceTexture");
        surfaceTexture = mStManager.getSurfaceTexture();
        LogMe("after getSurfaceTexture");
    }

    private int frameCount = 0;

    @Override
    protected void produce() throws PacketProductionException, InterruptedException{

        // Feed any pending encoder output into the muxer.
        LogMe("before drainEncoder");
        drainEncoder();
        LogMe("after drainEncoder");

        // Switch up the colors every 15 frames.  Besides demonstrating the use of
        // fragment shaders for video editing, this provides a visual indication of
        // the frame rate: if the camera is capturing at 15fps, the colors will change
        // once per second.
        if ((frameCount % 15) == 0) {
            String fragmentShader = null;
            if ((frameCount & 0x01) != 0) {
                fragmentShader = SWAPPED_FRAGMENT_SHADER;
            }
            mStManager.changeFragmentShader(fragmentShader);
        }
        frameCount++;

        // Acquire a new frame of input, and render it to the Surface.  If we had a
        // GLSurfaceView we could switch EGL contexts and call drawImage() a second
        // time to render it on screen.  The texture can be shared between contexts by
        // passing the GLSurfaceView's EGLContext as eglCreateContext()'s share_context
        // argument.
        LogMe("before mStManager.awaitNewImage");
        mStManager.awaitNewImage();
        LogMe("after mStManager.awaitNewImage");

        LogMe("before mStManager.drawImage");
        mStManager.drawImage();
        LogMe("after mStManager.drawImage");


        // Set the presentation time stamp from the SurfaceTexture's time stamp.  This
        // will be used by MediaMuxer to set the PTS in the video.
        LogMe("before inputSurface.setPresentationTime");
        inputSurface.setPresentationTime(surfaceTexture.getTimestamp());
        LogMe("after inputSurface.setPresentationTime");

        // Submit it to the encoder.  The eglSwapBuffers call will block if the input
        // is full, which would be bad if it stayed full until we dequeued an output
        // buffer (which we can't do, since we're stuck here).  So long as we fully drain
        // the encoder before supplying additional input, the system guarantees that we
        // can supply another frame without blocking.

        LogMe("before inputSurface.swapBuffers");
        inputSurface.swapBuffers();
        LogMe("after inputSurface.swapBuffers");
    }

    @Override
    protected void close() throws PacketProductionException{

        if(mediaCodec != null){

            mediaCodec.release();
            mediaCodec = null;
        }

        if(inputSurface != null){

            inputSurface.release();
            inputSurface = null;
        }

        if(camera != null){

            camera.release();
            camera = null;
        }
    }

    private void drainEncoder() {

        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        while(true)
        {
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
            if (outputBufferIndex >= 0)
            {
                Log.d(TAG, "Queue Buffer out " + outputBufferIndex);
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0)
                {
                    // Config Bytes means SPS and PPS
                    Log.d(TAG, "Got config bytes");
                }

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_SYNC_FRAME) != 0)
                {
                    // Marks a Keyframe
                    Log.d(TAG, "Got Sync Frame");
                }

                if (bufferInfo.size != 0)
                {
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    outputBuffer.position(bufferInfo.offset);
                    outputBuffer.limit(bufferInfo.offset + bufferInfo.size);

                    byte [] data = new byte[bufferInfo.size];
                    outputBuffer.get(data, bufferInfo.offset, data.length);
                    //return bufferInfo.presentationTimeUs;

                    NALUnit nalUnit = new NALUnit(0, data);
                    //long timestampInMillis = bufferInfo.presentationTimeUs/1000;
                    Timestamp timestamp = clock.getTimestamp(); /*new Timestamp(timestampInMillis);*/
                    H264Packet h264Packet = new H264Packet(nalUnit, timestamp);

                    h264PacketListener.onH264Packet(h264Packet);
                }

                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                {
                    // Stream is marked as done,
                    // break out of while
                    Log.d(TAG, "Marked EOS");
                    break;
                }
            }
            else if(outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
            {
                outputBuffers = mediaCodec.getOutputBuffers();
                Log.d(TAG, "Output Buffer changed " + outputBuffers);
            }
            else if(outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
            {
                MediaFormat newFormat = mediaCodec.getOutputFormat();
                Log.d(TAG, "Media Format Changed " + newFormat);
            }
            else if(outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
            {
                // No Data, break out
                break;
            }
            else
            {
                // Unexpected State, ignore it
                Log.d(TAG, "Unexpected State " + outputBufferIndex);
            }
        }
    }

    private void prepareCamera() throws PacketProductionException {

        if (camera != null) {
            throw new PacketProductionException("camera already initialized");
        }

        Camera.CameraInfo info = new Camera.CameraInfo();

        // Try to find a front-facing camera (e.g. for videoconferencing).
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camera = Camera.open(i);
                break;
            }
        }
        if (camera == null) {
            Log.d(TAG, "No front-facing camera found; opening default");
            camera = Camera.open();    // opens first back-facing camera
        }
        if (camera == null) {
            throw new RuntimeException("Unable to open camera");
        }

        Camera.Parameters parms = camera.getParameters();

        VideoSettings videoSettings = cameraSettings.getVideoSettings();
        Resolution resolution = videoSettings.getResolution();
        choosePreviewSize(parms, resolution.getWidth(), resolution.getHeight());
        // leave the frame rate set to default
        camera.setParameters(parms);

        Camera.Size size = parms.getPreviewSize();
        Log.d(TAG, "Camera preview size is " + size.width + "x" + size.height);
    }
    /**
     * Attempts to find a preview size that matches the provided width and height (which
     * specify the dimensions of the encoded video).  If it fails to find a match it just
     * uses the default preview size.
     * <p>
     * TODO: should do a best-fit match.
     */
    private static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        // We should make sure that the requested MPEG size is less than the preferred
        // size, and has the same aspect ratio.
        Camera.Size ppsfv = parms.getPreferredPreviewSizeForVideo();
        if (VERBOSE && ppsfv != null) {
            Log.d(TAG, "Camera preferred preview size for video is " +
                    ppsfv.width + "x" + ppsfv.height);
        }

        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                parms.setPreviewSize(width, height);
                return;
            }
        }

        Log.w(TAG, "Unable to set preview size to " + width + "x" + height);
        if (ppsfv != null) {
            parms.setPreviewSize(ppsfv.width, ppsfv.height);
        }
    }

    private void prepareEncoder() throws PacketProductionException {

        try {
            bufferInfo = new MediaCodec.BufferInfo();

            VideoSettings videoSettings = cameraSettings.getVideoSettings();
            Resolution resolution = videoSettings.getResolution();
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE,
                    resolution.getWidth(), resolution.getHeight());

            // Set some properties.  Failing to specify some of these can cause the MediaCodec
            // open() call to throw an unhelpful exception.

            format.setInteger(MediaFormat.KEY_BIT_RATE, videoSettings.getBitRate());
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

            // Create a MediaCodec encoder, and open it with our format.  Get a Surface
            // we can use for input and wrap it with a class that handles the EGL work.
            //
            // If you want to have two EGL contexts -- one for display, one for recording --
            // you will likely want to defer instantiation of CodecInputSurface until after the
            // "display" EGL context is created, then modify the eglCreateContext call to
            // take eglGetCurrentContext() as the share_context argument.
            mediaCodec =
                    //MediaCodec.createByCodecName("OMX.Nvidia.h264.encoder");
                    MediaCodec.createEncoderByType(MIME_TYPE);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = new CodecInputSurface(mediaCodec.createInputSurface());
            mediaCodec.start();
        }
        catch(IOException exc_){

            throw new PacketProductionException(exc_);
        }
    }

    private void prepareSurfaceTexture() {
        mStManager = new SurfaceTextureManager();
        SurfaceTexture st = mStManager.getSurfaceTexture();
        try {
            camera.setPreviewTexture(st);
        } catch (IOException ioe) {
            throw new RuntimeException("setPreviewTexture failed", ioe);
        }
    }
}
