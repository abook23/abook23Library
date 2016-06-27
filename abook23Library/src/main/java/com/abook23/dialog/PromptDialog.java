package com.abook23.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.abook23.utils.R;

/**
 * 提示 框
 *
 * @author abook23
 * @version 1.0
 */
public class PromptDialog {

    public AlertDialog alertDialog;
    private Context mContext;

    public PromptDialog(Context context) {
        this.mContext = context;
    }

    /**
     * @param msg
     */
    public void showYes(String msg) {
        showYes(msg, R.mipmap.yes_96);
    }

    /**
     * @param msg
     */
    public void showErr(String msg) {
        showErr(msg, R.mipmap.error_96);
    }

    /**
     * @param msg
     * @param resourceId 显示的资源文件Id
     */
    public void showYes(String msg, int resourceId) {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_prompt);
        window.setGravity(Gravity.CENTER);// 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.in_left_out_right_style);
        window.setBackgroundDrawableResource(R.color.transparent);

        ImageView imageView = (ImageView) window.findViewById(R.id.prompt_iv);
        TextView textView = (TextView) window.findViewById(R.id.prompt_msg);
        imageView.setImageResource(resourceId);
        textView.setText(msg);
    }

    /**
     * @param msg
     * @param resourceId 显示的资源文件Id
     */
    public void showErr(String msg, int resourceId) {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_prompt);
        window.setGravity(Gravity.CENTER);// 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.in_left_out_right_style);
        ImageView imageView = (ImageView) window.findViewById(R.id.prompt_iv);
        TextView textView = (TextView) window.findViewById(R.id.prompt_msg);
        imageView.setImageResource(resourceId);
        textView.setText(msg);
    }

    /**
     * 点击退出 默认 true
     *
     * @param @param flag 设定文件
     * @Title: setCancelable
     */
    public void setCancelable(boolean flag) {
        alertDialog.setCancelable(flag);
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
