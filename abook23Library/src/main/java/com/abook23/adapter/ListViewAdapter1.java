package com.abook23.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;

/**
 * Created by abook23 on 2015/10/20.
 */
public class ListViewAdapter1 extends BaseAdapter {
    private LayoutInflater inflater;
    private String[] imagePath;
    private String[][] array;
    private ImageCacheUtils imageCacheUtils;

    public ListViewAdapter1(Context context) {
        inflater = LayoutInflater.from(context);
        imageCacheUtils = new ImageCacheUtils(context);
    }

    public void setDate(String[] imagePath, String[][] array) {
        this.imagePath = imagePath;
        this.array = array;
    }

    @Override
    public int getCount() {
        return imagePath == null ? 0 : imagePath.length;
    }

    @Override
    public Object getItem(int position) {
        return imagePath[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.item_list__1, parent, false);
            holder.iv1 = (ImageView) v.findViewById(R.id.imageView1);
            holder.tv1 = (TextView) v.findViewById(R.id.textView1);
            holder.tv2 = (TextView) v.findViewById(R.id.textView2);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        if (imagePath[position].startsWith("http://")) {
            imageCacheUtils.getWebImageView(holder.iv1, imagePath[position], -1, -1, false, 4);
        } else {
            imageCacheUtils.getBitmapLocal(holder.iv1, imagePath[position], 150, 150);
            //holder.iv1.setImageBitmap(imageCacheUtils.getBitmapLocal(imagePath[position], 150, 150));
        }
        holder.tv1.setText(array[position][0]);
        holder.tv2.setText(array[position][1]);

        return v;
    }

    public class ViewHolder {
        public TextView tv1;
        public TextView tv2;
        public ImageView iv1;
    }
}
