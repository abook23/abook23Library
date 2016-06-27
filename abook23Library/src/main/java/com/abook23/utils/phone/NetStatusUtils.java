package com.abook23.utils.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.abook23.listener.OnNetStatusListener;

/**
 * 时时监听网络状态
 *
 * @author Comsys-Administrator
 * @ClassName: NetBroadcastReceiver
 * @Description: TODO
 * @date 2015-5-10 下午8:10:04
 */
public class NetStatusUtils extends BroadcastReceiver {

    //filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    private static OnNetStatusListener onNetStatusListener;
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
        /**
         * 时间 2016年3月25日12:43:54
         * 杨雄
         */
        if (onNetStatusListener != null) {
            onNetStatusListener.onNetStatus(NetType, NetName, "-1 NoNetWork; 0 TYPE_MOBILE; 1 TYPE_WIFI; 2 TYPE_ETHERNET; 3 OtherNetWork");
        }
    }

    public static void setOnNetStatusListener(OnNetStatusListener listener) {
        onNetStatusListener = listener;
    }

}
