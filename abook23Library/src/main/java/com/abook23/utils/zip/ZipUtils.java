package com.abook23.utils.zip;

import com.abook23.utils.util.FileUtils;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by abook23 on 2015/8/12.
 */
public class ZipUtils {
    private FileUtils fileUtils;
    private List<File> files = new ArrayList<>();

    public ZipUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    /**
     * org.apache.tools.zip.ZipFile 解压
     *
     * @param file_zip 文件路径
     * @param savePath 保存路径
     * @return
     */
    public List<File> ReaderZipFile(File file_zip, String savePath) {
        if (file_zip == null)
            return null;
        try {
            String file_zip_name = file_zip.getName();
            String dir_parent = savePath + File.separator + file_zip_name.replace(".zip", "");
            ZipFile zips = new ZipFile(file_zip, "gbk");
            Enumeration en = zips.getEntries();
            ZipEntry entry;
            while (en.hasMoreElements()) {
                entry = (ZipEntry) en.nextElement();
                if (entry.isDirectory()) {
                    File file = fileUtils.createDir(dir_parent + File.separator + entry.getName());
                    fileUtils.scannerFile(file.getPath());
                } else {
                    String fileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                    String path = dir_parent + File.separator + entry.getName().replace(fileName, "");
                    File file = fileUtils.createSDFile(path, fileName);
                    BufferedInputStream is = new BufferedInputStream(zips.getInputStream(entry));
                    FileOutputStream os = new FileOutputStream(file);
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = is.read(b)) != -1) {
                        os.write(b, 0, len);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    fileUtils.scannerFile(file.getPath());
                    files.add(file);
                }
            }
            zips.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
}
