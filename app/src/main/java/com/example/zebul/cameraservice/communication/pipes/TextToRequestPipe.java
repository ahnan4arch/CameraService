package com.example.zebul.cameraservice.communication.pipes;

import android.content.Context;

import com.example.zebul.cameraservice.request.ChangeCameraRequest;
import com.example.zebul.cameraservice.request.TakePictureRequest;
import com.example.signaling_message.Message;
import com.example.signaling_message.MessagePipe;

/**
 * Created by zebul on 9/20/16.
 */
public class TextToRequestPipe implements MessagePipe {

    private Context context;

    public TextToRequestPipe(Context context_){

        context = context_;
    }

    @Override
    public Message transmit(Message message_) throws UnknownRequestException {

        byte [] data = (byte [])message_.getData();
        String text = new String(data);
        if(text.contains("take_picture")){

            return new Message(message_.getAddress(), new TakePictureRequest(context));
        }
        else if(text.contains("change_camera")){

            return new Message(message_.getAddress(), new ChangeCameraRequest());
        }
        throw new UnknownRequestException(text);
    }
}
