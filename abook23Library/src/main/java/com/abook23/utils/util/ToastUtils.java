package com.abook23.utils.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	/**
	 *
	 * @param context 1121
	 * @param text 11
     */
	public static void show(Context context,String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void show(Context context,int values) {
		Toast.makeText(context, values+"", Toast.LENGTH_SHORT).show();
	}

	public static void debugShow(Context context,String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
