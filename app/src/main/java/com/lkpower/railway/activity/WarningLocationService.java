package com.lkpower.railway.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.lkpower.railway.dto.StationModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sth on 28/11/2016.
 */

public class WarningLocationService extends Service {

    private AMapLocationClient mlocationClient = null;

    private ArrayList<StationModel> stationList = null;

    private void startLocation() {
        initLocation();
        // 启动定位
        mlocationClient.startLocation();
    }

    private void stopLocation() {
        // 停止定位
        mlocationClient.stopLocation();
    }

    private void destroyLocation() {
        if (null != mlocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mlocationClient.onDestroy();
            mlocationClient = null;
        }
    }

    private void initLocation() {
        //初始化client
        mlocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        mlocationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        mlocationClient.setLocationListener(mLocationListener);
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(10000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        return mOption;
    }

    AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息 
                    Log.e("LOCATION", aMapLocation.toString());
                    getStationDis(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。 
                    Log.e("AmapError", "Location error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopLocation();

        destroyLocation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        stationList = (ArrayList<StationModel>) intent.getSerializableExtra("STATION_LIST");

        startLocation();

        return super.onStartCommand(intent, flags, startId);
    }

    private HashMap<String, String> getStationDis(double lat1, double lon1) {
        HashMap<String, String> disMap = new HashMap<String, String>();
        for (StationModel station : stationList) {
            double dis = getDistance(lat1, lon1, Double.parseDouble(station.getLatitude()), Double.parseDouble(station.getLongitude()));
            DecimalFormat df = new DecimalFormat("#.0");
            disMap.put(station.getID(), df.format(dis/1000.0f));
            Log.e("DIS", station.getStationName() + " -- " + df.format(dis/1000.0f));
        }

        return disMap;
    }

    private Double getDistance(double lat1, double lon1, double lat2, double lon2) {
        LatLng pt_start = new LatLng(lat1, lon1);
        LatLng pt_end = new LatLng(lat2, lon2);
        Double dis = DistanceUtil.getDistance(pt_start, pt_end);
        return dis;
    }


}
