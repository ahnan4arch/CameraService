package com.example.zebul.cameraservice.response;

/**
 * Created by zebul on 9/20/16.
 */
public class CameraResponseFailure  extends CameraResponse{

    @Override
    public boolean isSuccess(){

        return false;
    }
}
