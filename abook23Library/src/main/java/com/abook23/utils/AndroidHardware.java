package com.abook23.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 手机硬件
 *
 * @author Comsys-Administrator
 * @ClassName: AndroidHardware
 * @Description: TODO
 * @date 2015-5-10 下午7:55:56
 */
public class AndroidHardware {

    /**
     * -1 NoNetWork; 0 TYPE_MOBILE; 1 TYPE_WIFI; 2 TYPE_ETHERNET; 3 OtherNetWork
     */
    private static int NetType;
    private static String NetName;

    /**
     * 获取当前的网络状态 -1：没有网络  0：wap网络    1：WIFI网络     2：net网络
     * </p> startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));// 进入无线网络配置界面
     * </p> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // 进入手机中的wifi网络设置界面
     *
     * @param context
     * @return
     */
    public static int getNetWork(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            switch (netInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    NetType = 0;
                    NetName = netInfo.getSubtypeName();
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    NetType = 1;
                    NetName = netInfo.getTypeName();
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    NetType = 2;
                    NetName = netInfo.getTypeName();
                    break;
                default:
                    NetType = 3;
                    NetName = "OtherNetWork";
                    break;
            }
        } else {
            NetType = -1;
            NetName = "NoNetWork";
        }
        return NetType;
    }

    public static boolean isNetWork(Context context) {
        return getNetWork(context) > -1 ? true : false;
    }

    /**
     * GPS 是否打开
     * </p> GPS 设置
     * Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
     * startActivityForResult(intent,0); //此为设置完成后返回到获取界面
     *
     * @param context
     * @return boolean    返回类型
     * @Title: isGpsAvailable
     * @Description: TODO
     */
    public static boolean isGpsAvailable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }
}
