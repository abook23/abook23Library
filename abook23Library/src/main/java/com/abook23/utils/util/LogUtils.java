package com.abook23.utils.util;

import android.util.Log;

/**
 * Created by abook23 on 2015/9/9.
 */
public class LogUtils {
    private final static String TAG = "";

    public static void i(String value) {
        Log.i(TAG, value);
    }

    public static void d(String value) {
        Log.d(TAG, value);
    }

    public static void w(String value) {
        Log.w(TAG, value);
    }

    public static void e(String value) {
        Log.e(TAG, value);
    }

    public static void e(String value, Throwable tr) {
        Log.e(TAG, value, tr);
    }
}
