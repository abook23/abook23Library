package com.abook23.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.abook23.activity.image.PhotoActivity;
import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;

import java.util.List;

/**
 * Created by abook23 on 2015/10/19.
 */
public class GridViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> files;
    private List<String> list_checkPath;
    private ImageCacheUtils imageCacheUtils;
    private OnCheckBoxOnListener onCheckBoxOnListener;
    private int on;

    public GridViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        imageCacheUtils = new ImageCacheUtils(context);
    }

    public void setData(List<String> files) {
        this.files = files;
    }

    public void setCheckImage(List<String> list) {
        list_checkPath = list;
    }

    @Override
    public int getCount() {
        return files == null ? 0 : files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        if (v == null) {
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.item_gridview_1, parent, false);
            holder.iv = (ImageView) v.findViewById(R.id.gridView_iv);
            holder.cb = (CheckBox) v.findViewById(R.id.gridView_cb);
            holder.tv = (TextView) v.findViewById(R.id.select_count_text);
            if (PhotoActivity.checkCount == 1) {
                holder.cb.setVisibility(View.GONE);
            }
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        /**
         * 给imageView 赋 一个标签
         */
        holder.iv.setTag("iv" + position);
        holder.cb.setTag("cb" + position);
        String filePath = files.get(position);
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        imageCacheUtils.getCacheImage(holder.iv, fileName);
        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCheckBoxOnListener != null)
                    onCheckBoxOnListener.onCheckBox(holder.cb, position);
            }
        });
        if (list_checkPath != null && list_checkPath.contains(files.get(position))) {
            on = list_checkPath.indexOf(files.get(position)) + 1;
            holder.tv.setText(String.valueOf(on));
            holder.cb.setChecked(true);
        } else {
            holder.tv.setText(null);
            holder.cb.setChecked(false);
        }
        return v;
    }

    public class ViewHolder {
        public ImageView iv;
        public CheckBox cb;
        public TextView tv;
    }

    public void setOnCheckBoxOnListener(OnCheckBoxOnListener onCheckBoxOnListener) {
        this.onCheckBoxOnListener = onCheckBoxOnListener;
    }

    public interface OnCheckBoxOnListener {
        void onCheckBox(CheckBox checkBox, int position);
    }
}
