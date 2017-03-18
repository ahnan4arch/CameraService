package com.example.zebul.cameraservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.server.SignalingServer;
import com.example.signaling_message.ClientId;
import com.example.zebul.cameraservice.communication.server.RTSPSessionCreatedEvent;
import com.example.zebul.cameraservice.communication.server.RTSPSessionDestroyedEvent;
import com.example.zebul.cameraservice.communication.server.RTSPSessionEventListener;
import com.example.zebul.cameraservice.ice4j.IceSignalingClient;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by zebul on 9/18/16.
 */
public class CameraService extends Service implements RTSPSessionEventListener {

    private static final int EXECUTE_COMMAND = 1;
    private static final String SERVICE_NAME = CameraService.class.getSimpleName();
    private CameraController cameraController = new CameraController(this);

    private static final int CAMERA_SERVICE_NOTIFICATION_ID = 1;

    private CameraServiceBinder cameraServiceBinder = new CameraServiceBinder();
    private IceSignalingClient iceSignalingClient =
            new IceSignalingClient(
                    new InetSocketAddress("192.168.1.7", SignalingServer.DEFAULT_PORT)
                    , new ClientId(Build.MANUFACTURER));

    public class CameraServiceBinder extends Binder {
        CameraService getService() {
            return CameraService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return cameraServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        keepInForeground();
        return START_STICKY;
    }

    private void keepInForeground() {

        Context appContext = getApplicationContext();
        Notification notification = createServiceNotification(appContext);
        startForeground(CAMERA_SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public void onCreate() {

        super.onCreate();
        setUp();
        showToastServiceIsAlive();
        cameraController.attachRTSPSessionLifecycleListener(this);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        tearDown();
        showToastServiceIsDead();
    }

    private void showToastServiceIsAlive() {

        Toast.makeText(this, "Camera service is alive", Toast.LENGTH_SHORT).show();
    }

    private void showToastServiceIsDead() {

        Toast.makeText(this, "Camera service is dead", Toast.LENGTH_SHORT).show();
    }

    private void setUp() {

        cameraController.start();
        iceSignalingClient.start();
    }

    private void tearDown() {

        iceSignalingClient.stop();
        cameraController.stop();
    }

    public Notification createServiceNotification(Context appContext_){

        Intent intent = new Intent(appContext_, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_videocam_white_18dp)
                        .setContentTitle( SERVICE_NAME )
                        .setContentText( "Beware you are spied" )
                        .setContentIntent(pendingIntent);

        Notification notification =  notificationBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }

    private void updateNotification(String contentText, int imageResId){

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// Sets an ID for the notification, so it can be updated
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(SERVICE_NAME)
                .setContentText(contentText)
                .setSmallIcon(imageResId);

        notificationManager.notify(
                CAMERA_SERVICE_NOTIFICATION_ID,
                notifyBuilder.build());
    }

    final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what == EXECUTE_COMMAND){

                Command command = (Command)msg.obj;
                command.execute();
            }
            return false;
        }
    });

    interface Command{

        void execute();
    }

    class ShowToastCommand implements Command{

        private Context context;
        private String textMessage;

        ShowToastCommand(Context context, String textMessage){

            this.context = context;
            this.textMessage = textMessage;
        }

        @Override
        public void execute() {

            Toast.makeText(context, textMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRTSPSessionCreatedEvent(RTSPSessionCreatedEvent rtspSessionCreatedEvent) {

        final SocketAddress remoteSocketAddress = rtspSessionCreatedEvent.getRemoteSocketAddress();
        String textMessage = "SessionDescription created by: "+remoteSocketAddress.toString();
        showTextMessageAsToast(textMessage);
        updateNotification(textMessage, R.mipmap.ic_remove_red_eye_white_18dp);
    }

    @Override
    public void onRTSPSessionDestroyedEvent(RTSPSessionDestroyedEvent rtspSessionDestroyedEvent) {

        final SocketAddress remoteSocketAddress = rtspSessionDestroyedEvent.getRemoteSocketAddress();
        String textMessage = "SessionDescription destroyed by: "+remoteSocketAddress.toString();
        showTextMessageAsToast(textMessage);
        updateNotification(textMessage, R.mipmap.ic_videocam_white_18dp);
    }

    private void showTextMessageAsToast(String textMessage){

        Message message = handler.obtainMessage(EXECUTE_COMMAND);
        message.obj = new ShowToastCommand(this, textMessage);
        message.sendToTarget();
    }

    public void showToast(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
