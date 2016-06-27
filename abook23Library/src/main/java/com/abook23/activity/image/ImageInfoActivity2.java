package com.abook23.activity.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.abook23.adapter.ViewPagerAdapter;
import com.abook23.base.BaseFragmentActivity;
import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by abook23 on 2015/10/21.
 */
public class ImageInfoActivity2 extends BaseFragmentActivity {

    public static String PATH = "path";
    public static String PATHS = "paths";
    private ImageCacheUtils imageCacheUtils;
    private String path;
    private List<String> paths;
    private boolean showSelect;
    private ImageView imageView;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);
        addTitleBarFragment();
        showSelect = getIntent().getBooleanExtra("submit", false);
        imageCacheUtils = new ImageCacheUtils(context);
        path = getIntent().getStringExtra(PATH);
        paths = getIntent().getStringArrayListExtra(PATHS);
        imageView = (ImageView) findViewById(R.id.imageView1);
       // viewPager = (ViewPager) findViewById(R.id.viewPager);
        //viewPagerAdapter = new ViewPagerAdapter(imageCacheUtils,getImageData(),paths);
        viewPager.setAdapter(viewPagerAdapter);

//        if (path.startsWith("http://")) {
//            Bitmap bitmap = imageCacheUtils.getWebImageView(imageView, path, -1, -1, false, 1);
//            if (bitmap != null)
//                imageView.setOnTouchListener(new OnTouchListener(imageView, bitmap));
//        } else {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Bitmap bitmap = BitmapUitls.getSmallBitmap(path);
//                    Message message = Message.obtain();
//                    message.obj = bitmap;
//                    handler.sendMessage(message);
//                }
//            }).start();
//        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = (Bitmap) msg.obj;
            imageView.setImageBitmap(bitmap);
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
        }
    };

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

    private List<ImageView> getImageData() {
        List<ImageView> imageViews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
            imageCacheUtils.getBitmapLocalNotCache(imageView, paths.get(i), 0, 0);
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
        }
        return imageViews;
    }

    private class OnSubmitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setChecked(true);
            PhotoActivity.checkImage(checkBox, path, 1);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            ImageInfoActivity2.this.finish();
        }
    }
}
