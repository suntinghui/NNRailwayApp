package com.lkpower.railway;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.lkpower.railway.client.ActivityManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import com.umeng.message.lib.BuildConfig;

/**
 * Created by sth on 3/22/16.
 */
public class MyApplication extends Application {

    private static final String TAG = "Init";

    private static MyApplication instance = null;

    private Activity mCurrentActivity = null;

    public void onCreate() {
        super.onCreate();

        instance = this;

        SDKInitializer.initialize(getApplicationContext());

        HandlerThread workerThread = new HandlerThread("global_worker_thread");
        workerThread.start();
        initImageLoader(this);

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("UMENG DEVICETOKEN", "===:" + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });

        // for activity manager
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                ActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public static synchronized MyApplication getInstance() {
        return instance;
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    /**
     * 返回配置文件的日志开关
     *
     * @return
     */
    public boolean getLoggingSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("LOGGING");
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean getAlphaSwitch() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            boolean b = appInfo.metaData.getBoolean("ALPHA");
            return b;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static void initImageLoader(Context context) {
        if (!com.nostra13.universalimageloader.core.ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = null;
            if (BuildConfig.DEBUG) {
                config = new ImageLoaderConfiguration.Builder(context)
                        /*.threadPriority(Thread.NORM_PRIORITY - 2)
                        .memoryCacheSize((int) (Runtime.getRuntime().maxMemory() / 4))
						.diskCacheSize(500 * 1024 * 1024)
						.writeDebugLogs()
						.diskCacheFileNameGenerator(new Md5FileNameGenerator())
						.tasksProcessingOrder(QueueProcessingType.LIFO).build();*/

                        //.memoryCacheExtraOptions(200, 200)
                        //.diskCacheExtraOptions(200, 200, null).threadPoolSize(3)
                        .threadPriority(Thread.NORM_PRIORITY - 1)
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        //.denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024))
						/*.memoryCacheSize(20 * 1024 * 1024)*/
                        .memoryCacheSizePercentage(13)
                        .diskCacheSize(500 * 1024 * 1024)
                        //.imageDownloader(new BaseImageDownloader(A3App.getInstance().getApplicationContext()))
                        //.imageDecoder(new BaseImageDecoder(true))
                        //.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        //.writeDebugLogs()
                        .build();
            } else {
                config = new ImageLoaderConfiguration.Builder(context)
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .diskCacheSize(500 * 1024 * 1024)
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                        .tasksProcessingOrder(QueueProcessingType.LIFO).build();
            }
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
        }

    }


}
