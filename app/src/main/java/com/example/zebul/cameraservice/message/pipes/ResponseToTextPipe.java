package com.example.zebul.cameraservice.message.pipes;

import com.example.zebul.cameraservice.message.Message;
import com.example.zebul.cameraservice.message.MessagePipe;
import com.example.zebul.cameraservice.request.ChangeCameraRequest;
import com.example.zebul.cameraservice.request.TakePictureRequest;
import com.example.zebul.cameraservice.response.CameraResponse;

/**
 * Created by zebul on 9/20/16.
 */
public class ResponseToTextPipe implements MessagePipe {

    @Override
    public Message transmit(Message message_) throws UnknownRequestException{

        CameraResponse cameraResponse = (CameraResponse)message_.getData();
        String text = cameraResponse.isSuccess() ? "OKK" : "ERR";
        return new Message(message_.getAddress(), text.getBytes());
    }
}
