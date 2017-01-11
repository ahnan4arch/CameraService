package com.example.zebul.cameraservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpWidgets();
    }

    private void setUpWidgets() {

        Button startCameraServiceButton = (Button) findViewById(R.id.startCameraServiceButton);
        startCameraServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startCameraService();
            }
        });

        Button startDisplayVideoActivityButton = (Button) findViewById(R.id.startDisplayVideoActivityButton);
        startDisplayVideoActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDisplayVideoActivity();
            }
        });

        Button playMovieActivityButton = (Button) findViewById(R.id.playMovieActivityButton);
        playMovieActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMoviePlayerActivity();
            }
        });
    }

    private void startCameraService(){

        Intent intent = new Intent(this, CameraService.class);
        startService(intent);
    }

    private void startDisplayVideoActivity() {

        Intent intent = new Intent(this, DisplayVideoActivity.class);
        startActivity(intent);
    }

    private void startMoviePlayerActivity(){

        Intent intent = new Intent(this, PlayMovieActivity.class);
        startActivity(intent);
    }
}
