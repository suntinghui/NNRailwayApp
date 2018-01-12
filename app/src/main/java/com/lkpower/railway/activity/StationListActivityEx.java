package com.lkpower.railway.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.DeviceDetailInfoDto;
import com.lkpower.railway.dto.DeviceInfo;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lkpower.railway.util.UMengPushUtil;
import com.lkpower.railway.util.ViewUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import cn.hugeterry.updatefun.UpdateFunGO;
import cn.hugeterry.updatefun.config.UpdateKey;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

import static com.lkpower.railway.R.id.runningBtn;


/**
 * Created by sth on 17/10/2016.
 * <p>
 * 车站列表
 */

public class StationListActivityEx extends BaseActivity implements View.OnClickListener {

    public static String ACTION_UPDATE_DISTANCE = "action_update_distance";

    private NiceSpinner titleSpinner = null;

    private TextView warningStatusTextView = null;

    private ListView listView = null;
    private StationListAdapter adapter = null;

    private DeviceInfo deviceInfo = null;

    public ArrayList<TrainInfo> trainInfoList = new ArrayList<TrainInfo>();

    // 标题 车站列表名称
    private ArrayList<String> trainList = new ArrayList<String>();

    public int location = 0;

    public String yyyyMd = DateUtil.getCurrentDate3();

