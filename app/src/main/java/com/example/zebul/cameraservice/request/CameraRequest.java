package com.example.zebul.cameraservice.request;

import com.example.zebul.cameraservice.CameraController;

/**
 * Created by zebul on 9/18/16.
 */
public interface CameraRequest {

    void executeRequest(CameraController cameraController_)throws RequestExecutionException;
}
