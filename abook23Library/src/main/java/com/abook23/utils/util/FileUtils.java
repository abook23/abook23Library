package com.abook23.utils.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by abook23 on 2014/9/22. 目录名为项目名
 * /XDWL/[packageName]
 */
public class FileUtils {

    private Context context;
    /**
     * 手机硬缓缓存根目录
     */
    private String mDataRootPath = null;
    /**
     * 硬缓存目录名
     */
    private String CC_FOLDER_NAME = null;

    /**
     * sd卡的根目录
     */
    public String mSdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 软缓存目录名
     */
    // private  String SD_FOLDER_NAME = "/Android/data";
    private String SD_FOLDER_NAME = "/DATA2";

    public boolean isWrite = true;

    /**
     * @param context
     */
    public FileUtils(Context context) {
        this.context = context;
        mDataRootPath = context.getCacheDir().getPath();
        CC_FOLDER_NAME = File.separator + context.getPackageName();
        SD_FOLDER_NAME += File.separator + context.getPackageName();
    }

    /**
     * @param mDriName 在跟目录中的名称
     * @param context
     */
    public FileUtils(String mDriName, Context context) {
        this.context = context;
        mDataRootPath = context.getCacheDir().getPath();
        CC_FOLDER_NAME = mDriName + File.separator + context.getPackageName();
        SD_FOLDER_NAME = mDriName + File.separator + context.getPackageName();
    }

    /**
     * 获取储存的目录
     *
     * @return
     */
    public String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? mSdRootPath + SD_FOLDER_NAME
                : mDataRootPath + CC_FOLDER_NAME;
    }

    private OnOutputStreamListener mOutputStreamListener;

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public File saveBitmap(String filePath, String fileName, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return null;
        }
        File file = createSDFile(filePath, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        String Type = fileName.substring(fileName.lastIndexOf(".") + 1)
                .toUpperCase();
        if ("PNG".equals(Type) || "png".equals(Type)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        }

        fos.flush();
        fos.close();
        return file;
    }

    /**
     * 从手机或者sd卡获取Bitmap 大图片用 BitmapUitls.getSmallBitmap 防止OOM
     *
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String dirsName, String fileName) {
        return BitmapFactory.decodeFile(getStorageDirectory() + dirsName + File.separator
                + fileName);
    }

    /**
     * 获取文件的大小
     *
     * @param fileName
     * @return
     */
    public long getFileSize(String dirsName, String fileName) {
        return new File(getStorageDirectory() + dirsName + File.separator + fileName)
                .length();
    }

    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteFile(String dirsName) {
        File dirFile = new File(getStorageDirectory() + dirsName);
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }
        dirFile.delete();
    }

    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteFile() {
        deleteFile("");
    }

    /**
     * 创建目录
     *
     * @param dirsName 目录
     * @return
     */
    public File createDir(String dirsName) {
        File file = new File(getStorageDirectory() + dirsName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 在ＳＤ卡上 项目根目录 创建文件
     *
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public File createSDFile(String dirsName, String fileName) throws IOException {
        File dir = createDir(dirsName);//创建文件夹
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir.getPath() + File.separator + fileName);
        file.createNewFile();//创建文件
        Log.i("createSDFile", file.getPath());
        return file;
    }

    /**
     * 判断SD 卡上的文件夹内的 文件是否存在
     *
     * @param dirsName
     * @param fileName
     * @return
     */
    public boolean isFileExist(String dirsName, String fileName) {
        File file = new File(getStorageDirectory() + dirsName + File.separator + fileName);
        return file.exists();
    }

    /**
     * 获取文件
     *
     * @param dirsName
     * @param fileName
     * @return
     */
    public File getFile(String dirsName, String fileName) {
        if (isFileExist(dirsName, fileName)) {
            File file = new File(getStorageDirectory() + dirsName + File.separator + fileName);
            return file;
        }
        return null;
    }

    /**
     * 将 InputStream保存到 SD卡中
     * 写入监听 mOutputStreamListener
     *
     * @param dirsName     文件路径
     * @param fileName 文件名
     * @param input    输入流
     * @return
     */
    public File writeSDFormInput(String dirsName, String fileName, InputStream input) {
        File file = null;
        OutputStream outputStream = null;
        try {
            file = createSDFile(dirsName, fileName);
            outputStream = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int length;
            while (isWrite && (length = input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                if (mOutputStreamListener != null) {
                    mOutputStreamListener.onReadLength(length);
                }
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace(); // To change body of catch statement use File |
            // Settings | File Templates.
        } finally {
            try {
                outputStream.close(); // 清除缓存
            } catch (IOException e) {
                e.printStackTrace(); // To change body of catch statement use
                // File | Settings | File Templates.
            }
        }
        return file;
    }

    /**
     * 更新sd文件列表信息
     * </p> 告诉傻缺Android， 我放文件了，请及时显示 扫描文件 防止文件放入磁盘中，未能及时查看
     *
     * @param filePath
     */
    public void scannerFile(String... filePath) {
        MediaScannerConnection.scanFile(context, filePath, null, null);
    }

    public void setOutputStreamListener(OnOutputStreamListener listener) {
        this.mOutputStreamListener = listener;
    }

    public File getResourceFile(String dirsName, String fileName, int resourcesId, int inSampleSize) {
        boolean b = isFileExist(dirsName, fileName);
        if (!b) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inSampleSize = inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourcesId, options);
            try {
                File file = saveBitmap(dirsName, fileName, bitmap);
                scannerFile(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFile(dirsName, fileName);
    }

    /**
     * 默认图片
     *
     * @return
     * @deprecated 请使用 getResourceFile
     */
    public File initDefaultImage(String dirsName, String fileName, int resId) {
        boolean b = isFileExist(dirsName, fileName);
        if (!b) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            try {
                File file = saveBitmap(dirsName, fileName, bitmap);
                scannerFile(file.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFile(dirsName, fileName);
    }

    /**
     * 写入本地速度监听
     *
     * @author abook23
     */
    public interface OnOutputStreamListener {
        /**
         * @param size 写入量
         */
        void onReadLength(int size);
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public void deleteAllFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteAllFile(f);
                }
                file.delete();
            }
        }
    }
}
