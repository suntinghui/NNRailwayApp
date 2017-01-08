package com.lkpower.railway.activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.Response;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.client.ActivityManager;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.NotificationUtil;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by sth on 28/11/2016.
 */

public class WarningLocationService extends Service {

    private AMapLocationClient mlocationClient = null;

    private TrainInfo trainInfo = null;

    private final static int Time_Interval = 10000;
    private final static int MAX_Count = (10 * 60 * 1000) / Time_Interval;
    private int current_count = 0;


    private final int MAX_SEND = 10;
    private int currentSentCount = 0;

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
                    HashMap<String, String> distanceMap = getStationDis(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                    sendUpdateDistanceBroadcast(distanceMap);

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
        try {
            trainInfo = (TrainInfo) intent.getSerializableExtra("TRAIN_INFO");

            startLocation();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Location", "启动WarningLocationService时出现错误。");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private HashMap<String, String> getStationDis(double lat1, double lon1) {
        HashMap<String, String> disMap = new HashMap<String, String>();
        for (StationModel station : trainInfo.getStationInfo()) {
            double dis = getDistance(lat1, lon1, Double.parseDouble(station.getLatitude()), Double.parseDouble(station.getLongitude()));
            DecimalFormat df = new DecimalFormat("#.0");
            disMap.put(station.getID(), df.format(dis / 1000.0f));
            Log.e("DIS", station.getStationName() + " -- " + df.format(dis / 1000.0f));
        }

        return disMap;
    }

    private Double getDistance(double lat1, double lon1, double lat2, double lon2) {
        LatLng pt_start = new LatLng(lat1, lon1);
        LatLng pt_end = new LatLng(lat2, lon2);
        Double dis = DistanceUtil.getDistance(pt_start, pt_end);
        return dis;
    }

    private void sendUpdateDistanceBroadcast(HashMap<String, String> distanceMap) {
        Intent intent = new Intent(StationListActivityEx.ACTION_UPDATE_DISTANCE);
        intent.putExtra("DISTANCE", true);
        intent.putExtra("DISTANCE_MAP", distanceMap);
        sendBroadcast(intent);

        // 每隔一定的时间进行一次通知提醒
        if (++current_count > MAX_Count) {
            sendNotifaction();
            current_count = 0;
        }
    }

    private void sendNotifaction() {
        Intent warningIntent = new Intent(WarningLocationService.this, WarningNotificationClickReceiver.class);
        warningIntent.putExtra("PLAY", true);
        WarningLocationService.this.sendBroadcast(warningIntent);

        Intent intent = new Intent(WarningLocationService.this, StationListActivityEx.class);
        intent.putExtra("EarlyWarning", true);
        intent.putExtra("TRAIN_INFO", trainInfo);
        intent.putExtra("stationId", "0");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationUtil.showNotification(WarningLocationService.this, "预警提醒", "请及时关注地理位置信息。距离数据为当前位置距各车站的直线距离,数据仅供参考。", intent);

        requestTellServer();
    }

    private void requestTellServer() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "AlarmLogInfo");
        tempMap.put("InstanceId", trainInfo.getInstanceId());
        tempMap.put("DeviceId", DeviceUtil.getDeviceId(this));
        tempMap.put("LogTime", DateUtil.getCurrentDateTime());
        tempMap.put("StationId", "000000");
        tempMap.put("Remark", "");
        tempMap.put("Args", "");

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                    if (resultMsgDto.getResult().getFlag() == 1) {
                        Log.e("===", "预警信息已经发送到服务器");
                        currentSentCount = 0;

                    } else {
                        // Toast.makeText(ActivityManager.getInstance().peekActivity(), resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();

                        if (++currentSentCount < MAX_SEND) {
                            Log.e("===", "预警信息发送到服务器失败,重发:" + currentSentCount);

                            requestTellServer();

                        } else {
                            currentSentCount = 0;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, null);
    }

}
