package com.abook23.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yzz on 2015/11/9.
 */
public class KillEmojiEdit extends EditText {

    private String str_b = new String();
    private String beforeS = "";

    private Context mContext;

    public KillEmojiEdit(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }

    public KillEmojiEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEditText();
    }

    public KillEmojiEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEditText();
    }

    // 初始化edittext 控件
    private void initEditText() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                beforeS = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isEmoji(getText().toString())) {
                    str_b = s.toString();


                } else {
                    if (s.length() >= 1) {
                        if (beforeS.equals("")) {
                            setText("");
                        } else {
                            setText(str_b);
                            setSelection(str_b.length());
                        }

                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private static boolean isEmoji(String str) {

        String regex = "[!-~\u4E00-\u9FA5\\s]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        boolean istrue = match.matches();
        if (istrue) {
            return true;
        } else {
            return false;
        }


    }

}

