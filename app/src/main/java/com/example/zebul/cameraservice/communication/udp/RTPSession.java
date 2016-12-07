package com.example.zebul.cameraservice.communication.udp;

import android.content.res.AssetManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.example.zebul.cameraservice.AssetFileAVPacketProducer;
import com.example.zebul.cameraservice.CameraAVPacketProducer;
import com.example.zebul.cameraservice.CameraService;
import com.example.zebul.cameraservice.video_streaming.packetization.Packetizer;
import com.example.zebul.cameraservice.video_streaming.packetization.RTPPacketizationSession;
import com.example.zebul.cameraservice.video_streaming.rtp.Clock;
import com.example.zebul.cameraservice.video_streaming.rtp.RTPPacket;
import com.example.zebul.cameraservice.video_streaming.rtp.RTPPackets;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnit;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitHeader;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitHeaderDecoder;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitReader;
import com.example.zebul.cameraservice.video_streaming.rtp.header.RTPHeader;
import com.example.zebul.cameraservice.video_streaming.rtp.header.RTPHeaderEncoder;
import com.example.zebul.cameraservice.message.Message;
import com.example.zebul.cameraservice.video_streaming.rtp.nal_unit.NALUnitType;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPacket;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPacketProducer;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPackets;
import com.example.zebul.cameraservice.video_streaming.video_data.AVPacketProductionException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;


/**
 * Created by zebul on 10/23/16.
 */
public class RTPSession implements SocketMessageReceptionListener {

    private InetSocketAddress clientSocketAddress = null;
    private SocketEngine socketEngine;
    private Thread thread;
    private int sequenceNumber = 0;
    private int SSRC = 0;
    int prevTimestamp = -1;
    private byte[] pps;
    private byte[] sps;
    private byte[] stapa;
    public RTPSession(InetSocketAddress socketAddress){

        this.clientSocketAddress = socketAddress;
        socketEngine = new SocketEngine(socketAddress.getPort(), this);

        SSRC = new Random().nextInt();
    }

    public void start(){

        socketEngine.start();
        thread = new Thread(/*new Session()*//*new ZZZ()*/new AAA());
        thread.start();
    }

    public void stop(){

        socketEngine.stop();
        thread.interrupt();
    }

    @Override
    public void onSocketMessageReceived(Message message_) {

        int foo = 1;
        int bar = foo;
    }

    static class FrameData{

        public byte [] data;
        public long presentationTimeUs;
    }

    public void updateStapa() {

        stapa = new byte[sps.length + pps.length + 5];

        // STAP-A NAL header is 24
        stapa[0] = 24;

        // Write NALU 1 size into the array (NALU 1 is the SPS).
        stapa[1] = (byte) (sps.length >> 8);
        stapa[2] = (byte) (sps.length & 0xFF);

        // Write NALU 2 size into the array (NALU 2 is the PPS).
        stapa[sps.length + 3] = (byte) (pps.length >> 8);
        stapa[sps.length + 4] = (byte) (pps.length & 0xFF);

        // Write NALU 1 into the array, then write NALU 2 into the array.
        System.arraycopy(sps, 0, stapa, 3, sps.length);
        System.arraycopy(pps, 0, stapa, 5 + sps.length, pps.length);
    }

    byte [] readMovieData(String assetFileName){

        try {
            AssetManager assetManager = CameraService.CAMERA_SERVICE.getAssets();
            InputStream inputStream = assetManager.open(assetFileName);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int readCount = 0;
            byte[] data = new byte[1024];
            while ((readCount = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, readCount);
            }
            buffer.flush();
            return buffer.toByteArray();

        } catch (FileNotFoundException exc_) {
            exc_.printStackTrace();
            return null;
        } catch (IOException exc_) {
            exc_.printStackTrace();
            return null;
        }
    }

    class AAA implements Runnable{

        private static final String TAG = "ZZZ";

        AVPacketProducer avPacketProducer =
                //new AssetFileAVPacketProducer("H264_artifacts_motion.h264");
                new CameraAVPacketProducer();

