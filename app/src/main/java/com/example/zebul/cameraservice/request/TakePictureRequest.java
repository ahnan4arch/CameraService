package com.example.zebul.cameraservice.request;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.example.zebul.cameraservice.CameraController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zebul on 9/18/16.
 */
public class TakePictureRequest implements CameraRequest {

    private static final String TAG = TakePictureRequest.class.getSimpleName();

    private Context context;
    public TakePictureRequest(Context context_){

        context = context_;
    }
    @Override
    public void executeRequest(CameraController cameraController_)throws RequestExecutionException{

        try{

            Log.d(TAG, "before taking picture");
            Camera camera = cameraController_.getCamera();
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            Log.d(TAG, "after taking picture");
        }
        catch(Exception exc_){


            throw new RequestExecutionException();
        }
    }

    Camera.ShutterCallback shutterCallback = new android.hardware.Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new android.hardware.Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new android.hardware.Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
            Log.d(TAG, "onPictureTaken - jpeg");
            new SaveImageTask().execute(data);
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("aba_%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        context.sendBroadcast(mediaScanIntent);
    }

}
