package com.abook23.utils.bitmap;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.abook23.utils.task.PauseOnScrollHandler;

/**
 * Created by abook23 on 2015/9/7.
 */
public class PauseOnScrollListener implements OnScrollListener {

    private PauseOnScrollHandler mTaskHandler;
    private boolean firstLoad = true;
    private int mFirstVisibleItem, mVisibleItemCount;
    private OnScrollListener mOnScrollListener;

    public PauseOnScrollListener(PauseOnScrollHandler taskHandler) {
        this(taskHandler, null);
    }

    public PauseOnScrollListener(PauseOnScrollHandler taskHandler, OnScrollListener onScrollListener) {
        mTaskHandler = taskHandler;
        mOnScrollListener = onScrollListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE://停止
                mTaskHandler.resume();
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://滚动
                mTaskHandler.cancel();
                break;
            case OnScrollListener.SCROLL_STATE_FLING://惯性滚动
                mTaskHandler.cancel();
                break;
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mFirstVisibleItem = firstVisibleItem;
        this.mVisibleItemCount = visibleItemCount;
        if (firstLoad && visibleItemCount > 0) {//解决首次不加载
            firstLoad = false;
            mTaskHandler.resume();
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
