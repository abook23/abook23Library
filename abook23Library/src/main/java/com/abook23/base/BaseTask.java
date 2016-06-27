package com.abook23.base;

import android.content.Context;
import android.os.AsyncTask;

import com.abook23.dialog.LoadingDialog;
import com.abook23.listener.AsyncTaskListener;
import com.abook23.utils.util.ToastUtils;

import java.util.HashMap;

/**
 * Created by abook23 on 2015/12/28.
 */
public abstract class BaseTask extends AsyncTask<String, Integer, Integer> {
    protected HashMap<String, String> hs = new HashMap<>();
    protected AsyncTaskListener listener;
    protected LoadingDialog loadingDialog;
    protected String msg;
    protected Context context;

    public BaseTask(Context context) {
        this.context = context;
    }

    public BaseTask(Context context, AsyncTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (loadingDialog != null && loadingDialog.alertDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        if (integer != null) {
            switch (integer) {
                case 3:
                    msg = "登录超时,请重新登录";
                    break;
                case 400:
                    msg = "请求无效";
                    break;
                case 401:
                    msg = "未授权：登录失败";
                    break;
                case 403:
                    msg = "禁止访问";
                    break;
                case 405:
                    msg = "资源被禁止";
                    break;
                case 500:
                    msg = "内部服务器错误";
                    break;
                case 1001:
                    msg = "客户端异常";
                    break;
                case 1002:
                    msg = "服务器请求超时";
                    break;
                default:
                    if (integer > 200) {
                        msg = "未知错误!";
                    }
            }
            if (msg.length() > 0)
                ToastUtils.debugShow(context, msg);
        }
    }
}

