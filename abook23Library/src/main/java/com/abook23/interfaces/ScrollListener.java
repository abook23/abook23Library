package com.abook23.interfaces;

/**
 * Created by abook23 on 2015/9/9.
 *
 * @author abook23 abook23@163.com
 * @version 1.0
 */
public interface ScrollListener {
    void onBottom(int position);

    void onScrollMove();

    void onScrollStop();

}
