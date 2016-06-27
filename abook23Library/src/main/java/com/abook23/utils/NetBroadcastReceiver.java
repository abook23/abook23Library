package com.abook23.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 时时监听网络状态
 *
 * @author Comsys-Administrator
 * @ClassName: NetBroadcastReceiver
 * @Description: TODO
 * @date 2015-5-10 下午8:10:04
 */
public class NetBroadcastReceiver extends BroadcastReceiver {

    //filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    public static OnNetListener onNetListener;
    private static long oldTime;
    /**
     * -1 NoNetWork; 0 TYPE_MOBILE; 1 TYPE_WIFI; 2 TYPE_ETHERNET; 3 OtherNetWork
     */
    public static int NetType;
    public static String NetName;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        long time = System.currentTimeMillis();
        if (time - oldTime < 1000) {
            return;
        }
        oldTime = time;
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
        if (onNetListener != null) {
            onNetListener.onNetLoader(NetType, NetName);
        }
    }

    public void setOnNetListener(OnNetListener listener) {
        onNetListener = listener;
    }


    public interface OnNetListener {
        /**
         * -1 NoNetWork; 0 TYPE_MOBILE; 1 TYPE_WIFI; 2 TYPE_ETHERNET; 3 OtherNetWork
         *
         * @param netType
         * @param NetName
         */
        void onNetLoader(int netType, String NetName);
    }

}
