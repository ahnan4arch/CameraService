package com.example.zebul.cameraservice;

import android.content.Context;
import android.hardware.Camera;

import com.example.message.TransmissionException;
import com.example.zebul.cameraservice.communication.server.RTSPServer;
import com.example.zebul.cameraservice.communication.server.RTSPSessionEventListener;
import com.example.udp.SocketEngine;
import com.example.udp.SocketMessageReceptionListener;
import com.example.message.Message;
import com.example.message.MessagePipeline;
import com.example.message.MessagePipelineEndpoint;
import com.example.zebul.cameraservice.communication.pipes.ResponseToTextPipe;
import com.example.zebul.cameraservice.communication.pipes.TextToRequestPipe;
import com.example.zebul.cameraservice.request.CameraRequest;
import com.example.zebul.cameraservice.request.RequestExecutionException;
import com.example.zebul.cameraservice.response.CameraResponseFailure;
import com.example.zebul.cameraservice.response.CameraResponseSuccess;

/**
 * Created by zebul on 9/18/16.
 */
public class CameraController {

    private MessagePipeline outgoingMessagePipeline = new MessagePipeline();
    private MessagePipeline incomingMessagePipeline = new MessagePipeline();
    private RTSPServer rtspServer = new RTSPServer(9999);

    private SocketEngine socketEngine = new SocketEngine(8896, new IncomingMessageReceptionListener());
    private Camera camera;

    public CameraController(Context context_){

        outgoingMessagePipeline.addMessagePipe(new ResponseToTextPipe());
        OutgoingMessagePipeEndpoint outgoingMessagePipeEndpoint = new OutgoingMessagePipeEndpoint();
        outgoingMessagePipeline.setMessageEndpoint(outgoingMessagePipeEndpoint);

        incomingMessagePipeline.addMessagePipe(new TextToRequestPipe(context_));
        IncomingMessagePipeEndpoint incomingMessagePipeEndpoint = new IncomingMessagePipeEndpoint();
        incomingMessagePipeline.setMessageEndpoint(incomingMessagePipeEndpoint);
    }

    public void start(){

        try{

            //camera = Camera.open(0);
        }
        catch(Exception exc_){

            int foo = 1;
            int bar = foo;
        }
        socketEngine.start();
        rtspServer.start();
    }

    public void stop(){

        rtspServer.stop();
        socketEngine.stop();
        if(camera != null){
            camera.release();
            camera = null;
        }
    }

    public Camera getCamera() {
        return camera;
    }

    public void attachRTSPSessionLifecycleListener(
            RTSPSessionEventListener rtspSessionLifecycleListener) {

        rtspServer.attachRTSPSessionLifecycleListener(rtspSessionLifecycleListener);
    }

    public void detachRTSPSessionLifecycleListener(
            RTSPSessionEventListener rtspSessionLifecycleListener) {

        rtspServer.detachRTSPSessionLifecycleListener(rtspSessionLifecycleListener);
    }


    class IncomingMessageReceptionListener implements SocketMessageReceptionListener {

        @Override
        public void onSocketMessageReceived(Message message_) {

            try {
                incomingMessagePipeline.transmit(message_);
            } catch (TransmissionException e) {
                e.printStackTrace();
            }
        }
    }

    class OutgoingMessagePipeEndpoint implements MessagePipelineEndpoint {

        @Override
        public void onTransmittedMessage(Message message_) {

            //InetSocketAddress inetSocketAddress = null;//new InetSocketAddress();
            //DatagramPacket datagramPacket = null;//new DatagramPacket();
            socketEngine.post(message_);
        }
    }

    class IncomingMessagePipeEndpoint implements MessagePipelineEndpoint {

        @Override
        public void onTransmittedMessage(Message message_) {

            Object address = message_.getAddress();
            try{

                CameraRequest cameraRequest = (CameraRequest) message_.getData();
                execute(cameraRequest);
                Message successMessage = new Message(address, new CameraResponseSuccess());
                transmitViaOutgoingMessagePipeline(successMessage);
            }
            catch(RequestExecutionException exc_){

                Message failureMessage = new Message(address, new CameraResponseFailure());
                transmitViaOutgoingMessagePipeline(failureMessage);
            }
        }
    }

    private void transmitViaOutgoingMessagePipeline(Message message) {

        try {
            outgoingMessagePipeline.transmit(message);
        } catch (TransmissionException exc) {
            exc.printStackTrace();
        }
    }

    private void execute(CameraRequest command) throws RequestExecutionException {

        command.executeRequest(this);
    }
}
