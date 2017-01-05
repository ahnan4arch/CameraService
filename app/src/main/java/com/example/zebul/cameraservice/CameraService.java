package com.example.zebul.cameraservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by zebul on 9/18/16.
 */
public class CameraService extends Service{

    private CameraController cameraController = new CameraController(this);
    public static CameraService CAMERA_SERVICE;

    private static final int CAMERA_SERVICE_NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

        CAMERA_SERVICE = this;
        setUp();
        showToastServiceIsAlive();
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
    }

    private void tearDown() {

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
                        .setSmallIcon(R.mipmap.ic_remove_red_eye_white_18dp)
                        .setContentTitle( "Camera Service" )
                        .setContentText( "Beware you are spied" )
                        .setContentIntent(pendingIntent);

        Notification notification =  notificationBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        return notification;
    }

}
