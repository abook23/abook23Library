package com.abook23.dialog;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.abook23.utils.R;

/**
 * Created by yzz on 2015/11/11.
 */
public class SlidingInAndOutDialogFragment extends DialogFragment {


    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null) {
            return;
        }

        getDialog().getWindow().setWindowAnimations(
                R.style.DialogAnimation);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_loading, container,
                false);
//        View view = inflater.inflate(R.layout.dialog_loading, null);
//        button = (Button) v.findViewById(R.id.button1);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("debug", "click");
//            }
//        });
        return view;
    }


}