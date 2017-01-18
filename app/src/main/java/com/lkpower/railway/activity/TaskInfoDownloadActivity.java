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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.lkpower.railway.R;
import com.lkpower.railway.activity.view.MyGridView;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.ImgDataDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TaskDetailDto;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;

import anetwork.channel.cache.ImageCacheManager;
import okhttp3.Call;

import static com.lkpower.railway.R.id.imageView;

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

    private MyGridView noScrollgridview;
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

        noScrollgridview = (MyGridView) findViewById(R.id.noScrollgridview);
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
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "MissionInfoDetail")
                .params("missionStateId", taskInfo.getID())
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        HUDUtil.showHUD(TaskInfoDownloadActivity.this, "正在请求数据...");
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);

                        e.printStackTrace();

                        Toast.makeText(TaskInfoDownloadActivity.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
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
                convertView = inflater.inflate(R.layout.item_download_grid, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            UrlImageViewHelper.setUrlDrawable(holder.image, list.get(position).getImgInfoThumbPath(), R.drawable.image_loading);

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }
    }
}
