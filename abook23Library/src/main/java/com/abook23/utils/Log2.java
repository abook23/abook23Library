package com.abook23.utils;

import android.util.Log;

/**
 * Created by My on 2016/5/9.
 */
public class Log2 {

    public static boolean LOG_STATE = true;

    public static boolean DEBUG = true;


    public static boolean VERBOSE = true;


    public static boolean WARNING = true;


    public static boolean INFO = true;


    public static boolean ERROR = true;


    public static void d(String tag, String msg) {
        if (LOG_STATE) if (DEBUG)
            Log.d(tag, msg);
    }


    public static void i(String tag, String msg) {
        if (LOG_STATE)
            if (INFO)
                Log.i(tag, msg);
    }


    public static void e(String tag, String msg) {
        if (LOG_STATE)
            if (ERROR)
                Log.e(tag, msg);
    }


    public static void v(String tag, String msg) {
        if (LOG_STATE) if (VERBOSE)
            Log.v(tag, msg);
    }


    public static void w(String tag, String msg) {
        if (LOG_STATE) if (WARNING)
            Log.w(tag, msg);
    }


    public static String getStackTraceString(Exception e) {
        return Log.getStackTraceString(e);
    }


    public static void w(String tag, String msg, Exception e) {
        if (LOG_STATE) if (WARNING)
            Log.w(tag, msg, e);
    }

}
