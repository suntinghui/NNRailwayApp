package com.lkpower.railway.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.lkpower.railway.dto.DeviceDetailInfoDto;
import com.lkpower.railway.dto.DeviceInfo;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.NotificationUtil;

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
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.R.attr.data;
import static android.R.attr.start;
import static com.lkpower.railway.R.drawable.train;
import static com.lkpower.railway.R.id.contactLayout;
import static com.lkpower.railway.R.id.earlyWarningBtn;
import static com.lkpower.railway.R.id.settingTextView;
import static com.lkpower.railway.R.id.title;
import static com.lkpower.railway.R.id.titleTextView;
import static u.aly.av.I;
import static u.aly.cw.f;
import static u.aly.cw.i;


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
    private ArrayList<TrainInfo> trainInfoList = null;

    private List<StationModel> mList = new ArrayList<StationModel>();
    private List<String> trainList = new ArrayList<String>();

    private Button runningBtn = null;

    private int location = 0;

    private ArrayList<Timer> timerList = new ArrayList<Timer>();

    private String yyyyMd = DateUtil.getCurrentDate3();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_list);

        initView();
    }

    @Override
    protected void onDestory() {
        super.onDestory();

        stopTimers();
        timerList.clear();
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
                requestStationList();
            }
        });

        requestStationList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.e("===","=======================onNewIntent");

        boolean EarlyWarning = intent.getBooleanExtra("EarlyWarning", false);
        if (EarlyWarning) { // 预警
            requestEarlyWarning(intent.getStringExtra("stationId"), intent.getStringExtra("SerialNumber"));
        }

    }

    private void requestStationList() {
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
                        deviceInfo = deviceDetailInfoDto.getDataInfo().getDeviceInfo();
                        trainInfoList = (ArrayList<TrainInfo>) deviceDetailInfoDto.getDataInfo().getTrainInfo();

                        Constants.DeviceInfo = deviceInfo;

                        if (trainInfoList != null && trainInfoList.size() != 0) {
                            runningBtn.setVisibility(View.VISIBLE);
                            listView.setAlpha(1.0f);

                            mList = trainInfoList.get(0).getStationInfo();
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

        this.addToRequestQueue(request, "正在查询车站信息...");
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

                        startTimer();

                    } else {
                        Toast.makeText(StationListActivityEx.this, resultDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "请稍候...");
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

        this.addToRequestQueue(request, null);
    }

    private void stopTimers(){
        try{
            for (Timer timer: timerList) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            timerList.clear();
        }
    }

    // 更换车次
    private void changeTrain(int location) {
        stopTimers();

        Constants.RUNNING = false;
        runningBtn.setText("启动");

        mList = trainInfoList.get(location).getStationInfo();
        adapter.notifyDataSetChanged();

        Constants.CarNumberId = trainInfoList.get(location).getID();
        Constants.CarNumberName = trainInfoList.get(location).getTrainName();

        titleSpinner.setText(Constants.CarNumberName + "/" + Constants.DeviceInfo.getUserName());
    }

    private void startTimer() {
        timerList.clear();

        try {
            for (final StationModel station : trainInfoList.get(location).getStationInfo()) {
                Date when = DateUtil.getDate(yyyyMd, station.getArrivalDay(), station.getAheadTime(), station.getStartTime());
                Log.e("------", when.toString());

                // 如果本站的时间小于当前的时间则说明已经过站了,则不再提醒。
                if (when.before(new Date()))
                    continue;

                Log.e("======", when.toString());

                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            String content = station.getStationName() + "还有" + station.getAheadTime() + "分钟到站,请您及时完成相关任务。";
                            Intent intent = new Intent(StationListActivityEx.this, StationListActivityEx.class);
                            intent.putExtra("EarlyWarning", true);
                            intent.putExtra("SerialNumber", trainInfoList.get(location).getSerialNumber());
                            intent.putExtra("stationId", station.getID());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            NotificationUtil.showNotification(StationListActivityEx.this, "到站提醒", content, intent);
                        }
                        super.handleMessage(msg);
                    }

                    ;
                };

                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        // 需要做的事:发送消息
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                };

                timerList.add(timer);

                timer.schedule(task, when);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case settingTextView:
                Intent intent = new Intent(this, SettingActivity.class);
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

    private void startRunning(){
        SweetAlertDialog dialog = new SweetAlertDialog(StationListActivityEx.this, SweetAlertDialog.WARNING_TYPE).setTitleText("确定启动?").setContentText("请确认列车发车日期:"+DateUtil.getCurrentDate2()).setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
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

    private void showUpdateDate(){
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

    private void stopRunning(){
        new SweetAlertDialog(StationListActivityEx.this, SweetAlertDialog.WARNING_TYPE).setTitleText("确定停止?").setContentText("停止后将无法完成任务并停止到站预警").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                Constants.RUNNING = !Constants.RUNNING;

                sDialog.cancel();

                stopTimers();

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
        private LinearLayout contentLayout;
        private TextView numTextView;
        private TextView nameTextView;
        private TextView arrivalTimeTextView;
        private TextView gooutTimeTextView;
        private Button earlyWarningBtn;
    }

    public class StationListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public StationListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
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
                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.numTextView = (TextView) convertView.findViewById(R.id.numTextView);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.arrivalTimeTextView = (TextView) convertView.findViewById(R.id.arrivalTimeTextView);
                holder.gooutTimeTextView = (TextView) convertView.findViewById(R.id.gooutTimeTextView);
                holder.earlyWarningBtn = (Button) convertView.findViewById(R.id.earlyWarningBtn);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final StationModel dto = mList.get(position);

            holder.numTextView.setText(position + 1 + "");
            holder.nameTextView.setText(dto.getStationName());
            holder.arrivalTimeTextView.setText(dto.getArrivalTime().trim());
            holder.gooutTimeTextView.setText(dto.getStartTime().trim());
            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Constants.RUNNING) {
                        Intent intent = new Intent(StationListActivityEx.this, TaskListActivity.class);
                        intent.putExtra("TRAIN", trainInfoList.get(location));
                        intent.putExtra("STATION", dto);
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
