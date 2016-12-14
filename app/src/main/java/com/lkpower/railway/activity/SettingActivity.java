package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.ActivityManager;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.DeviceUtil;

import java.util.HashMap;

import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static anetwork.channel.http.NetworkSdkSetting.context;

/**
 * Created by sth on 19/10/2016.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout runInfoLayout = null; // 行车信息
    private LinearLayout messageLayout = null; // 段发信息
    private LinearLayout checkUpdateLayout = null; // 检查新版本
    private TextView currentVersionTextView = null;
    private LinearLayout feedbackLayout = null;
    private LinearLayout contactLayout = null;
    private LinearLayout aboutLayout = null;
    private Button logoutBtn = null;

    private Button toggleGestureLockBtn = null;
    private TextView tipModelTextView = null;
    private boolean toggleFlag = false;

    private TrainInfo trainInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        // 检查更新
        UpdateFunGO.init(this);

        this.trainInfo = (TrainInfo) this.getIntent().getSerializableExtra("TRAIN_INFO");

        toggleFlag = Constants.CURRENT_TRAIN_LATE;

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateFunGO.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        UpdateFunGO.onStop(this);
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("功能");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        runInfoLayout = (LinearLayout) this.findViewById(R.id.runInfoLayout);
        runInfoLayout.setOnClickListener(this);

        messageLayout = (LinearLayout) this.findViewById(R.id.messageLayout);
        messageLayout.setOnClickListener(this);

        checkUpdateLayout = (LinearLayout) this.findViewById(R.id.checkUpdateLayout);
        checkUpdateLayout.setOnClickListener(this);

        currentVersionTextView = (TextView) this.findViewById(R.id.currentVersionTextView);
        currentVersionTextView.setText("当前版本号:V" + this.getPackageInfo(this).versionName);

        feedbackLayout = (LinearLayout) this.findViewById(R.id.feedbackLayout);
        feedbackLayout.setOnClickListener(this);

        contactLayout = (LinearLayout) this.findViewById(R.id.contactLayout);
        contactLayout.setOnClickListener(this);

        aboutLayout = (LinearLayout) this.findViewById(R.id.aboutLayout);
        aboutLayout.setOnClickListener(this);

        logoutBtn = (Button) this.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);

        toggleGestureLockBtn = (Button) this.findViewById(R.id.toggleGestureLockBtn);
        toggleGestureLockBtn.setOnClickListener(this);

        tipModelTextView = (TextView) this.findViewById(R.id.tipModelTextView);

        toggleGestureLockBtn.setBackgroundResource(toggleFlag ? R.drawable.btn_toggle_on : R.drawable.btn_toggle_off);
        tipModelTextView.setText(toggleFlag ? "位置预警" : "时间预警");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.runInfoLayout: {// 行车信息
                Intent intent = new Intent(this, DrivingInfoActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.messageLayout: {// 段发信息
                Intent intent = new Intent(this, MessageListActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.checkUpdateLayout: {
                checkUpgrade();
            }
            break;

            case R.id.feedbackLayout: {
                Intent intent = new Intent(this, FeedBackActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.contactLayout: {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constants.DeviceInfo.getPhone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;

            case R.id.aboutLayout: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "南宁铁路局");
                intent.putExtra("url", "http://www.nntlj.com/staticpages/20140721/nt53cc744b-2703110.shtml");
                this.startActivity(intent);
            }
            break;

            case R.id.logoutBtn:
                break;

            case R.id.toggleGestureLockBtn: {
                new SweetAlertDialog(SettingActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText("确定修改?").setContentText(toggleFlag ? "确认后将会使本车次所有设备修改为正点运行" : "确认后将会使本车次所有设备修改为晚点运行").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        requestLaterNotice();
                    }
                }).setCancelText("取消").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                }).show();
            }

            break;
        }
    }

    // 手动
    private void checkUpgrade() {
        UpdateKey.API_TOKEN = Constants.FIR_API_TOKEN;
        UpdateKey.APP_ID = Constants.FIR_APP_ID;
        //下载方式:
        UpdateKey.DialogOrNotification = UpdateKey.WITH_DIALOG; //通过Dialog来进行下载
        //UpdateKey.DialogOrNotification=UpdateKey.WITH_NOTIFITION; //通过通知栏来进行下载(默认)
//        UpdateFunGO.init(this);

        UpdateFunGO.manualStart(this);
    }

    private void toggleLaterNotice() {
        if (toggleFlag) {
            // 如果设置了手势密码，则去关闭手势密码
            toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
            tipModelTextView.setText("时间预警");
//            Toast.makeText(this, "列车晚点状态取消,切换到时间预警模式", Toast.LENGTH_SHORT).show();

        } else {
            // 如果没有设置，则切换图片，并去设置
            toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_on);
            tipModelTextView.setText("位置预警");
//            Toast.makeText(this, "列车被标记为晚点,切换到GPS地理预警模式", Toast.LENGTH_SHORT).show();
        }

        toggleFlag = !toggleFlag;

        Constants.CURRENT_TRAIN_LATE = toggleFlag;
    }

    private void requestLaterNotice() {
        // 如果用户没有刷新到车站列表就点击了功能,是有可能为空的
        if (null == this.trainInfo) {
            Toast.makeText(this, "未检测到当前车次信息,请返回重试", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "SetLaterNotice");
        tempMap.put("InstanceId", trainInfo.getInstanceId());
        tempMap.put("DeviceId", DeviceUtil.getDeviceId(this));
        tempMap.put("state", toggleFlag ? "0" : "1"); // 1是晚点,其他为正点

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                    if (resultMsgDto.getResult().getFlag() == 1) {
                        toggleLaterNotice();

                    } else {
                        Toast.makeText(SettingActivity.this, resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "正在上传数据...");
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
