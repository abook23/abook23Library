package com.abook23.utils.http;

import android.content.Context;
import android.util.Log;

import com.abook23.listener.DownloadListener;
import com.abook23.utils.util.FileUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by abook23 on 2015/12/7.
 */
public class DownLoadUtils {

    public boolean pause;
    public boolean cancel;

    private final String TAG = "DownLoadUtils";
    private int POOL_SIZE = 1;// 获取当前系统的CPU 数目
    private int cpuNum = Runtime.getRuntime().availableProcessors();
    private int nThreads = POOL_SIZE * cpuNum;//ExecutorService通常根据系统资源情况灵活定义线程池大小
    private ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
    private DownloadListener downloadListener;
    private FileUtils fileUtils;

    public DownLoadUtils(Context context) {
        fileUtils = new FileUtils(context);
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
     * 取消 队列中的任务
     * 真正现在的线程 无法终止
     */
    public synchronized void cancelTask() {
        Log.d(TAG, "取消下载");
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
            executorService = Executors.newFixedThreadPool(nThreads);
            fileUtils.isWrite = false;
        }
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public void downFile(String url, String path, String fileName) {
        getThreadPool().execute(new DownloadRunnable(url, path, fileName));
    }

    public void downFile(String url, String path, String fileName, DownloadListener downloadListener) {
        getThreadPool().execute(new DownloadRunnable(url, path, fileName));
        this.downloadListener = downloadListener;
    }

    public class DownloadRunnable implements Runnable {
        private String url, dirsName, fileName;
        private float length;

        public DownloadRunnable(String url, String dirsName, String fileName) {
            this.url = url;
            this.dirsName = dirsName;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            downFile(url, dirsName, fileName);
        }

        /**
         * 原始的 http 链接  很难以终止
         * 推荐用 httpClient
         *
         * @param urlStr
         * @param dirsName
         * @param fileName
         */
        public void downFile1(String urlStr, String dirsName, String fileName) {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setDoInput(true);
                urlCon.setUseCaches(false);
                urlCon.setRequestMethod("GET");
                urlCon.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=utf-8");
                urlCon.connect();// 建立连接
                length = urlCon.getContentLength();
                if (urlCon.getResponseCode() == 200) {
                    downloadListener.onStart(length);//开始下载
                    if (fileName == null || "".equals(fileName)) {
                        fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
                    }
                    if (fileUtils.isFileExist(dirsName, fileName)) {//文件以及存在!
                        if (downloadListener != null) {
                            downloadListener.onSuccess(fileUtils.getFile(dirsName, fileName));
                        }
                    }
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        is = urlCon.getInputStream();
                        File file = fileUtils.createSDFile(dirsName, fileName);
                        os = new FileOutputStream(file);
                        byte buffer[] = new byte[1024];
                        int ln;
                        int readSize = 0;
                        label:
                        while (!isCancel()) {
                            if (isCancel()) {
                                setCancel(true);
                                if (downloadListener != null) {
                                    downloadListener.onCancel();
                                }
                            }
                            if (isPause()) {
                                if (downloadListener != null) {
                                    downloadListener.onPause();
                                    try {
                                        Thread.sleep(1000);//再停休眠500ms
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            while (!isPause() && (ln = is.read(buffer)) != -1) {
                                os.write(buffer, 0, ln);
                                readSize += ln;
                                if (downloadListener != null) {
                                    downloadListener.onSize(readSize, length);
                                    if (readSize == length) {//下载完成
                                        downloadListener.onSuccess(file);
                                    }
                                }
                                if (isCancel()) {//取消
                                    file.delete();
                                    if (downloadListener != null) {
                                        downloadListener.onCancel();
                                    }
                                    break label;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); // To change body of catch statement use File |
                    } finally {
                        if (os != null) {
                            os.flush();
                            os.close();
                        }
                        if (!isCancel()) {
                            if (is != null) {
                                //下载到一半，取消下载难以断开，对手机用户来说，浪费流量
                                is.close();
                                urlCon.disconnect();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void downFile(String urlStr, String dirsName, String fileName) {
            try {
                HttpGet httpGet = new HttpGet(urlStr);
                HttpClient httpClient = HttpUtils.getInstance().getDefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    if (fileName == null || "".equals(fileName)) {
                        fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
                    }
                    if (fileUtils.isFileExist(dirsName, fileName)) {//文件以及存在!
                        if (downloadListener != null) {
                            downloadListener.onSuccess(fileUtils.getFile(dirsName, fileName));
                        }
                    }
                    HttpEntity entity = httpResponse.getEntity();
                    length = entity.getContentLength();
                    if (downloadListener != null)
                        downloadListener.onStart(length);//开始下载
                    InputStream is = entity.getContent();
                    OutputStream os = null;
                    try {
                        File file = fileUtils.createSDFile(dirsName, fileName);
                        fileUtils.scannerFile(file.getPath());
                        os = new FileOutputStream(file);
                        byte buffer[] = new byte[1024];
                        int ln = -1;
                        int readSize = 0;
                        label:
                        while (!isCancel()) {
                            if (isCancel()) {
                                setCancel(true);
                                if (downloadListener != null) {
                                    downloadListener.onCancel();
                                }
                            }
                            if (isPause()) {
                                if (downloadListener != null) {
                                    downloadListener.onPause();
                                    try {
                                        Thread.sleep(1000);//再停休眠500ms
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            while (!isPause() && (ln = is.read(buffer)) != -1) {
                                os.write(buffer, 0, ln);
                                readSize += ln;
                                if (downloadListener != null) {
                                    downloadListener.onSize(readSize, length);
                                    if (readSize == length) {//下载完成
                                        downloadListener.onSuccess(file);
                                        break label;
                                    }
                                }
                                if (isCancel()) {//取消
                                    file.delete();
                                    if (downloadListener != null) {
                                        downloadListener.onCancel();
                                    }
                                    break label;
                                }
                            }
                            if (ln == -1) {
                                downloadListener.onSuccess(file);
                                break label;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); // To change body of catch statement use File |
                    } finally {
                        if (!httpGet.isAborted()) {
                            httpGet.abort();//终止链接，很重要 不然 is.close 很难断开，浪费流量
                        }
                        if (os != null) {
                            os.flush();
                            os.close();
                        }
                        if (is != null) {//建议先终止 http 链接在 断开
                            is.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
