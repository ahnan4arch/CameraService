package com.example.zebul.cameraservice.ice4j;

import com.example.server.SignalingServer;
import com.example.signaling_message.ClientId;
import com.example.utils.LOGGER;
import com.example.zebul.cameraservice.ManualResetEvent;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertTrue;

/**
 * Created by bartek on 17.03.17.
 */

public class IceSignalingClientTest {

    static class ClientConnectionListenerFake implements ClientConnectionResultListener {

        private static final String TAG = ClientConnectionListenerFake.class.getSimpleName();
        private ManualResetEvent resultReceivedEvent = new ManualResetEvent(false);

        private boolean success = false;
        private ClientConnections clientConnections;
        @Override
        public void onClientConnectionSuccess(
                ClientId remoteClientId,
                ClientConnections clientConnections) {

            success = true;
            this.clientConnections = clientConnections;
            resultReceivedEvent.set();
            LOGGER.Log(TAG, "onClientConnectionSuccess to client: "+remoteClientId);
        }

        @Override
        public void onClientConnectionFailure(
                ClientId remoteClientId,
                Exception exc) {

            success = false;
            resultReceivedEvent.set();
            LOGGER.Log(TAG, "onClientConnectionFailure: "+remoteClientId+", reason: "+exc);
        }

        public void waitForConnectionEvent() {

            try {
                resultReceivedEvent.waitOne(30*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean isConnectedSuccessfully() {
            return success;
        }

        public ClientConnections getClientConnections() {
            return clientConnections;
        }
    }

    class DataExchanger {

        private ClientStreamConnection clientStreamConnection1;
        private ClientStreamConnection clientStreamConnection2;

        DataExchanger(
                ClientStreamConnection clientStreamConnection1,
                ClientStreamConnection clientStreamConnection2) {

            this.clientStreamConnection1 = clientStreamConnection1;
            this.clientStreamConnection2 = clientStreamConnection2;
        }

        public String receive1(){

            String result = null;
            try {
                byte[] buffer = new byte[100];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                final DatagramSocket datagramSocket = clientStreamConnection1.getDatagramSocket();
                datagramSocket.receive(datagramPacket);
                byte[] receivedData = new byte[datagramPacket.getLength()];
                System.arraycopy(buffer, 0, receivedData, 0, receivedData.length);
                result = new String(receivedData, "UTF-8");
            } catch (IOException exc) {

                exc.printStackTrace();
            }
            return result;
        }

        public void receive2(){

        }

        public void send1(){

            final DatagramSocket datagramSocket1 = clientStreamConnection1.getDatagramSocket();
            while(true){



            }
        }

        public void send2(){

        }

    }

    @Test
    public void test1(){

        int serverPort = 32445;
        InetSocketAddress serverSocketAddress = new InetSocketAddress("127.0.0.1", serverPort);
        SignalingServer signalingServer = new SignalingServer(serverPort);
        ClientId clientId1 = new ClientId("foo");
        IceSignalingClient client1 = new IceSignalingClient(serverSocketAddress, clientId1);
        ClientId clientId2 = new ClientId("bar");
        IceSignalingClient client2 = new IceSignalingClient(serverSocketAddress, clientId2);

        signalingServer.start();
        client1.start();
        client2.start();

        ClientConnectionListenerFake clientConnectionListener1 = new ClientConnectionListenerFake();
        ClientConnectionListenerFake clientConnectionListener2 = new ClientConnectionListenerFake();
        client1.attachClientConnectionListener(clientConnectionListener1);
        client2.attachClientConnectionListener(clientConnectionListener2);

        client1.connectWithClient(clientId2);

        clientConnectionListener1.waitForConnectionEvent();
        clientConnectionListener2.waitForConnectionEvent();

        assertTrue(clientConnectionListener1.isConnectedSuccessfully());
        assertTrue(clientConnectionListener2.isConnectedSuccessfully());

        ClientConnections clientConnections1 = clientConnectionListener1.getClientConnections();
        ClientConnections clientConnections2 = clientConnectionListener2.getClientConnections();



        signalingServer.stop();
        client1.stop();
        client2.stop();
    }
}
