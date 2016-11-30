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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sth on 28/11/2016.
 */

public class WarningLocationService extends Service implements AMapLocationListener {

    private AMapLocationClient mlocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private AMapLocation amapLocation = null;

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        LatLng pt_start = new LatLng(lat1, lon1);
        LatLng pt_end = new LatLng(lat2, lon2);
        double dis = DistanceUtil.getDistance(pt_start, pt_end);
        return dis;
    }

    private void getLocation() {
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息 
                amapLocation.getLocationType();
                //获取当前定位结果来源，如网络定位结果，详见定位类型表 
                amapLocation.getLatitude();//获取纬度 
                amapLocation.getLongitude();//获取经度 
                amapLocation.getAccuracy(); // 获取精度信息 

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                sdf.format(date);
                Log.e("===", amapLocation.toString());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。 
                Log.e("AmapError", "Location error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


}
