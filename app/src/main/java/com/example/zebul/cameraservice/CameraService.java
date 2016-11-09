package com.example.zebul.cameraservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by zebul on 9/18/16.
 */
public class CameraService extends Service{

    private CameraController cameraController = new CameraController(this);
    public static CameraService CAMERA_SERVICE;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
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

}
