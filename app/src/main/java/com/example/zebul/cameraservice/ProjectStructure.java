package com.example.zebul.cameraservice;

/**
 * Created by zebul on 11/13/16.
 */

public class ProjectStructure {

    /*
    class RTSPSerever{

        List<RTSPSessions> rtspSessions;
        public void start(){
        }

        public void stop(){

            for(RTSPSessions rtspSession: rtspSessions){

                rtspSession.stop();
            }
        }

        public void run(){

            while(keepRunning){

                Socket socket = listen();
                RTSPSessionsController rtspSessionsController = new RTSPSessionsController();
                RTSPSession rtspSession = new RTSPSession(socket, rtspSessionsController);
                rtspSessions.add(rtspSession);
                rtspSession.start();
            }
        }
    }

    class RTSPSession{

        RTPSessionsController rtpSessionsController;
        Socket socket;

        public void start(){
        }

        public void stop(){
        }

        public void run(){

            while(true){

                byte [] incomingData = socket.receive();
                RTSPMessage incomingRTSPMessage = generateIncomingMessage(data);

                byte [] outgoingData = null;
                RTSPMessage outgoingRTSPMessage = null;

                if( isRequest(rtspMessage) ){
                    RTSPRequest rtspRequest = (RTSPRequest)rtspMessage;
                    switch(rtspRequest.getMethod()){
                    case Method.DESCRIBE:
                        outgoingRTSPMessage = rtpSessionsController.onDescribeRequest(rtspRequest);
                    break;
                    case Method.PLAY:
                        outgoingRTSPMessage = rtpSessionsController.onPlayRequest(rtspRequest);
                    break;
                    case Method.TEARDOWN:
                        outgoingRTSPMessage = rtpSessionsController.onTeardownRequest(rtspRequest);
                        return
                    break
                    }
                }
                else{
                    RTSPResponse rtspResponse = (RTSPResponse)rtspMessage;
                    outgoingRTSPMessage = rtpSessionsController.onResponseRequest(rtspResponse);
                }
            }
        }
    }

    class RTPServerSessionController{


        public RTSPResponse onDescribeRequest(RTSPRequest rtspRequest){
        }

        public RTSPResponse onPlayRequest(RTSPRequest rtspRequest){
        }
    }

    class RTPPacketizationSessionController{

        @Override
        public RTSPResponse onDescribeRequest(RTSPRequest rtspRequest){
        }

        @Override
        public RTSPResponse onPlayRequest(RTSPRequest rtspRequest){
        }

        public void run(){

            while(keepSession){

                List<NALUnit> nalUnits = nalUnitProducer.produce();
                List<RTPDataPacket> rtpDataPackets = rtpPacketizationSession.createRTPPackests(nalUnits);
                rtpDataPacketsConsumer.consume(rtpDataPackets);
            }
        }
    }

    class RTPDepacketizationSessionController{

        @Override
        public RTSPResponse onDescribeRequest(RTSPRequest rtspRequest){
        }

        @Override
        public RTSPResponse onPlayRequest(RTSPRequest rtspRequest){
        }

        public void run(){

            while(keepSession){

                NALUnit nalUnit = rtpDataPacketProducer.produce();
                List<RTPPacketData> rtpPacketsData = rtpPacketizationSession.createRTPPacketData(nalUnit);
                rtpPacketsDataConsumer.consume(rtpPacketsData);
            }
        }
    }

    class H264Packetizer{
        private int sequenceNumber = 0;
        private int SSRC = 0;

        List<RTPDataPacket> createRTPPackests(DataPackets videoDataPack_){
        }
        class RTPPacketizer{


        }
    }

    class RTPDepacketizationSession{
        private int sequenceNumber = 0;
        private int SSRC = 0;

        DataPackets createVideoDataPack(RTPPacketData rtpPacketData){

            rtpDepacketizer.createNALUnit(rtpPacketData);
        }

        class RTPDepacketizer{

        }
    }

    class RTPDataPacket{
        byte [] data;
    }

    class RTPPacket{
        RTPHeader rtpHeader;
        NALUnitHeader nalUnitHeader;
        NALUnitData nalUnitData;
    }

    */



}
