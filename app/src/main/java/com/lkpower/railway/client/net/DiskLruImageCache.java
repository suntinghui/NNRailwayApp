package com.lkpower.railway.client.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.lkpower.railway.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jakewharton.disklrucache.DiskLruCache;

// http://blog.csdn.net/guolin_blog/article/details/28863651

/**
 * Implementation of DiskLruCache by Jake Wharton modified from http://stackoverflow.com/questions/10185898/using-disklrucache-in-android-4-0-does-not-provide-for-opencache-method
 */
public class DiskLruImageCache implements ImageLoader.ImageCache {

    private DiskLruCache mDiskCache;
    private CompressFormat mCompressFormat = CompressFormat.JPEG;
    private static int IO_BUFFER_SIZE = 8 * 1024 * 2; // TODO  × 2 ?
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;

    public DiskLruImageCache(Context context, String uniqueName, int diskCacheSize, CompressFormat compressFormat, int quality) {
        try {
            final File diskCacheDir = getDiskCacheDir(context, uniqueName);
            if (!diskCacheDir.exists()) {
                diskCacheDir.mkdirs();
            }
            // 第一个参数指定的是数据的缓存地址，第二个参数指定当前应用程序的版本号，第三个参数指定同一个key可以对应多少个缓存文件，基本都是传1，第四个参数指定最多可以缓存多少字节的数据。
            mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
            mCompressFormat = compressFormat;
            mCompressQuality = quality;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException, FileNotFoundException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // uniqueName是为了对不同类型的数据进行区分而设定的一个唯一值，比如说在网易新闻缓存路径下看到的bitmap、object等文件夹。
    private File getDiskCacheDir(Context context, String uniqueName) {

        // final String cachePath = context.getCacheDir().getPath();
        // return new File(cachePath + File.separator + uniqueName);

        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    @Override
    public void putBitmap(String key, Bitmap data) {

        // ADD by STH
        key = StringUtil.MD5Crypto(key).toLowerCase();

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(key);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data, editor)) {
                mDiskCache.flush();
                editor.commit();
                Log.d("cache_test_DISK_", "image put on disk cache " + key);
            } else {
                editor.abort();
                Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
            }
        } catch (IOException e) {
            Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    // 这里获取到的是一个DiskLruCache.Snapshot对象，这个对象我们该怎么利用呢？
    // 很简单，只需要调用它的getInputStream()方法就可以得到缓存文件的输入流了。
    // 同样地，getInputStream()方法也需要传一个index参数，这里传入0就好。
    // 有了文件的输入流之后，想要把缓存图片显示到界面上就轻而易举了。
    @Override
    public Bitmap getBitmap(String key) {
        // ADD by STH
        key = StringUtil.MD5Crypto(key).toLowerCase();


        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get(key);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn = new BufferedInputStream(in, IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        Log.d("cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);

        return bitmap;

    }

    public boolean containsKey(String key) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        Log.d("cache_test_DISK_", "disk cache CLEARED");
        try {
            mDiskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

}
