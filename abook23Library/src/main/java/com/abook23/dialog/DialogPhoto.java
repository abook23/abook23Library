package com.abook23.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.abook23.utils.R;

/**
 * Created by abook23 on 2014/12/11.
 */
public class DialogPhoto {
    public Context context;
    public AlertDialog dialog;

    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果
    public Uri photoUri;

    /**
     * context is getActivity, not getApplicationContext(); AlertDialog dialog =
     * new AlertDialog.Builder(context).create(); DialogPhoto dialogPhoto = new
     * DialogPhoto(context, dialog);
     * <p/>
     * 创建一个新的实例 DialogPhoto.
     * <p>
     * Title:
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param context
     * @param dialog
     */
    public DialogPhoto(Context context, AlertDialog dialog) {
        this.context = context;
        this.dialog = dialog;

        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.layout_find_image);
        window.setGravity(Gravity.BOTTOM);// 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.from_botton_out_bottom);
        window.setBackgroundDrawableResource(R.color.transparent);

        Button but_zxj = (Button) window.findViewById(R.id.img_but_zxj);
        Button but_tk = (Button) window.findViewById(R.id.img_but_tk);
        Button but_back = (Button) window.findViewById(R.id.img_but_back);
        but_zxj.setOnClickListener(new onCameraListener());
        but_tk.setOnClickListener(new onPictrueListener());
        but_back.setOnClickListener(new onBackListener());
    }

    public class onBackListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            dialog.dismiss();
        }

    }

    public class onPictrueListener implements OnClickListener {

        @Override
        public void onClick(View v) { // TODO Auto-generated method stub
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);// 调用android的图库
            ((Activity) context).startActivityForResult(intent, 2);
        }
    }

    public class onCameraListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                    String.valueOf(System.currentTimeMillis()) + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            photoUri = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);// 旋转
            // String path =
            // Environment.getExternalStorageDirectory().toString()+"/Image"+"/"+System.nanoTime()+".jpg";
            // File file = new File(path);
            // photoUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 这样就将文件的存储方式和uri指定到了Camera应用中
            Log.i("path", photoUri.getPath());
            /*----------------------------------------*/
            ((Activity) context).startActivityForResult(intent, 1);
        }

    }

    /**
     * 图片裁剪
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        ((Activity) context).startActivityForResult(intent, PHOTORESOULT);
    }

    public String getPath() {
        return getPathFromUri(context, photoUri);
    }

    private String getPathFromUri(Context mContext, Uri contentUri) {
        Log.i("dingdongkai", "contentUri===" + contentUri.toString());
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(mContext, contentUri, proj,
                null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
