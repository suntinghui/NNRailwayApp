package com.lkpower.railway.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.lkpower.railway.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static android.R.attr.path;

public class FileUtil {
    /**
     * 返回程序路径 以 / 结束
     *
     * @return
     */
    public static String getFilePath() {
        // 其它程序无法访问
        // String path = ApplicationEnvironment.getInstance().getApplication().getFilesDir().getPath()+"/download/";
        String path = Environment.getExternalStorageDirectory() + File.separator + MyApplication.getInstance().getPackageName() + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            // file.mkdir();
            // creating missing parent directories if necessary
            file.mkdirs();
        }

        return path;
    }

    public static String createFolder(String name) {
        String path = Environment.getExternalStorageDirectory() + File.separator + MyApplication.getInstance().getPackageName() + File.separator + name + File.separator;

        File file = new File(path);
        if (!file.exists()) {
            // file.mkdir();
            // creating missing parent directories if necessary
            file.mkdirs();
        }

        return path;
    }

    public static ArrayList<File> fileList(String pathName) {
        ArrayList<File> list = new ArrayList<File>();

        File file = new File(createFolder(pathName));
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                list.add(f);
            }
        }

        return list;
    }

    // 判断文件是否存在
    public static boolean fileExists(String pathName, String fileName) {
        File file = new File(getFilePath() + pathName + File.separator + fileName);
        return file.exists();
    }

    // 删除目录下的所有文件
    public static void deleteFiles(String pathName) {
        File fileDir = new File(getFilePath() + pathName + File.separator);
        for (File file : fileDir.listFiles()) {
            file.delete();
            Log.e("delete file", file.getPath());
        }

        Log.e("delete file", "已删除所有附件");
    }

    public static void deleteFile(String pathName, String fileName) {
        File file = new File(getFilePath() + pathName + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 保存照片
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Bitmap mBitmap, String filePath) {
        Log.e("===", filePath);

        File f = new File(filePath);
        try {
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return f.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
