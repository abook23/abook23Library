package com.abook23.utils.view;

import android.widget.AbsListView;

import com.abook23.interfaces.ScrollListener;

/**
 * Created by abook23 on 2015/9/9.
 *
 * @author abook23 abook23@163.com
 * @version 1.0
 */
public class OnListViewScrollListener implements AbsListView.OnScrollListener {

    private int page;
    private boolean isLastRow;
    private boolean isFirst = true;

    private ScrollListener scrollListener;

    public OnListViewScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void resetPage(int index){
        this.page = index;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (isLastRow) {
                isLastRow = false;
                listener();
            }
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
//            if (scrollListener != null) {
//                scrollListener.onScrollMove();
//            }
        } else {
            if (scrollListener != null) {
                scrollListener.onScrollStop();
            }
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            if (scrollListener != null) {
                scrollListener.onScrollMove();
            }
        } else {
//            if (scrollListener != null) {
//                scrollListener.onScrollStop();
//            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && visibleItemCount < totalItemCount) {
            isLastRow = true;
        }
        if (isFirst) {
            isFirst = false;
            listener();
        }
    }

    private void listener() {
        if (scrollListener != null) {
            scrollListener.onBottom(++page);
        }
    }
}
