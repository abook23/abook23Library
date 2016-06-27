package com.abook23.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;
import com.abook23.utils.util.LogUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by abook23 on 2015/9/16.
 */
public class ViewPagerAdapter extends  PagerAdapter
        implements ViewPager.OnPageChangeListener {
    private static final String TAG = "AdapterCycle";
    private Context mContext; // 上下文
    private LayoutInflater mInflater; // 用于解XML
    private LinkedList<View> mViews; //
    private List<Drawable> mList; // 数据源
    private ViewPager mViewPager; //页面


    public ViewPagerAdapter(Context context, ViewPager viewPager,
                        List<Drawable> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mViewPager = viewPager;
        if (list != null) {
            //无论是否多于1个，都要初始化第一个（index:0）
            mViews = new LinkedList<View>();
            ImageView view = (ImageView) mInflater.inflate( R.layout.activity_main_item_cycle, null);
            Drawable drawable = list.get(list.size() - 1);
            view.setImageDrawable(drawable);
            mViews.add(view);
            //注意，如果不只1个，mViews比mList多两个（头尾各多一个）
            //假设：mList为mList[0~N-1], mViews为mViews[0~N+1]
            // mViews[0]放mList[N-1], mViews[i]放mList[i-1], mViews[N+1]放mList[0]
            // mViews[1~N]用于循环；首位之前的mViews[0]和末尾之后的mViews[N+1]用于跳转
            // 首位之前的mViews[0]，跳转到末尾（N）；末位之后的mViews[N+1]，跳转到首位（1）
            if (list.size() > 1) { //多于1个要循环
                for (Drawable d : list) { //中间的N个（index:1~N）
                    ImageView v = (ImageView) mInflater.inflate(
                            R.layout.activity_main_item_cycle, null);
                    v.setImageDrawable(d);
                    mViews.add(v);
                }
                //最后一个（index:N+1）
                view = (ImageView) mInflater.inflate(
                        R.layout.activity_main_item_cycle, null);
                drawable = mList.get(0);
                view.setImageDrawable(drawable);
                mViews.add(view);
            }
        }
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        container.addView(view);
        return view;
    }


    // 实现ViewPager.OnPageChangeListener接口
    @Override
    public void onPageSelected(int position) {
        LogUtils.i("onPageSelected:" + position);
        if (mViews.size() > 1) { //多于1，才会循环跳转
            if (position < 1) { //首位之前，跳转到末尾（N）
                position = mList.size(); //注意这里是mList，而不是mViews
                mViewPager.setCurrentItem(position, false);
            } else if (position > mList.size()) { //末位之后，跳转到首位（1）
                mViewPager.setCurrentItem(1, false); //false:不显示跳转过程的动画
                position = 1;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // 什么都不干
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // 什么都不干
    }
}
