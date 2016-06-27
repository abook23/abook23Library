package com.abook23.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.abook23.listener.DownloadListener;
import com.abook23.utils.http.DownLoadUtils;
import com.abook23.utils.util.FileUtils;

import java.io.File;

/**
 * 下载服务
 */
public class DownloadService extends Service {

    private final String TAG = "DownloadService";
    private FileUtils fileUtils;
    private Context context;
    private String url, path, fileName;
    public IBinder mBuilder = new DownloadBinder();

    private DownLoadUtils downLoadUtils;

    public DownloadService() {

    }

    public void setDownloadListener(DownloadListener downloadListener) {
        downLoadUtils.setDownloadListener(downloadListener);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null) {
            context = getApplicationContext();
        }
        fileUtils = new FileUtils(context);
        downLoadUtils = new DownLoadUtils(context);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBuilder;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        url = intent.getStringExtra(DownloadActivity.URL);
        path = intent.getStringExtra(DownloadActivity.DIRS_NAME);
        fileName = intent.getStringExtra(DownloadActivity.FILE_NAME);
        if (url != null) {
            downLoadUtils.downFile(url, path, fileName);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 取消
     */
    public void onCancel() {
        downLoadUtils.setCancel(true);
    }

    /**
     * 暂停、继续
     */
    public void onPause() {
        downLoadUtils.setPause(!downLoadUtils.isPause());
    }

    /**
     * 安装apk
     *
     * @param file 要安装的apk的目录
     */
    public void install(File file) {
        if (file != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
//		    如果没有android.os.Process.killProcess(android.os.Process.myPid());最后不会提示完成、打开。
//			如果没有i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);这一步的话，最后安装好了，点打开，是不会打开新版本应用的。
//          this.finish();
        }

    }

    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }
}
