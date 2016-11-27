package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.dto.LoginDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.dto.TrainDto;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sth on 19/10/2016.
 */

public class TaskListActivity extends BaseActivity implements View.OnClickListener {

    private ListView listView = null;
    private TaskListActivity.TaskListAdapter adapter = null;
    private List<TaskDto.TaskListInfoDto> mList = new ArrayList<TaskDto.TaskListInfoDto>();

    private TrainInfo train = null;
    private StationModel station = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_list);

        train = (TrainInfo) this.getIntent().getSerializableExtra("TRAIN");
        station = (StationModel) this.getIntent().getSerializableExtra("STATION");

        initView();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("任务列表");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new TaskListActivity.TaskListAdapter(this);
        listView.setAdapter(adapter);

        ActivityUtil.setEmptyView(this, listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTaskList("正在查询任务列表...");
            }
        });

        requestTaskList("正在查询任务列表...");
    }

    private void requestTaskList(String msg) {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "MissionInfoByUser");
        tempMap.put("serialNumber", DateUtil.getCurrentDate());
        tempMap.put("userId", Constants.DeviceInfo.getID());
        tempMap.put("stationId", station.getID());

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    TaskDto taskDto = gson.fromJson(jsonObject, TaskDto.class);
                    if (taskDto.getResult().getFlag() == 1) {
                        mList.clear();
                        mList = taskDto.getDataInfo();

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(TaskListActivity.this, taskDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, msg);
    }

    private class ViewHolder {
        private LinearLayout contentLayout;
        private TextView taskName;
        private TextView stateTextView;
        private TextView remarkTextView;
        private TextView executorNameTextView;
        private TextView updateTimeTextView;
    }

    private class TaskListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public TaskListAdapter(Context context) {
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
            TaskListActivity.ViewHolder holder = null;

            if (null == convertView) {
                holder = new TaskListActivity.ViewHolder();

                convertView = mInflater.inflate(R.layout.layout_task_list, null);
                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.taskName = (TextView) convertView.findViewById(R.id.taskName);
                holder.stateTextView = (TextView) convertView.findViewById(R.id.stateTextView);
                holder.remarkTextView = (TextView) convertView.findViewById(R.id.remarkTextView);
                holder.executorNameTextView = (TextView) convertView.findViewById(R.id.executorNameTextView);
                holder.updateTimeTextView = (TextView) convertView.findViewById(R.id.updateTimeTextView);

                convertView.setTag(holder);
            } else {
                holder = (TaskListActivity.ViewHolder) convertView.getTag();
            }

            final TaskDto.TaskListInfoDto info = mList.get(position);

            holder.taskName.setText(info.getMisName());
            holder.stateTextView.setText(info.getState().equals("1") ? "未完成" : "已完成");
            holder.remarkTextView.setText(TextUtils.isEmpty(info.getRemark()) ? "无" : info.getRemark());
            holder.executorNameTextView.setText(info.getExecutorName());
            holder.updateTimeTextView.setText(info.getUpdateTime());
            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (info.getState().equals("1")) {
                        Intent intent = new Intent(TaskListActivity.this, TaskInfoUploadActivityEx.class);
                        intent.putExtra("TASK", info);
                        startActivityForResult(intent, 0);

                    } else {
                        Intent intent = new Intent(TaskListActivity.this, TaskInfoDownloadActivity.class);

                        intent.putExtra("TASK_INFO", info);
                        startActivity(intent);

                    }
                }
            });


            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        requestTaskList(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
