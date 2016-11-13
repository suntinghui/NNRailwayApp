package com.lkpower.railway.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
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

    private LoginDto loginInfo = null;
    private TrainDto.TrainDataInfo trainInfo = null;
    private StationModel stationModel = null;
    private TaskDto.TaskListInfoDto taskInfo = null;

    private TaskDto.TaskListInfoDto info = null;

    private LinearLayout imageLayout = null;
    private LinearLayout infoLayout = null;
    private TextView taskNameTextView = null;
    private TextView taskIdTextView = null;
    private TextView executorTextView = null;
    private TextView updateTimeTextView = null;
    private TextView remarkTextView = null;
    private TextView stateTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_info_download);

        loginInfo = (LoginDto) this.getIntent().getSerializableExtra("LOGIN_INFO");
        trainInfo = (TrainDto.TrainDataInfo) this.getIntent().getSerializableExtra("TRAIN_INFO");
        stationModel = (StationModel) this.getIntent().getSerializableExtra("STATION_INFO");
        taskInfo = (TaskDto.TaskListInfoDto) this.getIntent().getSerializableExtra("TASK_INFO");

        initView();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("任务详情");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        imageLayout = (LinearLayout) this.findViewById(R.id.imageLayout);
        infoLayout = (LinearLayout) this.findViewById(R.id.infoLayout);
        taskNameTextView = (TextView) this.findViewById(R.id.taskNameTextView);
        taskIdTextView = (TextView) this.findViewById(R.id.taskIdTextView);
        executorTextView = (TextView) this.findViewById(R.id.executorTextView);
        updateTimeTextView = (TextView) this.findViewById(R.id.updateTimeTextView);
        remarkTextView = (TextView) this.findViewById(R.id.remarkTextView);
        stateTextView = (TextView) this.findViewById(R.id.stateTextView);

        infoLayout.setVisibility(View.GONE);

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

        this.addToRequestQueue(request, "请稍候...");
    }

    private void refreshView() {
        infoLayout.setVisibility(View.VISIBLE);
        taskNameTextView.setText(info.getMisName());
        taskIdTextView.setText(info.getID());
        executorTextView.setText(info.getExecutorName());
        updateTimeTextView.setText(info.getUpdateTime());
        remarkTextView.setText(info.getRemark());
        stateTextView.setText(info.getState().equals("1") ? "未完成" : "已完成");

        ArrayList<ImgDataDto> list = info.getImgData();

        for (ImgDataDto imgDto : list) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(800, 800);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageBitmap(stringtoBitmap(imgDto.getImgInfo()));
            imageView.setPadding(0, 20, 0, 0);
            imageLayout.addView(imageView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }

    private Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
