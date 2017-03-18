package com.example.zebul.cameraservice.ice4j;

import com.example.server.SignalingServer;
import com.example.signaling_message.ClientId;
import com.example.utils.LOGGER;
import com.example.utils.Timeout;
import com.example.utils.TimeoutStatus;
import com.example.zebul.cameraservice.ManualResetEvent;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
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

    private class DataExchanger {

        private ClientStreamConnection clientStreamConnection1, clientStreamConnection2;
        private ManualResetEvent client1DataReceivedEvent = new ManualResetEvent(false);
        private ManualResetEvent client2DataReceivedEvent = new ManualResetEvent(false);
        private String dataToSend1, dataToSend2;
        private String dataReceived1, dataReceived2;
        private static final long TIMEOUT_IN_MILLISECONDS = 10000;

        DataExchanger(
                ClientStreamConnection clientStreamConnection1,
                ClientStreamConnection clientStreamConnection2,
                String dataToSend1,
                String dataToSend2) {

            this.clientStreamConnection1= clientStreamConnection1;
            this.clientStreamConnection2= clientStreamConnection2;
            this.dataToSend1            = dataToSend1;
            this.dataToSend2            = dataToSend2;
        }

        void start(){

            new Thread(new Runnable() { @Override public void run() { receive1(); } }).start();
            new Thread(new Runnable() { @Override public void run() { receive2(); } }).start();
            new Thread(new Runnable() { @Override public void run() { send1(); } }).start();
            new Thread(new Runnable() { @Override public void run() { send2(); } }).start();
        }
        void receive1(){

            dataReceived1 = receive(clientStreamConnection1);
            client1DataReceivedEvent.set();
        }

        void receive2(){

            dataReceived2 = receive(clientStreamConnection2);
            client2DataReceivedEvent.set();
        }

        void waitForDataReception1(){

            waitForDataReception(client1DataReceivedEvent);
        }

        void waitForDataReception2(){

            waitForDataReception(client2DataReceivedEvent);
        }

        void waitForDataExchange(){

            waitForDataReception1();
            waitForDataReception2();
        }

        private void waitForDataReception(ManualResetEvent clientDataReceivedEvent){

            try {
                clientDataReceivedEvent.waitOne(TIMEOUT_IN_MILLISECONDS);
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }


        private String receive(ClientStreamConnection clientStreamConnection){

            String result = null;
            try {
                byte[] buffer = new byte[100];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                final DatagramSocket datagramSocket = clientStreamConnection.getDatagramSocket();
                datagramSocket.receive(datagramPacket);
                byte[] receivedData = new byte[datagramPacket.getLength()];
                System.arraycopy(buffer, 0, receivedData, 0, receivedData.length);
                result = new String(receivedData, "UTF-8");
            } catch (IOException exc) {

                exc.printStackTrace();
            }
            return result;
        }

        void send1(){

            Timeout timeout = new Timeout(TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS, TimeoutStatus.RESET);
            while(!client2DataReceivedEvent.isSet() && !timeout.isSet()){

                try {
                    send(clientStreamConnection1, dataToSend1);
                    client2DataReceivedEvent.waitOne(1000);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }

        void send2(){

            Timeout timeout = new Timeout(TIMEOUT_IN_MILLISECONDS, TimeUnit.MILLISECONDS, TimeoutStatus.RESET);
            while(!client1DataReceivedEvent.isSet() && !timeout.isSet()){

                try {
                    send(clientStreamConnection2, dataToSend2);
                    client1DataReceivedEvent.waitOne(1000);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }

        private void send(
                ClientStreamConnection clientStreamConnection,
                String dataToSend) throws IOException {

            final DatagramSocket datagramSocket = clientStreamConnection.getDatagramSocket();
            final InetSocketAddress remoteClientAddress = clientStreamConnection.getRemoteClientAddress();
            final byte[] bytes = dataToSend.getBytes(Charset.forName("UTF-8"));
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, remoteClientAddress);
            datagramSocket.send(datagramPacket);
        }

        public String getDataReceived1() {
            return dataReceived1;
        }

        public String getDataReceived2() {
            return dataReceived2;
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

        signalingServer.stop();
        client1.stop();
        client2.stop();

        ClientConnections clientConnections1 = clientConnectionListener1.getClientConnections();
        ClientConnections clientConnections2 = clientConnectionListener2.getClientConnections();

        final ClientStreamConnection clientStreamConnection1 =
                clientConnections1.getClientStreamConnection(ClientConnections.STREAM_NAME_CONTROL);

        final ClientStreamConnection clientStreamConnection2 =
                clientConnections2.getClientStreamConnection(ClientConnections.STREAM_NAME_CONTROL);

        DataExchanger dataExchanger = new DataExchanger(
                clientStreamConnection1,
                clientStreamConnection2,
                "data1",
                "data2");

        dataExchanger.start();
        dataExchanger.waitForDataExchange();

        assertEquals("data1", dataExchanger.getDataReceived2());
        assertEquals("data2", dataExchanger.getDataReceived1());
    }
}
