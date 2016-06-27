package com.abook23.utils.util;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 校验
 *
 * @author Comsys-anook23
 * @ClassName: CheckUtils
 * @Description: TODO
 * @date 2015-5-28 下午2:38:09
 */
public class CheckUtils {

    /**
     * 密码校验
     * 如果两个密码不相等  清楚第二个密码  并且在第二个密码输入框 setHint("两次密码不一样")
     *
     * @param @param  password1
     * @param @param  password2
     * @param @return 设定文件
     * @return boolean    返回类型
     * @Title: IsPasswordSame
     * @Description: TODO
     */
    public static boolean isPasswordSame(EditText password1, EditText password2) {
        boolean b = true;
        String pas1 = password1.getText().toString();
        String pas2 = password2.getText().toString();
        if (!pas1.equals(pas2)) {
            b = false;
            password2.setText("");
            password2.setHint("两次密码不一样");
            setHintTextColor(password2);
        }
        return b;
    }

    /**
     * 判断textView 是否有空  ( "" || null)
     *
     * @param @param  TextColor android.graphics.Color
     * @param @param  edt
     * @param @return 设定文件
     * @return boolean 返回类型
     * @Title: IsEditTextNull
     * @Description: TODO
     */
    public static boolean isNullTextView(int Color, String msg, TextView... textViews) {
        boolean b = false;
        for (int i = 0; i < textViews.length; i++) {
            if (isNull(textViews[i].getText().toString().trim())) {
                // textViews[i].setText(null);
                if (!isNull(msg)) {
                    textViews[i].setHint(msg);
                }
                if (Color != -1)
                    textViews[i].setHintTextColor(Color);
                return true;
            }
        }
        return b;
    }

    /**
     * 判断textView 是否有空  ( "" || null)
     *
     * @param @param  TextColor android.graphics.Color
     * @param @param  edt
     * @param @return 设定文件
     * @return boolean 返回类型
     * @Title: IsEditTextNull
     * @Description: TODO
     */
    public static boolean isNullEditView(int Color, String msg, EditText... edt) {
        boolean b = false;
        for (int i = 0; i < edt.length; i++) {
            if (isNull(edt[i].getText().toString().trim())) {
                edt[i].setText(null);
                if (!isNull(msg)) {
                    edt[i].setHint(msg);
                }
                if (Color != -1)
                    edt[i].setHintTextColor(Color);
                return true;
            }
        }
        return b;
    }

    public static boolean isNullEditView(EditText... edt) {
        return isNullEditView(-1, "", edt);
    }

    /**
     * 手机号码
     *
     * @param @param  Color
     * @param @param  edt
     * @param @param  errMsg
     * @param @return 设定文件
     * @return boolean    返回类型
     * @Title: IsPhone
     * @Description: TODO
     */
    public static boolean isPhone(int Color, EditText edt, String errMsg) {
        boolean b = true;
        if (edt.getText().toString().length() != 11) {
            edt.setText("");
            if (isNull(errMsg))
                errMsg = "手机号有误";
            edt.setHint(errMsg);
            edt.setHintTextColor(Color);
            b = false;
        }
        return b;
    }

    /**
     * 手机号码
     *
     * @param @param  Color
     * @param @param  edt
     * @param @param  errMsg
     * @param @return 设定文件
     * @return boolean    返回类型
     * @Title: IsPhone
     * @Description: TODO
     */
    public static boolean isPhone(String value) {
        boolean b = true;
        if (value.length() != 11) {
            b = false;
        }
        return b;
    }

    public static boolean isNull(String value) {
        if ("".equals(value) || null == value || value.length() < 1) {
            return true;
        }
        return false;
    }

    /**
     * 不可编辑
     *
     * @param editTexts
     */
    public static void setEnabled(boolean enabled, EditText... editTexts) {
        for (EditText e : editTexts) {
            e.setEnabled(enabled);
        }
    }

    /**
     * 不可编辑
     *
     * @param editTexts
     */
    public static void setEnabled(boolean enabled, TextView... editTexts) {
        for (TextView e : editTexts) {
            e.setEnabled(enabled);
        }
    }

    /**
     * 不可编辑
     *
     * @param editTexts
     */
    public static void setEnabled(boolean enabled, RadioButton... editTexts) {
        for (RadioButton e : editTexts) {
            e.setEnabled(enabled);
        }
    }

    public static boolean email(String value) {
        if (value.contains("@") && value.contains(".com")) {
            return true;
        }
        return false;
    }

    /**
     * 默认红色
     *
     * @param @param edt    设定文件
     * @return void    返回类型
     * @Title: setHintTextColor
     * @Description: TODO
     */
    public static void setHintTextColor(EditText edt) {
        setHintTextColor(Color.RED, edt);
    }

    public static void setHintTextColor(int Color, EditText edt) {
        edt.setHintTextColor(Color);
    }

    /**
     * 带小数点的监听
     */
    public static class DecimalFormatListener implements TextWatcher {
        private EditText editText;
        private int decimal;

        /**
         * @param editText
         * @param decimal  小数点位数
         */
        public DecimalFormatListener(EditText editText, int decimal) {
            this.editText = editText;
            this.decimal = decimal;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String values = s.toString();
            if (values.contains(".")) {
                if ((values.length() - 1) - values.lastIndexOf(".") > decimal) {
                    values = values.substring(0, values.lastIndexOf(".") + decimal + 1);
                    editText.setText(values);
                    editText.setSelection(values.length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
