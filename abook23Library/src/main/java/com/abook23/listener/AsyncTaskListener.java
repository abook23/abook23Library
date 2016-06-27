package com.abook23.listener;

/**
 * Created by abook23 on 2015/12/29.
 */
public interface AsyncTaskListener {
    /**
     * 成功
     */
    void onHttpSuccess(Object data, int requestCode);

    /**
     * 失败
     */
    void onHttpError(String msg, int resultCode, int requestCode);
}
