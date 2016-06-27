package com.abook23.activity.image;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.abook23.adapter.GridViewAdapter;
import com.abook23.base.BaseFragmentActivity;
import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 图片选择
 * Created by abook23 on 2015/10/19.
 */
public class SelectImagesActivity extends BaseFragmentActivity implements AbsListView.OnScrollListener, GridViewAdapter.OnCheckBoxOnListener {

    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    /**
     * 文件夹路径
     */
    public static String PATHS = "paths";

    private ArrayList<String> paths;
    private ImageCacheUtils imageCacheUtils;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private boolean firstLoad = true;

    private Callback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_phono);
        addTitleBarFragment();

        Intent intent = getIntent();
        paths = intent.getStringArrayListExtra(PATHS);

        imageCacheUtils = new ImageCacheUtils(context);

        gridView = (GridView) findViewById(R.id.gridView);
        gridViewAdapter = new GridViewAdapter(context);
        gridView.setAdapter(gridViewAdapter);
        gridViewAdapter.setData(paths);
        gridViewAdapter.notifyDataSetChanged();

        gridViewAdapter.setOnCheckBoxOnListener(this);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(new OnGridViewItemClickListener());

    }

    @Override
    protected void onStart() {
        super.onStart();
        titleBar.submit.setText("完成(" + PhotoActivity.checkNuber + "/" + PhotoActivity.checkCount + ")");
        titleBar.submit.setOnClickListener(new OnSubmitClickListener());
        gridViewAdapter.setCheckImage(PhotoActivity.checkImages);
        gridViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3 && PhotoActivity.checkCount == 1) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                this.finish();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //开始滚动（SCROLL_STATE_FLING），正在滚动(SCROLL_STATE_TOUCH_SCROLL), 已经停止（SCROLL_STATE_IDLE），
        //回调顺序如下
        //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动
        //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）
        //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                showImage(mFirstVisibleItem, mVisibleItemCount);
                break;
            default:
                imageCacheUtils.cancelTask();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mFirstVisibleItem = firstVisibleItem;
        this.mVisibleItemCount = visibleItemCount;
        if (firstLoad && visibleItemCount > 0) {//解决首次不加载
            firstLoad = false;
            showImage(firstVisibleItem, visibleItemCount);
        }
    }

    /**
     * 滑动加载图片
     *
     * @param firstVisibleItem
     * @param visibleItemCount
     */
    private void showImage(int firstVisibleItem, int visibleItemCount) {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            /**
             * 通过 Tag 找到对应 的View
             */
            ImageView imageView = (ImageView) gridView.findViewWithTag("iv" + i);
            if (imageView != null)
                imageCacheUtils.getBitmapLocal(imageView, paths.get(i), 150, 150);
        }

    }

    @Override
    public void onCheckBox(CheckBox checkBox, int position) {
        int checkNumber = PhotoActivity.checkImage(checkBox, paths.get(position), position);
        titleBar.submit.setText("完成(" + checkNumber + "/" + PhotoActivity.checkCount + ")");
        if (checkNumber == PhotoActivity.checkCount) {
            Toast.makeText(context, "最多只能选择" + PhotoActivity.checkCount + "张", Toast.LENGTH_SHORT).show();
        }
        if (PhotoActivity.checkCount == 1) {
            this.finish();
        }
        gridViewAdapter.setCheckImage(PhotoActivity.checkImages);
        gridViewAdapter.notifyDataSetChanged();
    }

    private class OnGridViewItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent = new Intent();
            intent.setClass(context, ImageInfoActivity.class);
            intent.putExtra(ImageInfoActivity.PATH, paths.get(position));
            intent.putExtra("submit", true);
            startActivityForResult(intent, 3);
//          GridViewAdapter.ViewHolder holder = (GridViewAdapter.ViewHolder) view.getTag();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageCacheUtils.clearCache();
    }

    private class OnSubmitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SelectImagesActivity.this.finish();
        }
    }

    /**
     * 回调接口
     */
    public interface Callback {
        void onSingleImageSelected(String path);

        void onImageSelected(String path);

        void onImageUnselected(String path);
    }
}
