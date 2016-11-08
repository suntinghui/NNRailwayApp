package com.lkpower.railway;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.HandlerThread;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.android.volley.toolbox.ImageLoader;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

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

        initCloudChannel(this);

        HandlerThread workerThread = new HandlerThread("global_worker_thread");
        workerThread.start();
        initImageLoader(this);
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

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");

                String deviceId = pushService.getDeviceId();
                Log.e("DeviceId", deviceId);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });

        // 初始化小米通道，自动判断是否支持小米系统推送，如不支持会跳过注册
//        MiPushRegister.register(applicationContext, "小米AppID", "小米AppKey");
        // 初始化华为通道，自动判断是否支持华为系统推送，如不支持会跳过注册
//        HuaWeiRegister.register(applicationContext);
    }


    public static void initImageLoader(Context context){
        if(!com.nostra13.universalimageloader.core.ImageLoader.getInstance().isInited()){
            ImageLoaderConfiguration config = null;
            if(BuildConfig.DEBUG){
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
            }else{
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
