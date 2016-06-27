package com.abook23.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;

import com.abook23.utils.R;

/**
 * 日期
 *
 * @author abook23@163.com
 * @ClassName: DateDialog
 * @Description: TODO
 * @date 2015-6-15 下午3:43:14
 */
public class DateDialog {

    public DatePicker datePicker;
    public Button but_ok;
    private AlertDialog dialog;

    public AlertDialog Show(Context context) {
        dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.layout_date_time);
        window.setGravity(Gravity.CENTER);// 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.in_left_out_right_style);
        window.setBackgroundDrawableResource(R.color.transparent);

        datePicker = (DatePicker) window
                .findViewById(R.id.date_time_datePicker1);
        datePicker.setCalendarViewShown(false);
        but_ok = (Button) window.findViewById(R.id.date_time_but_ok);
        return dialog;
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
