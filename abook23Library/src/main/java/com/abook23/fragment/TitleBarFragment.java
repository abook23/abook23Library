package com.abook23.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.abook23.utils.R;


/**
 * Created by abook23 on 2015/7/8.
 */
public class TitleBarFragment extends Fragment {
    public Button submit;
    public ImageView more,back;
    public TextView title;
    public final static int BACK_ID = R.id.iv_back;
    public final static int SUBMIT_ID = R.id.but_submit;
    public final static int MORE_ID = R.id.iv_more;
    public final static int TITLE_ID = R.id.tv_title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_title_bar_lib, container, false);
        back = (ImageView) v.findViewById(R.id.iv_back);
        submit = (Button) v.findViewById(R.id.but_submit);
        more = (ImageView) v.findViewById(R.id.iv_more);
        title = (TextView) v.findViewById(R.id.tv_title);
        back.setOnClickListener(new onBackListener());
        more.setVisibility(View.GONE);
        return v;
    }

    private class onBackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getActivity().finish();
        }
    }


}
