package com.example.zebul.cameraservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private CameraService cameraService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpWidgets();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CameraService.CameraServiceBinder binder = (CameraService.CameraServiceBinder) service;
            cameraService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        startCameraService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void setUpWidgets() {

        Button startDisplayVideoActivityButton = (Button) findViewById(R.id.startDisplayVideoActivityButton);
        startDisplayVideoActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDisplayVideoFromFileActivity();
            }
        });

        Button startDisplayVideoFromRemoteSourceActivityButton = (Button) findViewById(R.id.startDisplayVideoFromRemoteSourceActivityButton);
        startDisplayVideoFromRemoteSourceActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDisplayVideoFromRemoteSourceActivity();
            }
        });

        Button playMovieActivityButton = (Button) findViewById(R.id.playMovieActivityButton);
        playMovieActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMoviePlayerActivity();
            }
        });

        Button buildSdpActivityButton = (Button) findViewById(R.id.buildSdpActivityButton);
        buildSdpActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startBuildSdpActivity();
            }
        });

        Button showToastFromServiceButton = (Button) findViewById(R.id.showToastFromServiceButton);
        showToastFromServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToastFromService();
            }
        });

    }

    private void startBuildSdpActivity() {

        Intent intent = new Intent(this, SDPExchangeServiceTestActivity.class);
        startActivity(intent);
    }

    private void startCameraService(){

        Intent intent = new Intent(this, CameraService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void startDisplayVideoFromFileActivity() {

        Intent intent = new Intent(this, DisplayVideoFromFileActivity.class);
        startActivity(intent);
    }

    private void startDisplayVideoFromRemoteSourceActivity() {

        Intent intent = new Intent(this, DisplayVideoFromRemoteSourceActivity.class);
        startActivity(intent);
    }

    private void startMoviePlayerActivity(){

        Intent intent = new Intent(this, PlayMovieActivity.class);
        startActivity(intent);
    }

    private void showToastFromService() {

        cameraService.showToast("Some toast!!!");
    }
}
