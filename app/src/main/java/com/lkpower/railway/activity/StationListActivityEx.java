package com.lkpower.railway.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.Response;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.ActivityManager;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.DeviceDetailInfoDto;
import com.lkpower.railway.dto.DeviceInfo;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.UMengPushUtil;
import com.umeng.message.IUmengCallback;
import com.umeng.message.PushAgent;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by sth on 17/10/2016.
 * <p>
 * 车站列表
 */

public class StationListActivityEx extends BaseActivity implements View.OnClickListener {

    private NiceSpinner titleSpinner = null;

    private ListView listView = null;
    private StationListAdapter adapter = null;

    private DeviceInfo deviceInfo = null;
    private ArrayList<TrainInfo> trainInfoList = new ArrayList<TrainInfo>();

    private ArrayList<StationModel> mList = new ArrayList<StationModel>();
    private ArrayList<String> trainList = new ArrayList<String>();

    private Button runningBtn = null;

    private int location = 0;

    private String yyyyMd = DateUtil.getCurrentDate3();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_list);

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
    }

    private void initView() {
        titleSpinner = (NiceSpinner) this.findViewById(R.id.titleSpinner);
        List<String> initSet = new LinkedList<>(Arrays.asList("车站列表"));
        titleSpinner.attachDataSource(initSet);

        TextView settingTextView = (TextView) this.findViewById(R.id.settingTextView);
        settingTextView.setOnClickListener(this);

        runningBtn = (Button) this.findViewById(R.id.runningBtn);
        runningBtn.setOnClickListener(this);
        runningBtn.setVisibility(View.INVISIBLE);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new StationListAdapter(this);
        listView.setAdapter(adapter);
        listView.setAlpha(0.5f);

        ActivityUtil.setEmptyView(this, listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStationList("正在查询车站信息...");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestStationList(trainInfoList.isEmpty() ? "正在查询车站信息..." : null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.e("===", "=======================onNewIntent");

        boolean LATE_TYPE = intent.getBooleanExtra("LATE_TYPE", false);
        boolean late = intent.getBooleanExtra("LATE", false);
        Constants.CURRENT_TRAIN_LATE = late;

        if (LATE_TYPE) {
            if (!Constants.RUNNING) {
                Toast.makeText(this, "收到列车晚点更新通知,但是应用没有\"启动\",忽略该信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if (late) {
                Toast.makeText(this, "列车被标记为晚点,切换到GPS地理预警模式", Toast.LENGTH_SHORT).show();
                this.startWarningLocationService();
                this.stopWarningTimeService();

            } else {
                Toast.makeText(this, "列车晚点状态取消,切换到时间预警模式", Toast.LENGTH_SHORT).show();
                this.startWaringTimeService();
                this.stopWarningLocationService();
            }

            // 用户点击了预警推送,告知服务器

        } else {
            Intent warningIntent = new Intent(this, WarningNotificationClickReceiver.class);
            warningIntent.putExtra("PLAY", false);
            this.sendBroadcast(warningIntent);

            boolean EarlyWarning = intent.getBooleanExtra("EarlyWarning", false);
            if (EarlyWarning) { // 预警
                requestEarlyWarning(intent.getStringExtra("stationId"), intent.getStringExtra("SerialNumber"));
            }
        }
    }

    private void requestStationList(String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "DeviceDetailInfo");
        tempMap.put("DeviceId", DeviceUtil.getDeviceId(this));

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    DeviceDetailInfoDto deviceDetailInfoDto = gson.fromJson(jsonObject, DeviceDetailInfoDto.class);
                    if (deviceDetailInfoDto.getResult().getFlag() == 1) {
                        trainList.clear();

                        deviceInfo = deviceDetailInfoDto.getDataInfo().getDeviceInfo();
                        trainInfoList = (ArrayList<TrainInfo>) deviceDetailInfoDto.getDataInfo().getTrainInfo();

                        Constants.DeviceInfo = deviceInfo;

                        if (trainInfoList != null && trainInfoList.size() != 0) {
                            runningBtn.setVisibility(View.VISIBLE);
                            listView.setAlpha(1.0f);

                            mList = trainInfoList.get(0).getStationInfo();
                            adapter.setRecentlyOrderNum(DateUtil.getRecentLyStation(yyyyMd, mList).getOrderNum());
                            adapter.notifyDataSetChanged();

                            Constants.CarNumberId = trainInfoList.get(0).getID();
                            Constants.CarNumberName = trainInfoList.get(0).getTrainName();

                            titleSpinner.setText(Constants.CarNumberName + "/" + Constants.DeviceInfo.getUserName());

                            for (TrainInfo info : trainInfoList) {
                                trainList.add(info.getTrainName() + "/" + Constants.DeviceInfo.getUserName());
                            }
                            titleSpinner.attachDataSource(trainList);
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
                            runningBtn.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        Toast.makeText(StationListActivityEx.this, deviceDetailInfoDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                        runningBtn.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, msg);
    }

    private void requestTrainStart() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "TrainStart");
        tempMap.put("trainNumbeId", trainInfoList.get(location).getID());

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                    if (resultDto.getResult().getFlag() == 1) {
                        Constants.RUNNING = true;
                        runningBtn.setText("停止");
                        Toast.makeText(StationListActivityEx.this, "列车已标记为开始,请查看相关任务", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        listView.setAlpha(1.0f);

                        if (Constants.CURRENT_TRAIN_LATE) {
                            startWarningLocationService();
                            stopWarningTimeService();

                        } else {
                            startWaringTimeService();
                            stopWarningLocationService();
                        }

                    } else {
                        Toast.makeText(StationListActivityEx.this, resultDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "请稍候...");
    }

    private void startWaringTimeService() {
        Intent intent = new Intent(this, WarningTimeService.class);
        intent.putExtra("TRAIN_INFO", trainInfoList.get(location));
        intent.putExtra("DATE", yyyyMd);
        this.startService(intent);
    }

    private void stopWarningTimeService(){
        Intent intent = new Intent(this, WarningTimeService.class);
        this.stopService(intent);
    }

    private void startWarningLocationService(){
        Intent intent = new Intent(this, WarningLocationService.class);
        intent.putExtra("STATION_LIST", trainInfoList.get(location).getStationInfo());
        intent.putExtra("SERIALNUMBER", trainInfoList.get(location).getSerialNumber());
        intent.putExtra("DATE", yyyyMd);
        this.startService(intent);
    }

    private void stopWarningLocationService(){
        Intent intent = new Intent(this, WarningLocationService.class);
        this.stopService(intent);
    }

    private void requestEarlyWarning(String stationId, String serialNumber) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "UpdateRead");
        tempMap.put("SerialNumber", serialNumber);
        tempMap.put("stationId", stationId);

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                    if (resultDto.getResult().getFlag() == 1) {
                        Log.e("", "=====================预警==================");

                    } else {
                        Log.e("", "预警失败:" + resultDto.getResult().getFlagInfo());
                        //Toast.makeText(StationListActivityEx.this, resultDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, null);
    }


    // 更换车次
    private void changeTrain(int location) {
        // 停止计时服务
        this.stopWarningLocationService();
        this.stopWarningTimeService();

        Constants.RUNNING = false;
        runningBtn.setText("启动");

        mList = trainInfoList.get(location).getStationInfo();
        adapter.setRecentlyOrderNum(DateUtil.getRecentLyStation(yyyyMd, mList).getOrderNum());
        adapter.notifyDataSetChanged();

        Constants.CarNumberId = trainInfoList.get(location).getID();
        Constants.CarNumberName = trainInfoList.get(location).getTrainName();

        titleSpinner.setText(Constants.CarNumberName + "/" + Constants.DeviceInfo.getUserName());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingTextView:
                Intent intent = new Intent(this, SettingActivity.class);
                intent.putExtra("TRAIN_INFO", trainInfoList.get(location));
                this.startActivity(intent);
                break;

            case R.id.runningBtn: {
                try {
                    if (Constants.RUNNING) {
                        stopRunning();

                    } else {
                        startRunning();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    private void startRunning() {
        SweetAlertDialog dialog = new SweetAlertDialog(StationListActivityEx.this, SweetAlertDialog.WARNING_TYPE).setTitleText("确定启动?").setContentText("请确认列车发车日期:" + DateUtil.getCurrentDate2()).setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();
                requestTrainStart();
            }
        }).setCancelText("修改日期").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
                showUpdateDate();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showUpdateDate() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();

        DatePicker picker = new DatePicker(this);
        picker.setDate(Integer.parseInt(DateUtil.getCurrentYear()), Integer.parseInt(DateUtil.getCurrentDay()));
        picker.setMode(DPMode.SINGLE);
        picker.setTodayDisplay(true);
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                dialog.dismiss();

                yyyyMd = date;
                requestTrainStart();
            }
        });

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setContentView(picker, params);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void stopRunning() {
        new SweetAlertDialog(StationListActivityEx.this, SweetAlertDialog.WARNING_TYPE).setTitleText("确定停止?").setContentText("停止后将无法完成任务并停止到站预警").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                Constants.RUNNING = !Constants.RUNNING;

                sDialog.cancel();

                // 停止计时服务
                stopWarningLocationService();
                stopWarningTimeService();

                runningBtn.setText("启动");

                listView.setAlpha(0.5f);

                Toast.makeText(StationListActivityEx.this, "已停止", Toast.LENGTH_SHORT).show();
            }
        }).setCancelText("取消").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
            }
        }).show();
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
        private TextView gooutTimeTextView;
        private Button earlyWarningBtn;
        private TextView missionStateTextView;
    }

    public class StationListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private String recentlyOrderNum = null;

        public StationListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public String getRecentlyOrderNum() {
            return recentlyOrderNum;
        }

        public void setRecentlyOrderNum(String recentlyOrderNum) {
            this.recentlyOrderNum = recentlyOrderNum;
        }

        @Override
        public int getCount() {
            return mList.size();
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
                holder.gooutTimeTextView = (TextView) convertView.findViewById(R.id.gooutTimeTextView);
                holder.earlyWarningBtn = (Button) convertView.findViewById(R.id.earlyWarningBtn);
                holder.missionStateTextView = (TextView) convertView.findViewById(R.id.missionStateTextView);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final StationModel dto = mList.get(position);

            holder.numTextView.setText(dto.getOrderNum());
            holder.nameTextView.setText(dto.getStationName());
            holder.arrivalTimeTextView.setText(dto.getArrivalTime().trim());
            holder.gooutTimeTextView.setText(dto.getStartTime().trim());
            holder.missionStateTextView.setText(dto.getMissionState().trim());

            /*
            try {
                if (Constants.RUNNING && !Constants.CURRENT_TRAIN_LATE) {
                    if (Integer.parseInt(this.getRecentlyOrderNum()) == position + 1) {
                        holder.contentLayout.setDuration(1000);
                        holder.contentLayout.setRepeatDelay(3000);
                        holder.contentLayout.startShimmerAnimation();
                    } else {
                        holder.contentLayout.stopShimmerAnimation();
                    }
                } else {
                    holder.contentLayout.stopShimmerAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constants.RUNNING) {
                        Intent intent = new Intent(StationListActivityEx.this, TaskListActivity.class);
                        intent.putExtra("TRAIN", trainInfoList.get(location));
                        intent.putExtra("STATION", dto);
                        intent.putExtra("DATE", DateUtil.yyyyMd2yyyyMMDD(yyyyMd));
                        StationListActivityEx.this.startActivity(intent);

                    } else {
                        new SweetAlertDialog(StationListActivityEx.this, SweetAlertDialog.NORMAL_TYPE).setTitleText("提示").setContentText("火车尚未出发,暂不能查看任务。请点击左上角的开始按纽。").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        }).show();
                    }
                }
            });

            holder.earlyWarningBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestEarlyWarning("", "");
                }
            });

            holder.contentLayout.setAlpha(Constants.RUNNING ? 1.0f : 0.5f);


            return convertView;
        }
    }

    private long exitTimeMillis = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTimeMillis = System.currentTimeMillis();

        } else {
            for (Activity act : ActivityManager.getInstance().getAllActivity()) {
                act.finish();
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}