    private HashMap<String, String> distanceMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_list);

        checkUpdate();

        initView();

        ActivityUtil.verifyReadPhoneStatePermissions(this);

        new UMengPushUtil().new AddAliasTask(this).execute();

        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable(new IUmengCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(String s, String s1) {
            }
        });
        mPushAgent.setDebugMode(false);
        mPushAgent.setPushIntentServiceClass(MyUMengPushService.class);

        registerBroadcastReceiver();

        yaoyaoAction(this.getIntent());
    }

    private void initView() {
        titleSpinner = (NiceSpinner) this.findViewById(R.id.titleSpinner);
        List<String> initSet = new LinkedList<>(Arrays.asList("车站列表"));
        titleSpinner.attachDataSource(initSet);

        TextView settingTextView = (TextView) this.findViewById(R.id.settingTextView);
        settingTextView.setOnClickListener(this);

        warningStatusTextView = (TextView) this.findViewById(R.id.warningStatusTextView);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new StationListAdapter(this);
        listView.setAdapter(adapter);
        listView.setAlpha(0.5f);

        // 为空的时候用户可以手动查询车站列表数据
        ActivityUtil.setEmptyView(this, listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStationListEx("正在查询车站信息...");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        UpdateFunGO.onResume(this);

        // 每一次重新进入该界面都刷新一下，主要是为了刷新任务完成情况。
        requestStationListEx(trainInfoList.isEmpty() ? "正在查询车站信息..." : null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.stopWarningTimeService();
        this.stopWarningLocationService();
    }

    @Override
    protected void onStop() {
        super.onStop();

        UpdateFunGO.onStop(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.e("onNewIntent", "=======================onNewIntent");

        yaoyaoAction(intent);

    }

    private void yaoyaoAction(Intent intent) {
        try {
            String pushType = intent.getStringExtra("PushType");
            boolean LATE_TYPE = intent.getBooleanExtra("LATE_TYPE", false);
            boolean late = intent.getBooleanExtra("LATE", false);

            if ("LateType_Late".equalsIgnoreCase(pushType)) {
                Toast.makeText(this, "列车被标记为晚点,切换到GPS地理预警模式", Toast.LENGTH_SHORT).show();
                Constants.CURRENT_TRAIN_LATE = late;
                this.startWarningLocationService();
                this.stopWarningTimeService();

                // 用户点击了预警推送,告知服务器
                requestAlarmUpdateLogInfo(intent.getStringExtra("stationId"), trainInfoList.get(location).getInstanceId());

            } else if ("LateType_Normal".equalsIgnoreCase(pushType)) {
                Toast.makeText(this, "列车晚点状态取消,切换到时间预警模式", Toast.LENGTH_SHORT).show();
                Constants.CURRENT_TRAIN_LATE = late;
                this.startWaringTimeService();
                this.stopWarningLocationService();

                // 用户点击了预警推送,告知服务器
                requestAlarmUpdateLogInfo(intent.getStringExtra("stationId"), trainInfoList.get(location).getInstanceId());

            } else if ("OldWarning".equalsIgnoreCase(pushType)){
                // 停止播放及震动
                Intent warningIntent = new Intent(this, WarningNotificationClickReceiver.class);
                warningIntent.putExtra("PLAY", false);
                this.sendBroadcast(warningIntent);

                boolean EarlyWarning = intent.getBooleanExtra("EarlyWarning", false);
                if (EarlyWarning) { // 预警
                    // 用户点击了预警推送,告知服务器
                    requestAlarmUpdateLogInfo(intent.getStringExtra("stationId"), trainInfoList.get(location).getInstanceId());
                }

            } else if ("MissionWarning".equalsIgnoreCase(pushType)) {
                requestTellServer(intent.getStringExtra("InstanceId"), intent.getStringExtra("StationId"));

                showPushDialog(intent);

            } else if ("Test".equalsIgnoreCase(pushType)) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("提示").setContentText("收到测试消息").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 新的方案
    private void showPushDialog(final Intent intent) {
        Intent warningIntent = new Intent(this, WarningNotificationClickReceiver.class);
        warningIntent.putExtra("PLAY", true);
        this.sendBroadcast(warningIntent);

        String content = intent.getStringExtra("StationName") + "将在 " + intent.getStringExtra("ArriveDate") + " 到站,请您及时完成相关任务。";
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("提示").setContentText(content).setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();

                // 停止播放及震动
                Intent warningIntent = new Intent(StationListActivityEx.this, WarningNotificationClickReceiver.class);
                warningIntent.putExtra("PLAY", false);
                StationListActivityEx.this.sendBroadcast(warningIntent);

                // 用户点击了预警推送,告知服务器
                requestAlarmUpdateLogInfo(intent.getStringExtra("StationId"), intent.getStringExtra("InstanceId"));

            }
        }).show();
    }

    private void requestTellServer(String InstanceId, String StationId) {
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "AlarmLogInfo")
                .params("InstanceId", InstanceId)
                .params("DeviceId", DeviceUtil.getDeviceId(this))
                .params("LogTime", DateUtil.getCurrentDateTime())
                .params("StationId", StationId)
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

                        Toast.makeText(StationListActivityEx.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
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

                            } else {
                                Log.e("===", "预警信息发送到服务器失败");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 请求车站列表数据
    private void requestStationListEx(final String msg) {
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "DeviceDetailInfo")
                .params("DeviceId", DeviceUtil.getDeviceId(this))
                .params("Version", ActivityUtil.getPackageInfo(this).versionName)
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        HUDUtil.showHUD(StationListActivityEx.this, msg);
                    }


                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);

                        e.printStackTrace();

                        Toast.makeText(StationListActivityEx.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);

                        HUDUtil.dismiss();
                    }

                    @Override
                    public void onSuccess(String jsonObject, Call call, okhttp3.Response response) {
                        try {
                            Log.e("===", "okgo response:" + jsonObject);

                            Gson gson = new GsonBuilder().create();
                            DeviceDetailInfoDto deviceDetailInfoDto = gson.fromJson(jsonObject, DeviceDetailInfoDto.class);
                            if (deviceDetailInfoDto.getResult().getFlag() == 1) {
                                trainList.clear();

                                deviceInfo = deviceDetailInfoDto.getDataInfo().getDeviceInfo();
                                trainInfoList = (ArrayList<TrainInfo>) deviceDetailInfoDto.getDataInfo().getTrainInfo();

                                Constants.DeviceInfo = deviceInfo;

                                if (trainInfoList != null && trainInfoList.size() != 0) {

                                    TrainInfo trainInfo = trainInfoList.get(location);


                                    adapter.setData(trainInfo.getStationInfo());
                                    adapter.notifyDataSetChanged();

                                    yyyyMd = trainInfo.getSerialNumber();

                                    if ("".equals(yyyyMd.trim())){
                                        listView.setAlpha(0.5f);
                                    } else {
                                        listView.setAlpha(1.0f);
                                    }

                                    if (Constants.CURRENT_TRAIN_LATE) {
                                        startWarningLocationService();
                                        stopWarningTimeService();

                                    } else {
                                        startWaringTimeService();
                                        stopWarningLocationService();
                                    }


                                    Constants.CarNumberId = trainInfo.getID();
                                    Constants.CarNumberName = trainInfo.getTrainName();
                                    Constants.CardUserName = trainInfo.getUserName();

                                    titleSpinner.setText(Constants.CarNumberName + "/" + Constants.CardUserName);

                                    for (TrainInfo info : trainInfoList) {
                                        trainList.add(info.getTrainName() + "/" + info.getUserName());
                                    }

                                    titleSpinner.attachDataSource(trainList);
                                    titleSpinner.setSelectedIndex(location);
                                    titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                            changeTrain(i);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                } else {
                                    Toast.makeText(StationListActivityEx.this, "未查询到数据", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                Toast.makeText(StationListActivityEx.this, deviceDetailInfoDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    private void startWaringTimeService() {
        /*

        if ("".equals(yyyyMd.trim())) {
            Log.e("Time Service", "车次时间为空，未启动车次");
            return;
        }

        if (ActivityUtil.isTimeServiceWorked(StationListActivityEx.this)) {
            Log.e("Time Service", "时间监控服务正在运行中，没有必要再次启动");
            return;

        } else {
            Log.e("Location Service", "即将启动时间监控服务");
        }

        try {
            TrainInfo tempTrain = trainInfoList.get(location);
            if (null == tempTrain) {
                requestStationListEx(null);
            }
        } catch (Exception e) {
            e.printStackTrace();

            requestStationListEx(null);
        }


        Intent intent = new Intent(this, WarningTimeService.class);
        intent.putExtra("TRAIN_INFO", trainInfoList.get(location));
        intent.putExtra("DATE", yyyyMd);
        this.startService(intent);

        startCheckService();

         **/
    }

    private void stopWarningTimeService() {
        /*
        if (null != timer) {
            timer.cancel();
            timer = null;
        }

        Intent intent = new Intent(this, WarningTimeService.class);
        this.stopService(intent);
        */
    }

    private void startWarningLocationService() {
        /*
        if (ActivityUtil.isLocationServiceWorked(StationListActivityEx.this)) {
            Log.e("Location Service", "位置监控服务正在运行中，没有必要再次启动");
            return;

        } else {
            Log.e("Location Service", "即将启动位置监控服务");
        }

        Intent intent = new Intent(this, WarningLocationService.class);
        intent.putExtra("TRAIN_INFO", trainInfoList.get(location));
        intent.putExtra("DATE", yyyyMd);
        this.startService(intent);

        startCheckService();
        */
    }

    private void stopWarningLocationService() {
        /*
        if (null != timer) {
            timer.cancel();
            timer = null;
        }

        Intent intent = new Intent(this, WarningLocationService.class);
        this.stopService(intent);
        */
    }

    Timer timer = new Timer();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    checkServiceAction();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void startCheckService() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }

        }, 10 * 1000, 20 * 1000);
    }

    private void checkServiceAction() {
        if (Constants.CURRENT_TRAIN_LATE) {
            boolean locatilnRunning = ActivityUtil.isLocationServiceWorked(this);
            Log.e("@@@", "Location:" + locatilnRunning);
            if (!locatilnRunning) {
                startWarningLocationService();

                warningStatusTextView.setText("到站预警服务已被关闭, 系统正在尝试自启动或您重启APP");
                warningStatusTextView.setTextColor(Color.parseColor("#FF001A"));

            } else {
                warningStatusTextView.setText("到站预警服务运行中, 如果不能收到预警请重启APP");
                warningStatusTextView.setTextColor(Color.parseColor("#333333"));
            }

        } else {
            boolean timerRunning = ActivityUtil.isTimeServiceWorked(this);
            Log.e("@@@", "Time:" + timerRunning);
            if (!timerRunning) {
                startWaringTimeService();
                warningStatusTextView.setText("到站预警服务已关闭, 系统正在尝试自启动或您重启APP");
                warningStatusTextView.setTextColor(Color.parseColor("#FF001A"));

            } else {
                StationModel s = getNextStation();
                warningStatusTextView.setText("到站预警服务运行中" + (null == s ? "" : ", 下一站是" + s.getStationName()));
                warningStatusTextView.setTextColor(Color.parseColor("#333333"));
            }
        }
    }

    private void requestAlarmUpdateLogInfo(String stationId, String InstanceId) {
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "AlarmUpdateLogInfo")
                .params("InstanceId", InstanceId)
                .params("DeviceId", DeviceUtil.getDeviceId(this))
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

                        Toast.makeText(StationListActivityEx.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
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
                                Log.e("", "预警失败:" + resultDto.getResult().getFlagInfo());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    // 更换车次
    private void changeTrain(int loc) {
        this.location = loc;

        TrainInfo trainInfo = trainInfoList.get(location);

        adapter.setData(trainInfo.getStationInfo());
        adapter.notifyDataSetChanged();

        Constants.CarNumberId = trainInfo.getID();
        Constants.CarNumberName = trainInfo.getTrainName();
        Constants.CardUserName = trainInfo.getUserName();

        titleSpinner.setText(Constants.CarNumberName + "/" + Constants.CardUserName);

        // 停止计时服务
        this.stopWarningLocationService();
        this.stopWarningTimeService();

        requestStationListEx("正在查询车站数据...");

    }

    private void checkUpdate() {
        UpdateKey.API_TOKEN = Constants.FIR_API_TOKEN;
        UpdateKey.APP_ID = Constants.FIR_APP_ID;
        UpdateKey.DialogOrNotification = UpdateKey.WITH_DIALOG;
        UpdateFunGO.init(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingTextView:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("TRAIN_INFO", trainInfoList.isEmpty() ? null : trainInfoList.get(location));
                this.startActivity(intent);
                break;
        }
    }

    private StationModel getNextStation() {
        for (final StationModel ss : trainInfoList.get(location).getStationInfo()) {

            Date when = DateUtil.getDate(yyyyMd, ss.getArrivalDay(), "0", ss.getArrivalTime());
            // 如果本站的时间小于当前的时间则说明已经过站了
            if (!when.after(new Date()))
                continue;

            return ss;

        }

        return null;
    }

    /**
     * 注册广播
     */
    private void registerBroadcastReceiver() {
        UpdateDistanceReceiver receiver = new UpdateDistanceReceiver();
        IntentFilter filter = new IntentFilter(ACTION_UPDATE_DISTANCE);
        registerReceiver(receiver, filter);
    }

    public class UpdateDistanceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (StationListActivityEx.ACTION_UPDATE_DISTANCE.equals(action)) {
                distanceMap = (HashMap<String, String>) intent.getSerializableExtra("DISTANCE_MAP");
                if (null != distanceMap && null != adapter) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    private class ViewHolder {
        private ShimmerFrameLayout contentLayout;
        private TextView numTextView;
        private TextView nameTextView;
        private TextView arrivalTimeTextView;
        private TextView distanceTextView;
        private TextView missionStateTextView;
    }

    public class StationListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<StationModel> list = new ArrayList<StationModel>();

        public StationListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public void setData(ArrayList<StationModel> list) {
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (null == convertView) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.layout_station_list, null);
                holder.contentLayout = (ShimmerFrameLayout) convertView.findViewById(R.id.contentLayout);
                holder.numTextView = (TextView) convertView.findViewById(R.id.numTextView);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.arrivalTimeTextView = (TextView) convertView.findViewById(R.id.arrivalTimeTextView);
                holder.distanceTextView = (TextView) convertView.findViewById(R.id.distanceTextView);
                holder.missionStateTextView = (TextView) convertView.findViewById(R.id.missionStateTextView);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final StationModel dto = this.list.get(position);

            holder.numTextView.setText(dto.getOrderNum());
            holder.nameTextView.setText(dto.getStationName());
            holder.arrivalTimeTextView.setText(dto.getArrivalTime().trim());
            holder.missionStateTextView.setText(dto.getMissionState().trim());

            if (Constants.CURRENT_TRAIN_LATE && null != distanceMap && !distanceMap.isEmpty()) {
                holder.distanceTextView.setVisibility(View.VISIBLE);
                holder.distanceTextView.setText(distanceMap.get(dto.getID()) + "公里");
            } else {
                holder.distanceTextView.setVisibility(View.GONE);
            }

            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(StationListActivityEx.this, TaskListActivity.class);
                    intent.putExtra("TRAIN", trainInfoList.get(location));
                    intent.putExtra("STATION", dto);
                    intent.putExtra("DATE", yyyyMd);
                    StationListActivityEx.this.startActivity(intent);
                }
            });

            holder.contentLayout.setAlpha(1.0f);


            return convertView;
        }
    }

    private long exitTimeMillis = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTimeMillis = System.currentTimeMillis();

        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}
