package com.abook23.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.abook23.activity.image.ImageInfoActivity;
import com.abook23.activity.image.PhotoActivity;
import com.abook23.fragment.TitleBarFragment;
import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;

import java.util.List;

/**
 * Created by abook23 on 2015/10/21.
 */
public class ListViewH {

    private Context context;
    private LinearLayout linearLayout;
    private LayoutInflater inflater;
    private ImageCacheUtils imageCacheUtils;
    private TitleBarFragment titleBar;

    private List<String> list;

    public ListViewH(Context context, LinearLayout linearLayout, TitleBarFragment titleBar) {
        this.context = context;
        this.linearLayout = linearLayout;
        imageCacheUtils = new ImageCacheUtils(context);
        this.titleBar = titleBar;
    }

    public void setData(List<String> list) {
        this.list = list;
        linearLayout.removeAllViews();
        inflater = LayoutInflater.from(context);
        if (list != null) {
            int m = list.size() > 10 ? 10 : list.size();
            for (int i = 0; i < m; i++) {
                String path = list.get(i);
                View view = inflater.inflate(R.layout.item_image_check, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageView1);
                imageView.setImageBitmap(imageCacheUtils.getBitmapLocal(path, 150, 150));
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                if (PhotoActivity.checkImages.contains(path)) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }
                imageView.setOnClickListener(new OnIVClickListener(i));
                checkBox.setOnClickListener(new OnCBClickListener(checkBox, i));
                linearLayout.addView(view);
            }
        }
    }

    private class OnIVClickListener implements View.OnClickListener {
        private int position;

        public OnIVClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(context, ImageInfoActivity.class);
            intent.putExtra(ImageInfoActivity.PATH, list.get(position));
            intent.putExtra("submit",true);
            //context.startActivity(intent);
            ((Activity)context).startActivityForResult(intent, 3);
        }
    }

    private class OnCBClickListener implements View.OnClickListener {
        private CheckBox checkBox;
        private int position;

        public OnCBClickListener(CheckBox checkBox, int i) {
            this.checkBox = checkBox;
            this.position = i;
        }

        @Override
        public void onClick(View v) {
            //Toast.makeText(context, "选择了第" + (position + 1) + "状态:" + checkBox.isChecked(), Toast.LENGTH_SHORT).show();
            int checkNumber = PhotoActivity.checkImage(checkBox, list.get(position), position);
            titleBar.submit.setText("完成(" + checkNumber + "/" + PhotoActivity.checkCount + ")");
            if (checkNumber == PhotoActivity.checkCount) {
                Toast.makeText(context, "最多只能选择" + PhotoActivity.checkCount + "张", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
