package com.gxx.translatelangproject.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileSDCardUtil {
    public static FileSDCardUtil fileSDCardUtil;

    public static FileSDCardUtil getInstance() {
        if (fileSDCardUtil == null) {
            synchronized (FileSDCardUtil.class) {
                if (fileSDCardUtil == null) {
                    fileSDCardUtil = new FileSDCardUtil();
                }
            }
        }
        return fileSDCardUtil;
    }

    /**
     * @date: 2019/8/2 0002
     * @author: gaoxiaoxiong
     * @description:获取沙盒存储目录下的 fileName的文件夹路径
     **/
    public String getSandboxPublickDiskFileDir(Context context, String fileName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {//此目录下的是外部存储下的私有的fileName目录
            cachePath = context.getExternalFilesDir(fileName).getAbsolutePath();  //mnt/sdcard/Android/data/com.my.app/files/fileName
        } else {
            cachePath = context.getFilesDir().getPath() + "/" + fileName;        //data/data/com.my.app/files
        }
        File file = new File(cachePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();  //mnt/sdcard/Android/data/com.my.app/files/fileName
    }


}
