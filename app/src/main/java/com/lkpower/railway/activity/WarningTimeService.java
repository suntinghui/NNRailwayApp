package com.lkpower.railway.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sth on 28/11/2016.
 */

public class WarningTimeService extends Service {

    private TrainInfo trainInfo = null;
    private String yyyyMd = null;

    private Timer timer = null;
    private Handler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            trainInfo = (TrainInfo) intent.getSerializableExtra("TRAIN_INFO");
            yyyyMd = intent.getStringExtra("DATE");

            startTimer();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopTimers();

        Intent warningIntent = new Intent(this, WarningNotificationClickReceiver.class);
        warningIntent.putExtra("PLAY", false);
        this.sendBroadcast(warningIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        stopTimers();

        try {
            for (final StationModel station : trainInfo.getStationInfo()) {
                Date when = DateUtil.getDate(yyyyMd, station.getArrivalDay(), station.getAheadTime(), station.getArrivalTime());
                Log.e("------", when.toString());

                // 如果本站的时间小于当前的时间则说明已经过站了,则不再提醒。
                if (when.before(new Date()))
                    continue;

                Log.e("======", when.toString());

                timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                };

                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            startTimer();

                            Intent warningIntent = new Intent(WarningTimeService.this, WarningNotificationClickReceiver.class);
                            warningIntent.putExtra("PLAY", true);
                            WarningTimeService.this.sendBroadcast(warningIntent);

                            String content = station.getStationName() + "还有" + station.getAheadTime() + "分钟到站,请您及时完成相关任务。";
                            Intent intent = new Intent(WarningTimeService.this, StationListActivityEx.class);
                            intent.putExtra("EarlyWarning", true);
                            intent.putExtra("SerialNumber", trainInfo.getSerialNumber());
                            intent.putExtra("stationId", station.getID());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NotificationUtil.showNotification(WarningTimeService.this, "到站提醒", content, intent);

                            requestTellServer(station.getID());
                        }
                        super.handleMessage(msg);
                    }
                };

                timer.schedule(task, when);

                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopTimers() {
        try {
            if (timer != null)
                timer.cancel();

            timer = null;

            handler = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestTellServer(String stationId) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "AlarmLogInfo");
        tempMap.put("InstanceId", trainInfo.getInstanceId());
        tempMap.put("DeviceId", DeviceUtil.getDeviceId(this));
        tempMap.put("LogTime", DateUtil.getCurrentDateTime());
        tempMap.put("StationId", stationId);
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

                    } else {
                        Toast.makeText(ActivityManager.getInstance().peekActivity(), resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, null);
    }
}
