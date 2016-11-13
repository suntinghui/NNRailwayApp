package com.lkpower.railway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.lkpower.railway.util.DeviceUtil;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.R.attr.data;
import static com.lkpower.railway.R.id.settingTextView;
import static com.lkpower.railway.R.id.title;
import static com.lkpower.railway.R.id.titleTextView;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_list);

        initView();
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

        ActivityUtil.setEmptyView(this, listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStationList();
            }
        });

        requestStationList();
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
                        runningBtn.setAlpha(Constants.RUNNING ? 0.5f : 1.0f);
                        runningBtn.setText("已出发");
                        Toast.makeText(StationListActivityEx.this, "列车已标记为开始,请查看相关任务", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();

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

    private void requestEarlyWarning(String missionStateId) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "UpdateRead");
        tempMap.put("missionStateId", missionStateId);

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                    if (resultDto.getResult().getFlag() == 1) {
                        Constants.RUNNING = true;
                        runningBtn.setAlpha(Constants.RUNNING ? 0.5f : 1.0f);
                        runningBtn.setText("已出发");
                        Toast.makeText(StationListActivityEx.this, "列车已标记为开始,请查看相关任务", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();

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

    // 更换车次
    private void changeTrain(int location) {
        mList = trainInfoList.get(location).getStationInfo();
        adapter.notifyDataSetChanged();

        Constants.CarNumberId = trainInfoList.get(location).getID();
        Constants.CarNumberName = trainInfoList.get(location).getTrainName();

        titleSpinner.setText(Constants.CarNumberName + "/" + Constants.DeviceInfo.getUserName());
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
                    requestTrainStart();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(StationListActivityEx.this, "启动失败", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
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
                    requestEarlyWarning("");
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
