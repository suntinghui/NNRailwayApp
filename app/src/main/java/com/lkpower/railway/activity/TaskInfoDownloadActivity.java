package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.activity.view.CustomNetworkImageView;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.ImageCacheManager;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.ImgDataDto;
import com.lkpower.railway.dto.LoginDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TaskDetailDto;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.dto.TrainDto;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sth on 20/10/2016.
 */

public class TaskInfoDownloadActivity extends BaseActivity implements View.OnClickListener {

    private TaskDto.TaskListInfoDto taskInfo = null;
    private StationModel station = null;

    private TaskDto.TaskListInfoDto info = null;

    private LinearLayout infoLayout = null;
    private TextView taskNameTextView = null;
    private TextView taskIdTextView = null;
    private TextView executorTextView = null;
    private TextView updateTimeTextView = null;
    private TextView remarkTextView = null;
    private TextView tipTextView = null;
    private TextView stateTextView = null;

    private GridView noScrollgridview;
    private GridAdapter adapter;
    private ArrayList<ImgDataDto> list = new ArrayList<ImgDataDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_info_download);

        taskInfo = (TaskDto.TaskListInfoDto) this.getIntent().getSerializableExtra("TASK_INFO");
        station = (StationModel) this.getIntent().getSerializableExtra("STATION");

        initView();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("任务详情");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        infoLayout = (LinearLayout) this.findViewById(R.id.infoLayout);
        taskNameTextView = (TextView) this.findViewById(R.id.taskNameTextView);
        taskIdTextView = (TextView) this.findViewById(R.id.taskIdTextView);
        executorTextView = (TextView) this.findViewById(R.id.executorTextView);
        updateTimeTextView = (TextView) this.findViewById(R.id.updateTimeTextView);
        remarkTextView = (TextView) this.findViewById(R.id.remarkTextView);
        tipTextView = (TextView) this.findViewById(R.id.tipTextView);
        stateTextView = (TextView) this.findViewById(R.id.stateTextView);

        infoLayout.setVisibility(View.GONE);

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent intent = new Intent(TaskInfoDownloadActivity.this, ShowImageActivity.class);
                intent.putExtra("url", list.get(arg2).getImgInfoNormalPath());
                startActivity(intent);
            }
        });

        requestDownloadTask();
    }

    private void requestDownloadTask() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "MissionInfoDetail");
        tempMap.put("missionStateId", taskInfo.getID());

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    TaskDetailDto taskDetailDto = gson.fromJson(jsonObject, TaskDetailDto.class);

                    if (taskDetailDto.getResult().getFlag() == 1) {

                        info = taskDetailDto.getDataInfo();

                        refreshView();

                    } else {
                        Toast.makeText(TaskInfoDownloadActivity.this, taskDetailDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "请稍候...");
    }

    private void refreshView() {
        infoLayout.setVisibility(View.VISIBLE);
        taskNameTextView.setText("【" + station.getStationName() + "】" + info.getMisName());
        taskIdTextView.setText(info.getID());
        executorTextView.setText(info.getExecutorName());
        updateTimeTextView.setText(info.getUpdateTime());
        remarkTextView.setText(TextUtils.isEmpty(info.getMisRemark()) ? "无" : info.getMisRemark().replace("\n", ""));
        tipTextView.setText(TextUtils.isEmpty(info.getRemark()) ? "无" : info.getRemark());
        stateTextView.setText(info.getState().equals("1") ? "未完成" : "已完成");

        list = info.getImgData();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int arg0) {
            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_download_grid,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (CustomNetworkImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setImageUrl(list.get(position).getImgInfoThumbPath(), ImageCacheManager.getInstance().getImageLoader());

            return convertView;
        }

        public class ViewHolder {
            public CustomNetworkImageView image;
        }
    }
}
