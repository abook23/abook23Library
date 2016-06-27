package com.abook23.listener;

import java.util.Objects;

/**
 * Created by My on 2016/3/9.
 */
public interface OnHttpListener {
    /**
     * 失败
     */
    void onFail();

    /**
     * 成功
     */
    void onSuccess(Objects objects);

    /**
     * 取消下载
     */
    void onCancel();
}
