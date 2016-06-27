package com.abook23.utils.phone;

import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

/**
 * Created by abook23 on 2015/9/9.
 *
 * @author abook23 abook23@163.com
 * @version 1.0
 */
public class WifiUtil {

    private static WifiManager wifiManager;
    private Context context;
    private int wifi_state;

    public static WifiUtil init(Context context) {
        WifiUtil wifiUtil = new WifiUtil(context);
        wifiUtil.context = context;
        return wifiUtil;
    }

    public WifiUtil(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean opWifi() {
        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        return true;
    }

    public String getIpAddr() {
        if (!wifiManager.isWifiEnabled())
            return "";
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return int2ip(ip);
    }

    /**
     * wifi 不休眠
     * uses-permission android:name="android.permission.WRITE_SETTINGS"
     *
     * @value 默认WiFi策略
     */
    public int setWifiNeverDormancy() {
        ContentResolver resolver = context.getContentResolver();
        int wifi_state = Settings.System.getInt(resolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        if (Settings.System.WIFI_SLEEP_POLICY_NEVER != wifi_state) {
            Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);
        }
        return wifi_state;
    }

    public void recoverWifi() {
        ContentResolver resolver = context.getContentResolver();
        Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY, wifi_state);
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }
}
