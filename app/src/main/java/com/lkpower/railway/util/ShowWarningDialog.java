package com.lkpower.railway.util;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.MyApplication;
import com.lkpower.railway.activity.MessageListActivity;
import com.lkpower.railway.activity.StationListActivityEx;
import com.lkpower.railway.activity.WarningNotificationClickReceiver;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.TrainInfo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * Created by sth on 22/02/2017.
 */

public class ShowWarningDialog {

    private TrainInfo trainInfo;
    private String stationID;
    private boolean EarlyWarning;

    public void showWarningDialog(String content, TrainInfo trainInfo, String stationID, boolean EarlyWarning) {
        this.trainInfo = trainInfo;
        this.stationID = stationID;
        this.EarlyWarning = EarlyWarning;

        new SweetAlertDialog(MyApplication.getInstance().getCurrentActivity(), SweetAlertDialog.NORMAL_TYPE).setTitleText("到站提醒").setContentText(content).setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                dosomething();

                sDialog.cancel();

            }
        }).show();
    }

    private void dosomething(){
        // 停止播放及震动
        Intent warningIntent = new Intent(MyApplication.getInstance().getCurrentActivity(), WarningNotificationClickReceiver.class);
        warningIntent.putExtra("PLAY", false);
        MyApplication.getInstance().getCurrentActivity().sendBroadcast(warningIntent);

        if (EarlyWarning) { // 预警
            // 用户点击了预警推送,告知服务器
            requestAlarmUpdateLogInfo(this.stationID, this.trainInfo);
        }
    }

    private void requestAlarmUpdateLogInfo(String stationId, TrainInfo trainInfo) {
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "AlarmUpdateLogInfo")
                .params("InstanceId", trainInfo.getInstanceId())
                .params("DeviceId", DeviceUtil.getDeviceId(MyApplication.getInstance().getCurrentActivity()))
                .params("LogTime", DateUtil.getCurrentDateTime())
                .params("StationId", stationId)
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

                        Toast.makeText(MyApplication.getInstance().getCurrentActivity(), ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                    }

                    @Override
                    public void onSuccess(String jsonObject, Call call, okhttp3.Response response) {
                        try {
                            Gson gson = new GsonBuilder().create();
                            ResultMsgDto resultDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                            if (resultDto.getResult().getFlag() == 1) {
                                Log.e("===", "用户点击了预警通知,并成功告知服务器");

                            } else {
                                Log.e("", "点击预警失败:" + resultDto.getResult().getFlagInfo());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
