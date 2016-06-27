package com.abook23.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * @author abook23
 * @version 1.0
 *
 */
public class NotificationManagerUtils {

	private Context context;
	public NotificationManagerUtils(Context context){
		this.context = context;
	}
	/**
     * 在状态栏显示通知
     */
    public void showNotification(Class<?> class1){

    	/*Notification noti = new Notification.Builder(context)
        .setContentTitle("New mail from " )
        .setContentText("2312")
        .setSmallIcon(R.drawable.tbhost_dt).build();*/

        // 创建一个NotificationManager的引用   
        NotificationManager notificationManager = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 定义Notification的各种属性   
        Notification notification =new Notification(0,
                "督导系统", System.currentTimeMillis());
        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        //FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
        //FLAG_ONGOING_EVENT 通知放置在正在运行
        //FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中   
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用   
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
        //DEFAULT_LIGHTS  使用默认闪光提示
        //DEFAULT_SOUNDS  使用默认提示声音
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_LIGHTS;
        //叠加效果常量
        //notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000; //闪光时间，毫秒

        // 设置通知的事件消息   
        CharSequence contentTitle ="督导系统标题"; // 通知栏标题
        CharSequence contentText ="督导系统内容"; // 通知栏内容
        Intent notificationIntent =new Intent(context, class1); // 点击该通知后要跳转的Activity
//        PendingIntent contentItent =PendingIntent.getActivity(context, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(context, contentTitle, contentText, contentItent);

        // 把Notification传递给NotificationManager   
        notificationManager.notify(0, notification);
    }

    /**
     * 删除通知
     */
    private void clearNotification(){
        // 启动后删除之前我们定义的通知   
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

    }
}
