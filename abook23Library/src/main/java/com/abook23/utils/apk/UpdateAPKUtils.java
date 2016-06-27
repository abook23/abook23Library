package com.abook23.utils.apk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.util.Xml;

import com.abook23.listener.DownloadListener;
import com.abook23.utils.apk.bean.NewApkInfo;
import com.abook23.utils.http.DownLoadUtils;
import com.abook23.utils.http.HttpUtils;
import com.abook23.utils.http.bean.HttpInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 2015-9-2 15:32:08
 *
 * @author abook23
 *         newapk.DownloadAPK();
 */
public class UpdateAPKUtils {
    private Context context;
    private Handler handler = null;

    public UpdateAPKUtils(Context context) {
        this.context = context;
    }

    /**
     * 获取新的APK信息
     *
     * @param path
     * @return
     */
    private NewApkInfo getNewApkInfo(String path) {

        NewApkInfo apkInfo = null;
        HttpInfo httpInfo = HttpUtils.get(path);//http请求
        if (httpInfo.getHttpCode() == 200 && httpInfo.getResult() != null) {
            apkInfo = new NewApkInfo();
            XmlPullParser xmlPullParser = Xml.newPullParser();
            try {
                //InputStream is = new  StringBufferInputStream(httpInfo.getResult());
                InputStream is = new ByteArrayInputStream(httpInfo.getResult().getBytes());
                xmlPullParser.setInput(is, "utf-8");
                int type = xmlPullParser.getEventType();
                while (type != XmlPullParser.END_DOCUMENT) {
                    switch (type) {
                        case XmlPullParser.START_TAG:
                            if (xmlPullParser.getName().equals("version")) {
                                apkInfo.setVersion(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("description")) {
                                apkInfo.setDescription(xmlPullParser.nextText());
                            } else if (xmlPullParser.getName().equals("apkurl")) {
                                apkInfo.setUrl(xmlPullParser.nextText());
                            }
                            break;

                        default:
                            break;
                    }
                    type = xmlPullParser.next();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return apkInfo;
    }

    public String getVersion() {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "未知版本";
        }

    }

    /**
     * 安装apk
     *
     * @param file 要安装的apk的目录
     */
    private void install(File file) {
        if (file != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
//				如果没有android.os.Process.killProcess(android.os.Process.myPid());最后不会提示完成、打开。
//				如果没有i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);这一步的话，最后安装好了，点打开，是不会打开新版本应用的。
        }

    }

    /**
     * 主方法
     */
    public void DownloadAPK(String apkUrl, String fileName) {
        DownLoadUtils downLoadUtils = new DownLoadUtils(context);
        downLoadUtils.downFile(apkUrl, context.getPackageName() + "/download/", fileName, new DownloadListener() {
            @Override
            public void onStart(float fileByteSize) {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onSize(float size, float maxSize) {

            }

            @Override
            public void onFail() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null)
                    install(file);
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