        RTPPacketizationSession rtpPacketizationSession = new RTPPacketizationSession();
        @Override
        public void run() {

            try {

                while (true) {

                    packetize();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch(AVPacketProductionException exc_){

                int foo = 1;
                int bar = foo;
            }
        }

        private void packetize() throws AVPacketProductionException {

            AVPackets avPackets = avPacketProducer.produceAVPackets();
            Log.d(TAG, "produced: "+avPackets.getNumberOfPackets()+" avPackets");
            RTPPackets rtpPackets = rtpPacketizationSession.createRTPPackests(avPackets);
            Log.d(TAG, "created: "+rtpPackets.getNumberOfPackets()+" rtpPackets");
            int count = rtpPackets.getNumberOfPackets();
            for (RTPPacket rtpPacket : rtpPackets) {

                byte [] rtpPacketBytes = rtpPacket.toBytes();
                Log.d(TAG, rtpPacketBytes.length +" bytes will be sent");
                Message message = new Message(clientSocketAddress, rtpPacketBytes);
                socketEngine.post(message);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ZZZ implements Runnable{

        private static final String TAG = "ZZZ";

        AssetFileAVPacketProducer videoDataPackProducer =
                new AssetFileAVPacketProducer("H264_artifacts_motion.h264");

        @Override
        public void run() {

            try {

                while (true) {

                    packetize();
                }
            }
            catch(AVPacketProductionException exc_){

                int foo = 1;
                int bar = foo;
            }
        }

        private void packetize() throws AVPacketProductionException {

            AVPackets videoDataPack = videoDataPackProducer.produceAVPackets();
            for (AVPacket videoData : videoDataPack.getAVPackets()) {

                NALUnit nalUnit = videoData.getNALUnit();

                if ((nalUnit.getBeg() == -1) || (nalUnit.getEnd() == -1)) {
                    return;
                }

                byte[] nalBuffer = nalUnit.getData();
                if (nalBuffer.length <= 4) {

                    continue;
                }

                int timestamp = (int) videoData.getTimestamp().getTimestampInMillis();

                Packetizer packetizer = new Packetizer();
                Log.d(TAG, "Before packetizer.makePackets nalBuffer.len: " + nalBuffer.length);
                List<byte[]> headerlessRTPPackets = packetizer.makePackets(nalBuffer, NALUnit.START_CODES.length);
                Log.d(TAG, "After packetizer.makePackets");

                for (int i = 0; i < headerlessRTPPackets.size(); i++) {

                    boolean isLast = (i + 1) == headerlessRTPPackets.size();
                    boolean markerBit = isLast ? true : false;
                    byte payloadType = 96;
                    RTPHeader rtpHeader = new RTPHeader(
                            markerBit, payloadType, sequenceNumber++, timestamp, SSRC);
                    byte[] encodedRtpHeader = RTPHeaderEncoder.encode(rtpHeader);
                    byte[] rtpPacket = headerlessRTPPackets.get(i);
                    System.arraycopy(encodedRtpHeader, 0, rtpPacket, 0, RTPHeader.LENGTH);
                    Message message = new Message(clientSocketAddress, rtpPacket);

                    NALUnitHeader nalUnitHeader = NALUnitHeaderDecoder.decode(nalBuffer[4]);
                    NALUnitType nalUnitType = NALUnitType.NAL_UNIT_TYPES[nalUnitHeader.getNALUnitType()];
                    Log.d(TAG, "marker " + markerBit + " timestamp: " + timestamp +
                            ", nal unit type:" + (int) nalUnitHeader.getNALUnitType() + " (" + nalUnitType + ")" +
                            ", NRI:" + (int) nalUnitHeader.getNALReferenceIndicator() +
                            ", packet len:" + rtpPacket.length);

                    socketEngine.post(message);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class SSS implements Runnable{

        private static final String TAG = "SSS";
        private Clock clock = new Clock();

        @Override
        public void run() {
            byte [] movieData = readMovieData("H264_artifacts_motion.h264"/*"sample_iPod.m4v"*/);
            NALUnitReader reader = new NALUnitReader(movieData);
            clock.restart();

            while(true){

                NALUnit nalUnit = reader.read();
                if((nalUnit.getBeg()==-1)||(nalUnit.getEnd()==-1)){
                    break;
                }

                int timestamp = (int)clock.getTimestamp().getTimestampInMillis();
                byte [] nalBuffer = nalUnit.getData();

                if(nalBuffer.length <= 4){

                    continue;
                }
                /*
                int i = 0;
                if(
                        (nalBuffer[i]==0x00) && (nalBuffer[i+1]==0x00) &&
                                (nalBuffer[i+2]==0x00) && (nalBuffer[i+3]==0x01)
                        )
                {
                    //if(ucpInBuffer[i+4] & 0x0F ==0x07)
                    if(nalBuffer[i+4] == 0x67 || nalBuffer[i+4] == 0x27)
                    {
                        int foo = 1;
                        int bar = foo;
                    }
                }
                byte [] str = new byte[50];
                System.arraycopy(nalBuffer, 0, str, 0, Math.min(nalBuffer.length, str.length));
                Log.d(TAG, new String(str));
                */
                Packetizer packetizer = new Packetizer();
                Log.d(TAG, "Before packetizer.makePackets nalBuffer.len: "+nalBuffer.length);
                List<byte[]> headerlessRTPPackets = packetizer.makePackets(nalBuffer, NALUnit.START_CODES.length);
                Log.d(TAG, "After packetizer.makePackets");

                for(int i=0; i<headerlessRTPPackets.size(); i++){

                    boolean isLast = (i+1)== headerlessRTPPackets.size();
                    boolean markerBit = isLast ? true : false;
                    byte payloadType = 96;
                    RTPHeader rtpHeader = new RTPHeader(
                            markerBit, payloadType, sequenceNumber++, timestamp, SSRC);
                    byte [] encodedRtpHeader = RTPHeaderEncoder.encode(rtpHeader);
                    byte [] RTPPacket = headerlessRTPPackets.get(i);
                    System.arraycopy(encodedRtpHeader, 0, RTPPacket, 0, RTPHeader.LENGTH);
                    Message message = new Message(clientSocketAddress, RTPPacket);


                    NALUnitHeader nalUnitHeader = NALUnitHeaderDecoder.decode(nalBuffer[4]);
                    NALUnitType nalUnitType = NALUnitType.NAL_UNIT_TYPES[nalUnitHeader.getNALUnitType()];
                    Log.d(TAG, "marker " + markerBit + " timestamp: "+timestamp+
                            ", nal unit type:"+(int)nalUnitHeader.getNALUnitType()+" ("+nalUnitType+")"+
                            ", NRI:"+(int)nalUnitHeader.getNALReferenceIndicator()+
                            ", packet len:"+RTPPacket.length);

                    socketEngine.post(message);
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Session implements Runnable{


        boolean sssent = false;
        @Override
        public void run() {

            try{

                init();
                while(!Thread.interrupted()){
                //for(int i=0; i<30; i++){

                    FrameData frameData = fetchFrameData();

                    if(frameData != null){

                        byte [] nalBuffer = frameData.data;
                        Packetizer packetizer = new Packetizer();
                        List<byte[]> headerlessRTPPackets = packetizer.makePackets(nalBuffer, NALUnit.START_CODES.length);

                        int timestamp = (int)((frameData.presentationTimeUs/1000000.0f)*Clock.DEFAULT_CLOCK_RATE);
                        for(int i=0; i<headerlessRTPPackets.size(); i++){

                            boolean isLast = (i+1)== headerlessRTPPackets.size();
                            boolean markerBit = isLast ? true : false;
                            byte payloadType = 96;
                            RTPHeader rtpHeader = new RTPHeader(
                                    markerBit, payloadType, sequenceNumber++, timestamp, SSRC);
                            byte [] encodedRtpHeader = RTPHeaderEncoder.encode(rtpHeader);
                            byte [] RTPPacket = headerlessRTPPackets.get(i);
                            System.arraycopy(encodedRtpHeader, 0, RTPPacket, 0, RTPHeader.LENGTH);
                            Message message = new Message(clientSocketAddress, RTPPacket);
                            socketEngine.post(message);
                            byte nalUnitType = (byte)(nalBuffer[4]&0x1F);
                            Log.d(TAG, "marker " + markerBit + " timestamp: "+timestamp+", nal unit type:"+(int)nalUnitType);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                        /*
                        byte [] payload = frameData.data;
                        int coef = 4;
                        byte [] data = new byte[RTPHeader.LENGTH+(payload.length-coef)];

                        boolean markerBit = true;
                        byte payloadType = 96;
                        long presentation = frameData.presentationTimeUs*1000;
                        int timestamp = (int)((presentation/100L)*(90000L/1000L)/10000L);//(int)Clock.computeTimestamp(frameData.presentationTimeUs);

                        RTPHeader rtpHeader = new RTPHeader(
                                markerBit, payloadType, sequenceNumber++, timestamp, SSRC);
                        //NALUnitHeader nalUnitHeader = new NALUnitHeader();

                        byte [] encodedRTPHeader = RTPHeaderEncoder.encode(rtpHeader);

                        byte nalUnitType = (byte)(payload[4]&0x1F);
                        if(nalUnitType==5){
                            if(!sssent) {
                                updateParameters(payload);
                                updateStapa();
                                byte[] buffer = new byte[RTPHeader.LENGTH + stapa.length];
                                System.arraycopy(encodedRTPHeader, 0, buffer, 0, encodedRTPHeader.length);
                                System.arraycopy(stapa, 0, buffer, RTPHeader.LENGTH, stapa.length);
                                Message message = new Message(clientSocketAddress, buffer);
                                socketEngine.post(message);
                                sssent = true;
                            }
                        }


                        System.arraycopy(encodedRTPHeader, 0, data, 0, encodedRTPHeader.length);
                        //data[encodedRTPHeader.length] = payload[4];

                        // Parses the NAL unit type


                        System.arraycopy(payload, coef, data, encodedRTPHeader.length, payload.length-coef);

                        if (VERBOSE) Log.d(TAG, "marker " + markerBit + " timestamp: "+timestamp+", nal unit type:"+(int)nalUnitType);

                        Message message = new Message(clientSocketAddress, data);
                        socketEngine.post(message);
                        */
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException exc) {
                        exc.printStackTrace();
                    }
                }
                uninit();
            }
            catch(Exception exc){

                exc.printStackTrace();
            }
        }

        private void updateParameters(byte [] outData){

            ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
            if (spsPpsBuffer.getInt() == 0x00000001) {
                System.out.println("parsing sps/pps");
            } else {
                System.out.println("something is amiss?");
            }
            int ppsIndex = 4;
            /*
            while(!(spsPpsBuffer.get() == 0x00 && spsPpsBuffer.get() == 0x00 && spsPpsBuffer.get() == 0x00 && spsPpsBuffer.get() == 0x01)) {

            }
            ppsIndex = spsPpsBuffer.position();
            */
            sps = new byte[ppsIndex - 8];
            System.arraycopy(outData, 4, sps, 0, sps.length);
            pps = new byte[outData.length - ppsIndex];
            System.arraycopy(outData, ppsIndex, pps, 0, pps.length);

        }

        private void init() throws IOException {

            mWidth = 320;
            mHeight = 240;
            mBitRate = 2000000;

            mBufferInfo = new MediaCodec.BufferInfo();

            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);

            // Set some properties.  Failing to specify some of these can cause the MediaCodec
            // configure() call to throw an unhelpful exception.
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
            if (VERBOSE) Log.d(TAG, "format: " + format);

            // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
            // we can use for input and wrap it with a class that handles the EGL work.
            //
            // If you want to have two EGL contexts -- one for display, one for recording --
            // you will likely want to defer instantiation of CodecInputSurface until after the
            // "display" EGL context is created, then modify the eglCreateContext call to
            // take eglGetCurrentContext() as the share_context argument.
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mInputSurface = new CodecInputSurface(mEncoder.createInputSurface());
            mEncoder.start();

            // Output filename.  Ideally this would use Context.getFilesDir() rather than a
            // hard-coded output directory.
            final File OUTPUT_DIR = Environment.getExternalStorageDirectory();
            String outputPath = new File(OUTPUT_DIR,
                    "test." + mWidth + "x" + mHeight + ".mp4").toString();
            Log.d(TAG, "output file is " + outputPath);


            // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
            // because our MediaFormat doesn't have the Magic Goodies.  These can only be
            // obtained from the encoder after it has started processing data.
            //
            // We're not actually interested in multiplexing audio.  We just want to convert
            // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
            try {
                mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException ioe) {
                throw new RuntimeException("MediaMuxer creation failed", ioe);
            }

            mTrackIndex = -1;
            mMuxerStarted = false;
            mInputSurface.makeCurrent();
        }

        private void uninit() {

            drainEncoder(true);
            if (VERBOSE) Log.d(TAG, "releasing encoder objects");
            if (mEncoder != null) {
                mEncoder.stop();
                mEncoder.release();
                mEncoder = null;
            }
            if (mInputSurface != null) {
                mInputSurface.release();
                mInputSurface = null;
            }
            if (mMuxer != null) {
                mMuxer.stop();
                mMuxer.release();
                mMuxer = null;
            }
        }

        int frameIndex1 = 0;
        private FrameData fetchFrameData() {

            FrameData frameData = drainEncoder(false);
            // Generate a new frame of input.
            generateSurfaceFrame(frameIndex1);
            mInputSurface.setPresentationTime(computePresentationTimeNsec(frameIndex1));
            frameIndex1++;
            // Submit it to the encoder.  The eglSwapBuffers call will block if the input
            // is full, which would be bad if it stayed full until we dequeued an output
            // buffer (which we can't do, since we're stuck here).  So long as we fully drain
            // the encoder before supplying additional input, the system guarantees that we
            // can supply another frame without blocking.
            mInputSurface.swapBuffers();
            return frameData;
        }

        /**
         * Extracts all pending data from the encoder.
         * <p>
         * If endOfStream is not set, this returns when there is no more data to drain.  If it
         * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
         * Calling this with endOfStream set should be done once, right before stopping the muxer.
         */

        private FrameData drainEncoder(boolean endOfStream) {
            final int TIMEOUT_USEC = 10000;
            if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

            if (endOfStream) {
                if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
                mEncoder.signalEndOfInputStream();
            }

            ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
            while (true) {
                int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    if (!endOfStream) {
                        break;      // out of while
                    } else {
                        if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                    }
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not expected for an encoder
                    encoderOutputBuffers = mEncoder.getOutputBuffers();
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // should happen before receiving buffers, and should only happen once
                    if (mMuxerStarted) {
                        throw new RuntimeException("format changed twice");
                    }
                    MediaFormat newFormat = mEncoder.getOutputFormat();
                    Log.d(TAG, "encoder output format changed: " + newFormat);

                    // now that we have the Magic Goodies, start the muxer
                    mTrackIndex = mMuxer.addTrack(newFormat);
                    mMuxer.start();
                    mMuxerStarted = true;
                } else if (encoderStatus < 0) {
                    Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                            encoderStatus);
                    // let's ignore it
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                                " was null");
                    }

                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        // The codec config data was pulled out and fed to the muxer when we got
                        // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                        if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                        mBufferInfo.size = 0;
                    }

                    FrameData frameData = null;
                    if (mBufferInfo.size != 0) {
                        if (!mMuxerStarted) {
                            throw new RuntimeException("muxer hasn't started");
                        }

                        // adjust the ByteBuffer values to match BufferInfo (not needed?)
                        encodedData.position(mBufferInfo.offset);
                        encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                        mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                        if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");

                        try{

                            byte [] data = new byte[mBufferInfo.size];
                            encodedData.get(data, 0, data.length);
                            //return mBufferInfo.presentationTimeUs;

                            frameData = new FrameData();
                            frameData.data = data;
                            frameData.presentationTimeUs = mBufferInfo.presentationTimeUs;
                        }
                        catch(Exception exc_){

                            int foo = 1;
                            int bar = foo;
                        }
                    }

                    mEncoder.releaseOutputBuffer(encoderStatus, false);

                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (!endOfStream) {
                            Log.w(TAG, "reached end of stream unexpectedly");
                        } else {
                            if (VERBOSE) Log.d(TAG, "end of stream reached");
                        }
                        break;      // out of while
                    }
                    return frameData;
                }
            }
            return null;
        }

        /**
         * Generates a frame of data using GL commands.  We have an 8-frame animation
         * sequence that wraps around.  It looks like this:
         * <pre>
         *   0 1 2 3
         *   7 6 5 4
         * </pre>
         * We draw one of the eight rectangles and leave the rest set to the clear color.
         */
        private void generateSurfaceFrame(int frameIndex) {
            frameIndex %= 8;

            int startX, startY;
            if (frameIndex < 4) {
                // (0,0) is bottom-left in GL
                startX = frameIndex * (mWidth / 4);
                startY = mHeight / 2;
            } else {
                startX = (7 - frameIndex) * (mWidth / 4);
                startY = 0;
            }

            GLES20.glClearColor(TEST_R0 / 255.0f, TEST_G0 / 255.0f, TEST_B0 / 255.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(startX, startY, mWidth / 4, mHeight / 2);
            GLES20.glClearColor(TEST_R1 / 255.0f, TEST_G1 / 255.0f, TEST_B1 / 255.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
        }

        /**
         * Generates the presentation time for frame N, in nanoseconds.
         */
        private long computePresentationTimeNsec(int frameIndex) {
            final long ONE_BILLION = 1000000000;
            return frameIndex * ONE_BILLION / FRAME_RATE;
        }

        private static final String TAG = "EncodeAndMuxTest";
        private static final boolean VERBOSE = true;           // lots of logging

        // parameters for the encoder
        private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
        private static final int FRAME_RATE = 15;               // 15fps
        private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
        private static final int NUM_FRAMES = 30;               // two seconds of video

        // RGB color values for generated frames
        private static final int TEST_R0 = 0;
        private static final int TEST_G0 = 0;
        private static final int TEST_B0 = 200;
        private static final int TEST_R1 = 236;
        private static final int TEST_G1 = 50;
        private static final int TEST_B1 = 186;

        // size of a frame, in pixels
        private int mWidth = -1;
        private int mHeight = -1;
        // bit rate, in bits per second
        private int mBitRate = -1;

        // encoder / muxer state
        private MediaCodec mEncoder;
        private CodecInputSurface mInputSurface;
        private MediaMuxer mMuxer;
        private int mTrackIndex;
        private boolean mMuxerStarted;

        // allocate one of these up front so we don't need to do it every time
        private MediaCodec.BufferInfo mBufferInfo;
    }

    /**
     * Holds state associated with a Surface used for MediaCodec encoder input.
     * <p>
     * The constructor takes a Surface obtained from MediaCodec.createInputSurface(), and uses that
     * to create an EGL window surface.  Calls to eglSwapBuffers() cause a frame of data to be sent
     * to the video encoder.
     * <p>
     * This object owns the Surface -- releasing this will release the Surface too.
     */
    private static class CodecInputSurface {
        private static final int EGL_RECORDABLE_ANDROID = 0x3142;

        private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
        private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

        private Surface mSurface;

        /**
         * Creates a CodecInputSurface from a Surface.
         */
        public CodecInputSurface(Surface surface) {
            if (surface == null) {
                throw new NullPointerException();
            }
            mSurface = surface;

            eglSetup();
        }

        /**
         * Prepares EGL.  We want a GLES 2.0 context and a surface that supports recording.
         */
        private void eglSetup() {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                throw new RuntimeException("unable to get EGL14 display");
            }
            int[] version = new int[2];
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                throw new RuntimeException("unable to initialize EGL14");
            }

            // Configure EGL for recording and OpenGL ES 2.0.
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL_RECORDABLE_ANDROID, 1,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                    numConfigs, 0);
            checkEglError("eglCreateContext RGB888+recordable ES2");

            // Configure context for OpenGL ES 2.0.
            int[] attrib_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
                    attrib_list, 0);
            checkEglError("eglCreateContext");

            // Create a window surface, and attach it to the Surface we received.
            int[] surfaceAttribs = {
                    EGL14.EGL_NONE
            };
            mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurface,
                    surfaceAttribs, 0);
            checkEglError("eglCreateWindowSurface");
        }

        /**
         * Discards all resources held by this class, notably the EGL context.  Also releases the
         * Surface that was passed to our constructor.
         */
        public void release() {
            if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                        EGL14.EGL_NO_CONTEXT);
                EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
                EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
                EGL14.eglReleaseThread();
                EGL14.eglTerminate(mEGLDisplay);
            }

            mSurface.release();

            mEGLDisplay = EGL14.EGL_NO_DISPLAY;
            mEGLContext = EGL14.EGL_NO_CONTEXT;
            mEGLSurface = EGL14.EGL_NO_SURFACE;

            mSurface = null;
        }

        /**
         * Makes our EGL context and surface current.
         */
        public void makeCurrent() {
            EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
            checkEglError("eglMakeCurrent");
        }

        /**
         * Calls eglSwapBuffers.  Use this to "publish" the current frame.
         */
        public boolean swapBuffers() {
            boolean result = EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
            checkEglError("eglSwapBuffers");
            return result;
        }

        /**
         * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
         */
        public void setPresentationTime(long nsecs) {
            EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nsecs);
            checkEglError("eglPresentationTimeANDROID");
        }

        /**
         * Checks for EGL errors.  Throws an exception if one is found.
         */
        private void checkEglError(String msg) {
            int error;
            if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
                throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
            }
        }
    }
}
