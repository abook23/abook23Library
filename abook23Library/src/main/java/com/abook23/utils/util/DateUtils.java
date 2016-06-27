package com.abook23.utils.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param @return 设定文件
     * @return String    返回类型
     * @Title: getDate
     * @Description: TODO
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date());
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param @return 设定文件
     * @return String    返回类型
     * @Title: getDate
     * @Description: TODO
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(long dataTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(dataTime);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param @return 设定文件
     * @return String    返回类型
     * @Title: getDate
     * @Description: TODO
     */
    @SuppressLint("SimpleDateFormat")
    public static String format(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(dateStr);
    }

    /**
     * yyyy-MM-dd
     *
     * @param @return 设定文件
     * @return String    返回类型
     * @Title: getDate
     * @Description: TODO
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMDDate(long dataTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(dataTime);
    }

    /**
     * @param @param  dateType
     * @param @return 设定文件
     * @return String    返回类型
     * @Title: getDate ex:yyyy-MM-dd HH:mm:ss
     * @Description: TODO
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String dateType) {
        SimpleDateFormat format = new SimpleDateFormat(dateType);
        return format.format(new Date());
    }
}
