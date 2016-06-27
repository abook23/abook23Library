package com.abook23.utils.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.abook23.utils.util.Encode;
import com.abook23.utils.util.FileUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片双缓存
 *
 * @author abook23 2014年10月20日09:37:36
 *         图片缓存路径  基于 FileUtils类 的路径   ---> 根目录/XDWL/[packageName] ;
 *         根目录/XDWL/[packageName]/imageCache
 * @author abook23 2015-9-6 10:37:35
 *         1.修复了内存溢出问题
 *         2.修复设置存储路径  /[根目录]/XDWL/[packageName]/imageCache
 * @version 1.5
 */
public class ImageCacheUtils {

    private final String TAG = "ImageCacheUtils";
    private Context context = null;
    private FileUtils fileUtils = null;
    private String dirName = "/imageCache";//  /XDWL/[packageName]/imageCache
    private HashMap<String, String> errorUrl = new HashMap<>();
    ;
    /**
     * 缓存Image的类，当存储Image的大小大于LruCache设定的值，系统自动释放内存
     */
    private LruCache<String, Bitmap> lruCache = null;

    private int POOL_SIZE = 1;
    // 获取当前系统的CPU 数目
    private int cpuNums = Runtime.getRuntime().availableProcessors();
    private int nThreads = POOL_SIZE * cpuNums;
    // ExecutorService通常根据系统资源情况灵活定义线程池大小
    private ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    public ImageCacheUtils(Context context) {
        this.context = context;
        fileUtils = new FileUtils(context);
        // 获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 给LruCache分配1/8 4M
        int mCacheSize = maxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(mCacheSize) {

            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

        };
    }

    /**
     * 获取线程池的方法，因为涉及到并发的问题，我们加上同步锁
     *
     * @return
     */
    private ExecutorService getThreadPool() {
        if (executorService == null) {
            synchronized (ExecutorService.class) {
                if (executorService == null) {
                    // 为了下载图片更加的流畅，我们用了2个线程来下载图片
                    executorService = Executors.newFixedThreadPool(nThreads);
                }
            }
        }
        return executorService;

    }

