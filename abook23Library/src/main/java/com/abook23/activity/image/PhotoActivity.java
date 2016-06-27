package com.abook23.activity.image;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.abook23.adapter.ListViewAdapter1;
import com.abook23.base.BaseFragmentActivity;
import com.abook23.dialog.LoadingDialog;
import com.abook23.utils.R;
import com.abook23.utils.bitmap.ImageCacheUtils;
import com.abook23.utils.phone.CameraUtil;
import com.abook23.utils.util.FileUtils;
import com.abook23.widget.ListViewByScrollview;
import com.abook23.widget.ListViewH;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PhotoActivity extends BaseFragmentActivity implements AdapterView.OnItemClickListener {

    private static final int RESULT_CAMERA = 100;
    public static String CHECK_COUNT = "checkCount";//能选中多少张
    public static String CHECK_PATH = "path";//被选中的图片
    public static String DATA = "data";//返回值

    /**
     * 已选择的图片
     */
    public static ArrayList<String> checkImages;
    public static int checkNuber;//选择数量
    public static int checkCount;//总数

    private ListViewAdapter1 listViewAdapter;
    private ListView listView;
    private LinearLayout linearLayout;
    private Handler mHandler;
    private HashMap<String, ArrayList<String>> map_folder;
    private List<File> list_image = new ArrayList<>();//图片
    private ArrayList<String> recentlyImage = new ArrayList<>();//最近的照片
    private ArrayList<View> cameraImages;
    private ArrayList<String> list_check_path;
    private ArrayList<String> list_all_path;
    private int SCAN_OK = 100;
    private LoadingDialog loadingDialog;
    private FileUtils fileUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        addTitleBarFragment();
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);

        checkNuber = 0;
        fileUtils = new FileUtils(context);
        checkImages = new ArrayList<>();

        listViewAdapter = new ListViewAdapter1(context);
        listView = (ListViewByScrollview) findViewById(R.id.listView);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();
        checkCount = intent.getIntExtra(CHECK_COUNT, 9);//选择数量
        list_check_path = intent.getStringArrayListExtra(CHECK_PATH);//已经选择的图片

        initUI();
        selectImage();
    }

    protected void initUI() {
        loadingDialog = new LoadingDialog(context);
        loadingDialog.show("加载中....");
    }

    @Override
    protected void onStart() {
        super.onStart();
        titleBar.submit.setText("完成(" + PhotoActivity.checkNuber + "/" + PhotoActivity.checkCount + ")");
        titleBar.submit.setOnClickListener(new OnSubmitClickListener());
        for (String path : checkImages) {
            if (recentlyImage.contains(path)) {
                recentlyImage.remove(path);
            }
            recentlyImage.add(0, path);
        }
        ListViewH listViewH = new ListViewH(context, linearLayout, titleBar);
        listViewH.setData(recentlyImage);
        linearLayout.addView(getCameraView(), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CAMERA) {
            if (resultCode == RESULT_OK) {
                String path = CameraUtil.newInstance().path;
                MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
                if (checkCount == 1) {//只选一张图片时
                    checkImages.add(path);
                    onResult();
                } else
                    selectImage();


            } else {
                CameraUtil.newInstance().delete();
            }
        }
        if (requestCode == 3 && checkCount == 1) {
            if (resultCode == RESULT_OK) {
                onResult();
            }
        }
    }

    protected void selectImage() {
        map_folder = new HashMap<>();//所有图片
        cameraImages = new ArrayList<>();//图片
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == SCAN_OK) {
                    loadingDialog.dismiss();
                    setData();
                }
            }
        };
        // new Thread(new PathImageThread("/DCIM/Camera")).start();
        new Thread(new AllImageThread()).start();
    }

    private void setData() {
        String imagePath[] = new String[map_folder.size()];
        String array[][] = new String[map_folder.size()][2];
        //map_folder
        Iterator<Map.Entry<String, ArrayList<String>>> iterator = map_folder.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = iterator.next();
            array[i][0] = entry.getKey();
            array[i][1] = map_folder.get(entry.getKey()).size() + "张";
            imagePath[i] = entry.getValue().get(0);
            i++;
        }
        listViewAdapter.setDate(imagePath, array);
        listViewAdapter.notifyDataSetChanged();

        recentlyImage.clear();
        if (map_folder.get("Camera") == null)
            return;
        int m = map_folder.get("Camera").size() > 10 ? 10 : map_folder.get("Camera").size();
        for (i = 0; i < m; i++) {
            recentlyImage.add(map_folder.get("Camera").get(i));
        }
        for (String path : checkImages) {
            if (recentlyImage.contains(path)) {
                recentlyImage.remove(path);
            }
            recentlyImage.add(1, path);
        }
        ListViewH listViewH = new ListViewH(context, linearLayout, titleBar);
        listViewH.setData(recentlyImage);
        linearLayout.addView(getCameraView(), 0);
    }

    /**
     * 相机
     *
     * @return
     */
    private View getCameraView() {

        View view = getLayoutInflater().inflate(R.layout.layout_camera, null);
        view.setOnClickListener(new onLinearLayoutItemClick(0));
        return view;
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.asv);
//        ImageView imageView = new ImageView(context);
//        int w = (int) getResources().getDimension(R.dimen.camera_view_w);
//        int p = (int) getResources().getDimension(R.dimen.camera_view_padding);
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(w, w));
//        imageView.setPadding(p, p, p, p);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        imageView.setBackgroundColor(Color.BLACK);
//        imageView.setImageBitmap(bitmap);
//        imageView.setOnClickListener(new onLinearLayoutItemClick(0));
//        return imageView;
    }


    /**
     * 图库中的照相机
     *
     * @param fileName
     * @return
     */
    private File getCameraPath(String fileName) {
        //return fileUtils.initDefaultImage("", fileName, R.mipmap.camera);
        return fileUtils.getResourceFile("", fileName, R.mipmap.asv, 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListViewAdapter1.ViewHolder holder = (ListViewAdapter1.ViewHolder) view.getTag();
        Intent intent = new Intent();
        intent.setClass(context, SelectImagesActivity.class);
        String key = holder.tv1.getText().toString();
        ArrayList<String> list = map_folder.get(key);
        intent.putStringArrayListExtra(SelectImagesActivity.PATHS, list);
        startActivityForResult(intent, 3);
        //startActivity(intent);
    }

    ;

    /**
     * 文件夹下的图片
     */
    private class PathImageThread implements Runnable {
        private String path;

        public PathImageThread(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            //获取存储卡路径
            final String mCardPath = Environment.getExternalStorageDirectory().getPath() + path;
            File file = new File(mCardPath);
            //文件过滤器
            MyFilenameFilter myFilenameFilter = new MyFilenameFilter(new String[]{".jpg"});
            File[] files = file.listFiles(myFilenameFilter);
            //File[] files = file.listFiles();
            if (files != null) {
                for (File mFile : files) {
                    list_image.add(mFile);
                }
                Collections.sort(list_image, new FileComparator());//排序
            }
            //图库中的照相机
            File cameraFile = getCameraPath("camera");
            list_image.add(0, cameraFile);
            mHandler.sendEmptyMessage(SCAN_OK);
        }
    }

    //所有图片
    private class AllImageThread implements Runnable {

        @Override
        public void run() {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver mContentResolver = getContentResolver();
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
            Cursor cursor = mContentResolver.query(uri, projection,
                    MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg"},
                    MediaStore.Images.Media.DATE_MODIFIED + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //图片父路径
                    String parenName = new File(path).getParentFile().getName();
                    if ("imageCache".equals(parenName))
                        continue;
                    //map_folder.put(parenName, path);

                    //------ <code></code>
                    ArrayList<String> files = map_folder.get(parenName);
                    if (files != null) {
                        files.add(path);
                    } else {
                        files = new ArrayList<>();
                        files.add(path);
                        map_folder.put(parenName, files);
                    }
                }
                cursor.close();
            }
            mHandler.sendEmptyMessage(SCAN_OK);
        }
    }

    /**
     * 文件排序
     */
    public class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() < rhs.lastModified()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * 文件过滤
     */
    public class MyFilenameFilter implements FilenameFilter {
        private String[] types;

        public MyFilenameFilter(String[] types) {
            this.types = types;
        }

        @Override
        public boolean accept(File dir, String filename) {
            for (String type : types) {
                return filename.endsWith(type);
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * linearLayout 图片点击
     */
    private class onLinearLayoutItemClick implements View.OnClickListener {
        private int position;

        public onLinearLayoutItemClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (position) {
                case 0:
                    CameraUtil.newInstance().startCamera(PhotoActivity.this, RESULT_CAMERA);
                    break;
            }
        }
    }

    /**
     * @param checkBox
     * @param imagePath
     * @param position
     * @return
     */
    public static int checkImage(CheckBox checkBox, String imagePath, int position) {
        if (checkBox.isChecked() && checkNuber >= checkCount) {
            checkBox.setChecked(false);
            return checkNuber;
        }
        if (checkBox.isChecked()) {
            checkNuber++;
            PhotoActivity.checkImages.add(imagePath);
        } else {
            checkNuber--;
            PhotoActivity.checkImages.remove(imagePath);
        }
        return checkNuber;
    }

    /**
     * 图片裁剪
     *
     * @param uri
     */
    public static Intent startPhotoZoom(Uri uri) {
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
        return intent;
    }

    private class OnSubmitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onResult();
        }
    }

    private void onResult() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(DATA, checkImages);
        setResult(Activity.RESULT_OK, intent);
        PhotoActivity.this.finish();
    }
}
