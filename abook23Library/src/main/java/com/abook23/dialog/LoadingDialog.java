package com.abook23.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

import com.abook23.utils.R;

/**
 * 等待dialog
 *
 * @author abook23@163.com
 * @ClassName: LoadingDialog
 * @Description: TODO
 * @date 2015-6-15 下午3:42:15
 */
public class LoadingDialog {

    public AlertDialog alertDialog;
    private Context mContext;
    public TextView MsgTextView;

    public LoadingDialog(Context context) {
        this.mContext = context;
    }

    public Dialog show(String msg) {
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_loading);
        window.setGravity(Gravity.CENTER);// 此处可以设置dialog显示的位置
        //window.setWindowAnimations(R.style.DialogAnimation);
        window.setBackgroundDrawableResource(R.color.transparent);
        window.setDimAmount(0f);//覆盖成透明度

        MsgTextView = (TextView) window.findViewById(R.id.textView1);
        MsgTextView.setText(msg);
        //alertDialog.setCancelable(false);// 点击退出
        return alertDialog;
    }

    public void dismiss() {
        if (alertDialog.isShowing())
            alertDialog.dismiss();
    }

    public void setCancelable(boolean b) {
        alertDialog.setCancelable(b);// 点击退出 f
    }

}
