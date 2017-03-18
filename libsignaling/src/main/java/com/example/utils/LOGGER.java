package com.example.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bartek on 18.03.17.
 */

public class LOGGER {

    public static void Log(String tag, String message){

        String formatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        System.out.println(formatedDate+": "+tag+": "+message);
    }
}
