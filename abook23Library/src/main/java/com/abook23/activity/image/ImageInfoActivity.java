package com.abook23.activity.image;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.abook23.base.BaseFragmentActivity;
import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by abook23 on 2015/10/21.
 */
public class ImageInfoActivity extends BaseFragmentActivity {

    public static String PATH = "path";
    public static String PATHS = "paths";
    private ImageCacheUtils imageCacheUtils;
    private String path;
    private boolean showSelect;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);
        addTitleBarFragment();
        showSelect = getIntent().getBooleanExtra("submit", false);
        imageCacheUtils = new ImageCacheUtils(context);
        path = getIntent().getStringExtra(PATH);
        imageView = (ImageView) findViewById(R.id.imageView1);

        if (path != null) {
            if (path.startsWith("http://") || path.startsWith("https://")) {
                imageCacheUtils.getWebImageView(imageView, path, -1, -1, false, 1);
            } else {
                imageCacheUtils.getBitmapLocalNotCache(imageView, path, 0, 0);
            }
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        titleBar.submit.setText("选择");
        titleBar.submit.setOnClickListener(new OnSubmitClickListener());
        titleBar.submit.setVisibility(View.VISIBLE);
        if (!showSelect) {
            titleBar.submit.setVisibility(View.GONE);
        }
    }

    private class OnSubmitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setChecked(true);
            PhotoActivity.checkImage(checkBox, path, 1);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            ImageInfoActivity.this.finish();
        }
    }
}
