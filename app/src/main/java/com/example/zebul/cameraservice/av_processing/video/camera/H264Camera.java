package com.example.zebul.cameraservice.av_processing.video.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.example.zebul.cameraservice.av_processing.PacketProcessingExceptionListener;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.H264Packet;
import com.example.zebul.cameraservice.av_processing.video.H264PacketConsumer;
import com.example.zebul.cameraservice.av_protocols.rtp.Clock;
import com.example.zebul.cameraservice.av_protocols.rtp.Timestamp;
import com.example.zebul.cameraservice.av_protocols.rtp.h264.NALUnit;
import com.example.zebul.cameraservice.av_processing.PacketProcessingException;
import com.example.zebul.cameraservice.av_processing.MediaCodecPacketProcessor;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zebul on 12/1/16.
 */

public class H264Camera extends MediaCodecPacketProcessor {

    public static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final String TAG = "CameraPacketProducer";
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames
    private static final boolean VERBOSE = false;           // lots of logging

    private Camera camera;
    private SurfaceTextureManager surfaceTextureManager;

    private Clock clock = new Clock();

    private CodecInputSurface inputSurface;

    private SurfaceTexture surfaceTexture;
    private CameraSettings cameraSettings;

    private H264PacketConsumer h264PacketConsumer;

    public H264Camera(
            H264PacketConsumer h264PacketConsumer,
            PacketProcessingExceptionListener packetProcessingExceptionListener){

        super(packetProcessingExceptionListener);
        this.h264PacketConsumer = h264PacketConsumer;
        outputBufferTimeoutInUs = 500000;//configure to best possible
    }

    public boolean start(CameraSettings cameraSettings) {

        this.cameraSettings = cameraSettings;
        return super.start();
    }

    @Override
    protected void open() throws PacketProcessingException {

        prepareCamera();
        prepareEncoder();
        inputSurface.makeCurrent();
        prepareSurfaceTexture();
        camera.startPreview();
        surfaceTexture = surfaceTextureManager.getSurfaceTexture();
    }

    @Override
    protected void process() throws PacketProcessingException, InterruptedException{

        // Feed any pending encoder output into the muxer.
        processMediaCodecOutput();

        // Acquire a new frame of input, and render it to the Surface.  If we had a
        // GLSurfaceView we could switch EGL contexts and call drawImage() a second
        // time to render it on screen.  The texture can be shared between contexts by
        // passing the GLSurfaceView's EGLContext as eglCreateContext()'s share_context
        // argument.
        surfaceTextureManager.awaitNewImage();
        surfaceTextureManager.drawImage();

        // Set the presentation time stamp from the SurfaceTexture's time stamp.  This
        // will be used by MediaMuxer to set the PTS in the video.
        inputSurface.setPresentationTime(surfaceTexture.getTimestamp());

        // Submit it to the encoder.  The eglSwapBuffers call will block if the input
        // is full, which would be bad if it stayed full until we dequeued an output
        // buffer (which we can't do, since we're stuck here).  So long as we fully drain
        // the encoder before supplying additional input, the system guarantees that we
        // can supply another frame without blocking.
        inputSurface.swapBuffers();
    }

    @Override
    protected void close() throws PacketProcessingException {

        super.close();
        if(inputSurface != null){

            inputSurface.release();
            inputSurface = null;
        }

        if(camera != null){

            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onOutputBufferAvailable(int outputBufferIndex, ByteBuffer outputBuffer) {

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
            outputBuffer.position(bufferInfo.offset);
            outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
            byte [] data = new byte[bufferInfo.size];
            outputBuffer.get(data, bufferInfo.offset, data.length);

            NALUnit nalUnit = new NALUnit(data);
            Timestamp timestamp = clock.getTimestamp();
            H264Packet h264Packet = new H264Packet(nalUnit, timestamp);
            h264PacketConsumer.consumeH264Packet(h264Packet);
        }

        mediaCodec.releaseOutputBuffer(outputBufferIndex, false);

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
        {
            // Stream is marked as done,
            // break out of while
            Log.d(TAG, "Marked EOS");
        }
    }

    private void prepareCamera() throws PacketProcessingException {

        if (camera != null) {
            throw new PacketProcessingException("camera already initialized");
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
            throw new PacketProcessingException("Unable to open camera");
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

    private void prepareEncoder() throws PacketProcessingException {

        try {
            VideoSettings videoSettings = cameraSettings.getVideoSettings();
            Resolution resolution = videoSettings.getResolution();
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE,
                    resolution.getWidth(), resolution.getHeight());

            // Set some properties.  Failing to specify some of these can cause the MediaCodec
            // open() call to throw an unhelpful exception.

            format.setInteger(MediaFormat.KEY_BIT_RATE, videoSettings.getBitRate());
            format.setInteger(MediaFormat.KEY_FRAME_RATE, videoSettings.getFrameRate());
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

            // Create a MediaCodec encoder, and open it with our format.  Get a Surface
            // we can use for input and wrap it with a class that handles the EGL work.
            //
            // If you want to have two EGL contexts -- one for display, one for recording --
            // you will likely want to defer instantiation of CodecInputSurface until after the
            // "display" EGL context is created, then modify the eglCreateContext call to
            // take eglGetCurrentContext() as the share_context argument.
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            inputSurface = new CodecInputSurface(mediaCodec.createInputSurface());
            mediaCodec.start();
        }
        catch(IOException exc_){

            throw new PacketProcessingException(exc_);
        }
    }

    private void prepareSurfaceTexture() {
        surfaceTextureManager = new SurfaceTextureManager();
        SurfaceTexture st = surfaceTextureManager.getSurfaceTexture();
        try {
            camera.setPreviewTexture(st);
        } catch (IOException ioe) {
            throw new RuntimeException("setPreviewTexture failed", ioe);
        }
    }
}
