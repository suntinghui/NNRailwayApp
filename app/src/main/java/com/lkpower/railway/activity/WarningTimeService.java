package com.lkpower.railway.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.NotificationUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sth on 28/11/2016.
 */

public class WarningTimeService extends Service {

    private ArrayList<StationModel> stationList = null;
    private String yyyyMd = null;
    private String serialNumber = null;

    private Timer timer = null;
    private Handler handler = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            stationList = (ArrayList<StationModel>) intent.getSerializableExtra("STATION_LIST");
            serialNumber = intent.getStringExtra("SERIALNUMBER");
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
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        stopTimers();

        try {
            for (final StationModel station : stationList) {
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
                        // 需要做的事:发送消息
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
                            intent.putExtra("SerialNumber", serialNumber);
                            intent.putExtra("stationId", station.getID());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NotificationUtil.showNotification(WarningTimeService.this, "到站提醒", content, intent);
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
}
