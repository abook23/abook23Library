package com.abook23.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abook23.listener.DownloadListener;
import com.abook23.utils.R;

import java.io.File;
import java.text.DecimalFormat;

/**
 * dialog 下载 当选后台下载后，notification 显示下， android 4.0 +
 * intent.getStringExtra("url");
 * getStringExtra("path");
 * getStringExtra("fileName");
 *
 * @author abook23
 */
public class DownloadActivity extends Activity {

    public static String URL = "url";
    public static String DIRS_NAME = "path";
    public static String FILE_NAME = "fileName";
    public Button but_cancel, but_task;

    private static String TAG = "DownloadActivity";
    private TextView tv_title, tv_content;
    private ProgressBar progressBar;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int notifyId = 0;
    private Context context;
    private Dialog dialog;
    private String fileName;
    private float mSize, mMax;

    private DownloadService downloadService;
    private UpdateView updateView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        context = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        updateView = new UpdateView();

        initNotify();
        showDialog(context);
        Intent intent = getIntent();
//        String url = intent.getStringExtra(URL);
//        String path = intent.getStringExtra(DIRS_NAME);
        fileName = intent.getStringExtra(FILE_NAME);
        intent.setClass(context, DownloadService.class);
        startService(intent);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        Intent intent = getIntent();
        intent.setClass(context, DownloadService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);

        return super.onCreateView(name, context, attrs);
    }

    public void showDialog(Context context) {
        dialog = new Dialog(context, R.style.my_dialog);
        View view = View.inflate(context, R.layout.dialog_download, null);
        but_cancel = (Button) view.findViewById(R.id.button_cancel);
        but_task = (Button) view.findViewById(R.id.button_task);
        tv_title = (TextView) view.findViewById(R.id.textView_title);
        tv_content = (TextView) view.findViewById(R.id.textView_content);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        dialog.show();
        tv_title.setText("下载提示");
        dialog.setCancelable(false);
        dialog.setContentView(view);

    }

    /**
     * 初始化
     */
    private void initNotify() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                // .setPriority(Notification.PRIORITY_DEFAULT)// 优先级
                .setOngoing(false).setDefaults(Notification.DEFAULT_LIGHTS)// 震动，声音，闪光
                .setSmallIcon(R.mipmap.yes_96);
        showProgressNotify();//显示进度都条
        initContentIntent(DownloadActivity.class);//点击通知栏 跳转
    }

    /**
     * 点击通知栏 跳转
     */
    private void initContentIntent(Class<?> startActivity) {
        Intent intent = new Intent(context, startActivity);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
    }

    /**
     * 显示进度条
     */
    private void showProgressNotify() {
        mBuilder.setContentTitle(fileName).setContentText("0/0");
        mBuilder.setProgress(100, 0, false);
    }

    /**
     * 取消监听
     *
     * @param v
     */
    public void OnCencelClickListener(View v) {

        mNotificationManager.cancel(notifyId);

        downloadService.onCancel();
        Intent intent = getIntent();
        intent.setClass(context, DownloadService.class);
        stopService(intent);

        this.finish();
    }

    /**
     * 后台下载监听
     *
     * @param v
     */
    public void OnTaskClickListener(View v) {
        mNotificationManager.notify(notifyId, mBuilder.build());
        dialog.dismiss();

        this.finish();
    }

    public class UpdateView implements Runnable {
        private float size, max;

        public UpdateView() {
        }

        @Override
        public void run() {
            size = mSize;
            max = mMax;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String _size = decimalFormat.format(size);//format 返回的是字符串
            String _max = decimalFormat.format(max);
            if (dialog.isShowing()) {
                mNotificationManager.cancel(notifyId);
                tv_title.setText("下载中....");
                progressBar.setIndeterminate(false);
                progressBar.setProgress((int) size);
                progressBar.setMax((int) max);
                tv_content.setText(_size + "/" + _max + " MB");
                Log.d(TAG, "下载大小：" + _size + "/" + _max + " MB");
            } else {//通知栏
                Log.d(TAG, "通知栏显示：" + _size + "/" + _max + "MB");
                mBuilder.setProgress((int) max, (int) size, false).setContentText(_size + "/" + _max + "MB"); // 这个方法是显示进度条
                mNotificationManager.notify(notifyId, mBuilder.build());
            }
        }
    }

    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.DownloadBinder) service).getService();
            downloadService.setDownloadListener(downloadListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    DownloadListener downloadListener = new DownloadListener() {
        int count;

        @Override
        public void onStart(float fileByteSize) {
            mMax = fileByteSize / (1024 * 1024);
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onResume() {

        }

        @Override
        public void onSize(float size, float maxSize) {
            mSize = size / (1024 * 1024);
            mMax = maxSize / (1024 * 1024);
            count++;
            if (count > 50) {
                count = 0;
                handler.postDelayed(updateView, 100);
            }
        }

        @Override
        public void onFail() {

        }

        @Override
        public void onSuccess(File file) {
            if (file.getName().endsWith(".apk"))
                downloadService.install(file);
        }

        @Override
        public void onCancel() {

        }
    };
}
