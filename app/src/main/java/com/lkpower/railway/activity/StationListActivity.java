package com.lkpower.railway.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.LoginDto;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TrainDto;
import com.lkpower.railway.dto.TrainStationDto;
import com.lkpower.railway.util.ActivityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by sth on 17/10/2016.
 * <p>
 * 车站列表
 */

@Deprecated
public class StationListActivity extends BaseActivity implements View.OnClickListener {

    private ListView listView = null;
    private StationListAdapter adapter = null;
    private List<StationModel> mList = new ArrayList<StationModel>();

    private Button runningBtn = null;

    private LoginDto loginInfo = null;
    private TrainDto.TrainDataInfo trainInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_station_list);

        loginInfo = (LoginDto) this.getIntent().getSerializableExtra("LOGIN_INFO");
        trainInfo = (TrainDto.TrainDataInfo) this.getIntent().getSerializableExtra("TRAIN_INFO");

        initView();
    }

    private void initView() {
        TextView settingTextView = (TextView) this.findViewById(R.id.settingTextView);
        settingTextView.setOnClickListener(this);

        runningBtn = (Button) this.findViewById(R.id.runningBtn);
        runningBtn.setOnClickListener(this);

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
        tempMap.put("commondKey", "StationInfo");
        tempMap.put("trainId", trainInfo.getID());

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    TrainStationDto trainDto = gson.fromJson(jsonObject, TrainStationDto.class);
                    if (trainDto.getResult().getFlag() == 1) {
//                        mList = trainDto.getDataInfo();
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(StationListActivity.this, trainDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "正在查询车站信息...");
    }

    private void requestTrainStart() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "TrainStart");
        tempMap.put("trainNumbeId", trainInfo.getID());

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
                        Toast.makeText(StationListActivity.this, "列车已标记为开始,请查看相关任务", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(StationListActivity.this, resultDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "请稍候...");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settingTextView:
                Intent intent = new Intent(this, SettingActivity.class);
                this.startActivity(intent);
                break;

            case R.id.runningBtn: {
                requestTrainStart();
            }
            break;
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
                        Intent intent = new Intent(StationListActivity.this, TaskListActivity.class);
                        intent.putExtra("TRAIN_INFO", trainInfo);
                        intent.putExtra("LOGIN_INFO", loginInfo);
                        intent.putExtra("STATION_INFO", dto);
                        StationListActivity.this.startActivity(intent);

                    } else {
                        new SweetAlertDialog(StationListActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText("提示").setContentText("火车尚未出发,暂不能查看任务。请点击左上角的开始按纽。").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        }).show();
                    }
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
