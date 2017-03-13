package com.example.signaling_message;

/**
 * Created by zebul on 9/18/16.
 */
public class Message {

    protected Object address;
    protected Object data;

    public Message(Object address_, Object data_){

        address = address_;
        data = data_;
    }

    public Object getAddress(){

        return address;
    }

    public Object getData(){

        return data;
    }

    public void setAddress(Object address_) {
        address = address_;
    }

    public void setData(Object data_) {
        data = data_;
    }
}