    /**
     * 取消正在下载的任务
     */
    public synchronized void cancelTask() {
        Log.d(TAG, "取消下载");
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
            executorService = Executors.newFixedThreadPool(nThreads);
        }
    }

    /**
     * 添加到硬件缓存
     *
     * @param key
     * @param bitmap
     * @return 成功 true
     */
    private boolean setLruCache(String key, Bitmap bitmap) {
        if (getLruCache(key) == null && bitmap != null) {
            lruCache.put(key, bitmap);
            return true;
        }
        return false;
    }

    /**
     * 获取硬件缓存图片
     *
     * @param key
     * @return
     */
    private Bitmap getLruCache(String key) {
        return lruCache.get(key);
    }

    public void clearCache() {
        if (lruCache != null) {
            if (lruCache.size() > 0) {
                Log.d("CacheUtils",
                        "mMemoryCache.size() " + lruCache.size());
                lruCache.evictAll();
                Log.d("CacheUtils", "mMemoryCache.size()" + lruCache.size());
            }
            lruCache = null;
        }
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public File setSDCache(String fileName, Bitmap bitmap) throws IOException {
        if (bitmap == null) {
            return null;
        }
        Log.i("CacheImage", "保存到硬缓存" + fileName);
        setLruCache(fileName, bitmap);//保存到硬件缓存

        // 保存到本地
        String filePath = fileUtils.createDir(dirName).getPath();
        File file = new File(filePath + File.separator + fileName);
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        String Type = fileName.substring(fileName.lastIndexOf(".") + 1)
                .toUpperCase();
        if ("PNG".equals(Type)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        fos.flush();
        fos.close();
        Log.d(TAG, "保存到软缓存" + fileName);
        fileUtils.scannerFile(file.getPath());
        return file;
    }

    /**
     * 获取SD 缓存图片
     *
     * @return
     */
    private Bitmap getSDCache(String fileName) {
        if (fileUtils.isFileExist(dirName, fileName) && fileUtils.getFileSize(dirName, fileName) != 0) {
            Bitmap bitmap = fileUtils.getBitmap(dirName, fileName);
            return bitmap;
        }
        return null;
    }

    /**
     * 先到硬缓存中寻找,若没有,再到软缓存中加载
     *
     * @param key
     * @return
     */
    private Bitmap getCacheImage(final String key) {
        Bitmap bitmap = getLruCache(key);// 硬缓
        if (bitmap != null) {
            Log.d(TAG, "在硬缓存找到图片");
            return bitmap;
        }
        bitmap = getSDCache(key);// 软缓
        if (bitmap != null) {
            Log.d(TAG, "在软缓存找到图片");
            setLruCache(key, bitmap); //保存到软缓存中
            return bitmap;
        }
        return null;
    }

    public Bitmap getCacheImage(final ImageView imageView, final String key) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                imageView.setImageBitmap(bitmap);
            }
        };
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getCacheImage(key);
                Message msg = Message.obtain();
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        });
        return null;
    }

    /**
     * 压缩比例
     *
     * @param inSampleSize
     * @return
     */
    private BitmapFactory.Options getOptions(int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;//用于存储Pixel的内存空间在系统内存不足时可以被回收
        options.inSampleSize = inSampleSize;// 4 ..... 1/4 压缩
        return options;
    }

    /**
     * 从Url中获取Bitmap
     *
     * @param url
     * @return
     */
    @Deprecated
    private Bitmap getBitmapFormUrl1(String url, int inSampleSize) {

        String type = url.substring(url.lastIndexOf("."));
        final String key = Encode.getEncode("MD5", url) + type;

        Bitmap bitmap = null;
        HttpURLConnection con = null;
        try {
            URL mImageUrl = new URL(url);
            con = (HttpURLConnection) mImageUrl.openConnection();
            con.setConnectTimeout(10 * 1000);
            con.setReadTimeout(10 * 1000);
            con.setDoInput(true);
            con.setDoOutput(true);
            int l = con.getContentLength();
            InputStream inputStream = con.getInputStream();
            bitmap = BitmapFactory.decodeStream(con.getInputStream(), null, getOptions(inSampleSize));
            Log.d("getBitmapFormUrl", "下载成功...url:" + url);
        } catch (Exception e) {
            Log.d("getBitmapFormUrl", "下载失败...url:" + url);
            errorUrl.put(key, url);
            bitmap = null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return bitmap;
    }

    private Bitmap getBitmapFormUrl(String url, int inSampleSize) {

        String type = url.substring(url.lastIndexOf("."));
        final String key = Encode.getEncode("MD5", url) + type;
        Bitmap bitmap = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            // 请求超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                float l = entity.getContentLength();
                int s = (int) (l / (1024 * 1024)) * 4;
                inSampleSize = inSampleSize > s ? inSampleSize : s;
                bitmap = BitmapFactory.decodeStream(entity.getContent(), null, getOptions(inSampleSize));
            } else {
                Log.d("getBitmapFormUrl", "下载失败...url:" + url);
                errorUrl.put(key, url);
            }
            Log.d("getBitmapFormUrl", "下载成功...url:" + url);
        } catch (Exception e) {
            Log.d("getBitmapFormUrl", "下载失败...url:" + url);
            errorUrl.put(key, url);
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 本地图片
     *
     * @param @param  filePath
     * @param @return 设定文件
     * @return Bitmap 返回类型
     * @Title: getBitmapLocal
     * @Description: TODO
     */
    public Bitmap getBitmapLocal(final String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Bitmap bitmap = getCacheImage(fileName);
        if (bitmap != null)
            return bitmap;
        bitmap = BitmapUitls.getSmallBitmap(filePath, context);
        try {
            setSDCache(fileName, bitmap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 本地文件
     *
     * @param filePath     图片物理路径
     * @param inSampleSize 压缩比例
     * @return Bitmap
     */
    public Bitmap getBitmapLocal(final String filePath, int inSampleSize) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Bitmap bitmap = getCacheImage(fileName);
        if (bitmap != null)
            return bitmap;
        bitmap = BitmapFactory.decodeFile(filePath, getOptions(inSampleSize));
        try {
            setSDCache(fileName, bitmap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 自定义 宽高 压缩
     *
     * @param filePath
     * @param width
     * @param high
     * @return
     */
    public Bitmap getBitmapLocal(final String filePath, int width, int high) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Bitmap bitmap = getCacheImage(fileName);
        if (bitmap != null)
            return bitmap;
        bitmap = BitmapUitls.getSmallBitmap(filePath, width, high);
        try {
            setSDCache(fileName, bitmap);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap getBitmapLocal(final ImageView imageView, final String filePath) {
        return getBitmapLocal(imageView, filePath, 0, 0);
    }

    /**
     * 通过线程加载 本地图片
     *
     * @param imageView
     * @param filePath
     * @param Width
     * @param high
     * @return
     */
    public Bitmap getBitmapLocal(final ImageView imageView,
                                 final String filePath, final int Width, final int high) {//ImageCacheUtils

        final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        Log.d(TAG, "开始读取缓存" + fileName);
        Bitmap bitmap = getCacheImage(fileName);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return bitmap;
        }
        Log.d(TAG, "缓存中未能找到图片");
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                Log.d(TAG, "显示图片");
                imageView.setImageBitmap(bitmap);
            }

        };
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Bitmap bitmap;
                Log.d(TAG, "加载本地图片");
                if (Width > 0 && high > 0)
                    bitmap = BitmapUitls.getSmallBitmap(filePath, Width, high);
                else
                    bitmap = BitmapUitls.getSmallBitmap(filePath, context);
                try {
                    setSDCache(fileName, bitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message msg = Message.obtain();
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        });
        return null;
    }

    /**
     * 通过线程加载 本地图片
     *
     * @param imageView
     * @param filePath
     * @param Width
     * @param high
     * @return
     */
    public void getBitmapLocalNotCache(final ImageView imageView,
                                       final String filePath, final int Width, final int high) {//ImageCacheUtils
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                Bitmap bitmap = (Bitmap) msg.obj;
                Log.d(TAG, "显示图片");
                imageView.setImageBitmap(bitmap);
            }

        };
        getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Bitmap bitmap;
                Log.d(TAG, "加载本地图片");
                if (Width > 0 && high > 0)
                    bitmap = BitmapUitls.getSmallBitmap(filePath, Width, high);
                else
                    bitmap = BitmapUitls.getSmallBitmap(filePath, context);
                Message msg = Message.obtain();
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 异步加载网络图片
     *
     * @param imageView
     * @param url           图片网络地址
     * @param downloadImage 下载前显示图片 -1 不显示
     * @param errorImage    下载失败显示图片    -1不显示
     * @param IsRound       是否圆角
     * @param inSampleSize  压缩比例   1不压缩
     * @return
     */
    public Bitmap getWebImageView(final ImageView imageView, final String url,
                                  final int downloadImage, final int errorImage, final boolean IsRound, final int inSampleSize) {
        String type = url.substring(url.lastIndexOf("."));
        final String key = Encode.getEncode("MD5", url) + type;

        Bitmap bitmap = getCacheImage(key); // 缓存
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return bitmap;
        }
        if (downloadImage > 0) {
            imageView.setImageResource(downloadImage);
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                Bitmap bm = (Bitmap) msg.obj;
                if (bm != null) {
                    try {
                        if (IsRound) {// 是否要显示圆角图片
                            String fileName = url.substring(url.lastIndexOf("/") + 1);
                            setSDCache(fileName, bm);//原始图片
                            Bitmap r_b = BitmapUitls.toRoundBitmap(bm);// 重绘圆形图片
                            setSDCache(key, r_b);//圆角图片
                            imageView.setImageBitmap(r_b);
                        } else {
                            setSDCache(key, bm);
                            imageView.setImageBitmap(bm);// 下载成功显示图片
                        }
                    } catch (IOException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                } else {
                    if (errorImage > 0) {// 判断图片是否存在
                        imageView.setImageResource(errorImage);// 下载失败显示的图片
                    }
                }
                super.handleMessage(msg);
            }

        };
        /**
         * 下载器
         */
        getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                Message msg = Message.obtain();
                Bitmap bitmap = null;
                if (!errorUrl.containsKey(key))// 查询是否是下载失败的图片.如果是,则不再下载
                    bitmap = getBitmapFormUrl(url, inSampleSize);
                msg.obj = bitmap;
                handler.sendMessage(msg);
            }
        });
        return null;
    }
}
