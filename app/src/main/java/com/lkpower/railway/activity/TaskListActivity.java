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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.dto.TrainInfo;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

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

    private void requestTaskList(final String msg) {
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "MissionInfoByUser")
                .params("serialNumber", this.getIntent().getStringExtra("DATE"))
                .params("userId", train.getUserId())
                .params("stationId", station.getID())
                .params("deviceId", DeviceUtil.getDeviceId(this))
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        HUDUtil.showHUD(TaskListActivity.this, msg);
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);

                        e.printStackTrace();

                        Toast.makeText(TaskListActivity.this, ExceptionUtil.getMsg(e), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);

                        HUDUtil.dismiss();
                    }

                    @Override
                    public void onSuccess(String jsonObject, Call call, okhttp3.Response response) {
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
    }

    private class ViewHolder {
        private LinearLayout contentLayout;
        private TextView taskName;
        private TextView stateTextView;
        private TextView remarkTextView; // 任务描述
        private TextView tipTextView; // 完成情况
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
                holder.tipTextView = (TextView) convertView.findViewById(R.id.tipTextView);
                holder.executorNameTextView = (TextView) convertView.findViewById(R.id.executorNameTextView);
                holder.updateTimeTextView = (TextView) convertView.findViewById(R.id.updateTimeTextView);

                convertView.setTag(holder);
            } else {
                holder = (TaskListActivity.ViewHolder) convertView.getTag();
            }

            final TaskDto.TaskListInfoDto info = mList.get(position);

            holder.taskName.setText("【" + station.getStationName() + "】" + info.getMisName());
            holder.stateTextView.setText(info.getState().equals("1") ? "未完成" : "已完成");
            holder.remarkTextView.setText(TextUtils.isEmpty(info.getMisRemark()) ? "无" : info.getMisRemark().replace("\n", ""));
            holder.tipTextView.setText(TextUtils.isEmpty(info.getRemark()) ? "无" : info.getRemark());
            holder.executorNameTextView.setText(info.getExecutorName());
            holder.updateTimeTextView.setText(info.getUpdateTime());
            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (info.getState().equals("1")) {
                        Intent intent = new Intent(TaskListActivity.this, TaskInfoUploadActivityEx.class);
                        intent.putExtra("STATION", station);
                        intent.putExtra("TASK_INFO", info);
                        startActivityForResult(intent, 0);

                    } else {
                        Intent intent = new Intent(TaskListActivity.this, TaskInfoDownloadActivity.class);
                        intent.putExtra("STATION", station);
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
