package com.lkpower.railway.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.NotificationUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * Created by sth on 28/11/2016.
 */

public class WarningTimeService extends Service {

    private TrainInfo trainInfo = null;
    private String yyyyMd = null;

    private Timer timer = null;
    private Handler handler = null;

    private StationModel station = null;

    private final int MAX_SEND = 10;
    private int currentSentCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
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

        try {
            stopTimers();

            Intent warningIntent = new Intent(this, WarningNotificationClickReceiver.class);
            warningIntent.putExtra("PLAY", false);
            this.sendBroadcast(warningIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        stopTimers();

        try {
            for (final StationModel ss : trainInfo.getStationInfo()) {
                this.station = ss;

                Date when = DateUtil.getDate(yyyyMd, this.station.getArrivalDay(), this.station.getAheadTime(), this.station.getArrivalTime());
                Log.e("------", when.toString());

                // 如果本站的时间小于当前的时间则说明已经过站了,则不再提醒。
                if (!when.after(new Date()))
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

                            Intent warningIntent = new Intent(WarningTimeService.this, WarningNotificationClickReceiver.class);
                            warningIntent.putExtra("PLAY", true);
                            WarningTimeService.this.sendBroadcast(warningIntent);

                            String content = station.getStationName() + "即将在" + station.getAheadTime() + "分钟后(" + station.getArrivalTime() + ")到站,请您及时完成相关任务。";
                            Intent intent = new Intent(WarningTimeService.this, StationListActivityEx.class);
                            intent.putExtra("EarlyWarning", true);
                            intent.putExtra("TRAIN_INFO", trainInfo);
                            intent.putExtra("stationId", station.getID());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NotificationUtil.showNotification(WarningTimeService.this, "到站提醒", content, intent);

                            // 启动下一轮监测
                            startTimer();

                            requestTellServer();
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

    private void requestTellServer() {
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "AlarmLogInfo")
                .params("InstanceId", trainInfo.getInstanceId())
                .params("DeviceId", DeviceUtil.getDeviceId(this))
                .params("LogTime", DateUtil.getCurrentDateTime())
                .params("StationId", station.getID())
                .params("Remark", "")
                .params("Args", "")
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);

                        e.printStackTrace();

                        Toast.makeText(WarningTimeService.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                    }

                    @Override
                    public void onSuccess(String jsonObject, Call call, okhttp3.Response response) {
                        try {
                            Gson gson = new GsonBuilder().create();
                            ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                            if (resultMsgDto.getResult().getFlag() == 1) {
                                Log.e("===", "预警信息已经发送到服务器");
                                currentSentCount = 0;

                            } else {
                                //Toast.makeText(ActivityManager.getInstance().peekActivity(), resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();

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
    }
}
