package com.lkpower.railway.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.activity.view.UploadImageView;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.LoginDto;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.dto.TrainDto;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.StringUtil;
import com.pizidea.imagepicker.AndroidImagePicker;
import com.pizidea.imagepicker.ImgLoader;
import com.pizidea.imagepicker.UilImgLoader;
import com.pizidea.imagepicker.Util;
import com.pizidea.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.lkpower.railway.R.id.toggleGestureLockBtn;
import static com.lkpower.railway.util.ActivityUtil.verifyStoragePermissions;

/**
 * Created by sth on 19/10/2016.
 */

@Deprecated
public class TaskInfoUploadActivity extends BaseActivity implements View.OnClickListener {

    private EditText remarkEditText = null;
    private Button toggleGestureLockBtn = null;

    private boolean toggleFlag = false;

    ImgLoader presenter = new UilImgLoader();
    GridView mGridView;
    SelectAdapter mAdapter;

    private int screenWidth;

    private Button pickPhotoBtn = null;
    private Button doneBtn = null;

    private LinearLayout uploadImageLayout = null;

    private LoginDto loginInfo = null;
    private TrainDto.TrainDataInfo trainInfo = null;
    private StationModel stationModel = null;
    private TaskDto.TaskListInfoDto taskInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_info_upload);

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

        pickPhotoBtn = (Button) this.findViewById(R.id.pickPhotoBtn);
        pickPhotoBtn.setOnClickListener(this);

        doneBtn = (Button) this.findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(this);

        remarkEditText = (EditText) this.findViewById(R.id.remarkEditText);

        toggleGestureLockBtn = (Button) this.findViewById(R.id.toggleGestureLockBtn);
        toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
        toggleGestureLockBtn.setOnClickListener(this);

        uploadImageLayout = (LinearLayout) this.findViewById(R.id.uploadImageLayout);

        UploadImageView imageView = new UploadImageView(this);
        uploadImageLayout.addView(imageView);

        mGridView = (GridView) findViewById(R.id.gridview);
        mAdapter = new SelectAdapter(this);
        mGridView.setAdapter(mAdapter);

        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        ActivityUtil.verifyStoragePermissions(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.toggleGestureLockBtn:
                if (toggleFlag) {
                    // 如果设置了手势密码，则去关闭手势密码
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
                    Toast.makeText(this, "已经标记为未完成", Toast.LENGTH_SHORT).show();
                } else {
                    // 如果没有设置，则切换图片，并去设置
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_on);
                    Toast.makeText(this, "已经标记为完成", Toast.LENGTH_SHORT).show();
                }

                toggleFlag = !toggleFlag;

                break;

            case R.id.pickPhotoBtn: {
                AndroidImagePicker.getInstance().pickMulti(TaskInfoUploadActivity.this, true, new AndroidImagePicker.OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<ImageItem> items) {
                        if (items != null && items.size() > 0) {
                            Log.i("==", "=====选择了：" + items.get(0).path);
                            mAdapter.clear();
                            mAdapter.addAll(items);
                        }
                    }
                });

                break;
            }

            case R.id.doneBtn:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE).setTitleText("\n确认提交？").setContentText(toggleFlag?"标记为完成状态下,数据提交后不可更改":"").setCancelText("取消").setConfirmText("确定").showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();

                        requestUpdateTask();
                    }
                }).show();

                break;
        }
    }


    private void requestUpdateTask() {
        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("ID", taskInfo.getID());
        jsonMap.put("missionId", taskInfo.getMissionId());
        jsonMap.put("executor", taskInfo.getExecutor());
        jsonMap.put("state", toggleFlag ? "4" : "1");
        jsonMap.put("remark", remarkEditText.getText().toString());
        jsonMap.put("updateUser", loginInfo.getDataInfo().getID());
        jsonMap.put("updateTime", DateUtil.getCurrentDateTime());

        if (!mAdapter.isEmpty()) {
            ArrayList imgList = new ArrayList();
            for (int i = 0; i < mAdapter.getCount(); i++) {
                HashMap<String, String> imgMap = new HashMap<String, String>();
                imgMap.put("imgData", StringUtil.Image2Base64(mAdapter.getItem(i).path));
                imgList.add(imgMap);
            }
            jsonMap.put("ImgInfo", imgList);
        }


        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "UpdateMissionInfo");
        tempMap.put("jsonData", new GsonBuilder().create().toJson(jsonMap));

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);

                    if (resultMsgDto.getResult().getFlag() == 1) {
                        Toast.makeText(TaskInfoUploadActivity.this, "数据更新成功", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(TaskInfoUploadActivity.this, resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "正在上传数据...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Activity.RESULT_OK) {
            List<ImageItem> imageList = AndroidImagePicker.getInstance().getSelectedImages();
            mAdapter.clear();
            mAdapter.addAll(imageList);
        }
        /*
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == REQ_IMAGE) {
                List<ImageItem> imageList = AndroidImagePicker.getInstance().getSelectedImages();
                mAdapter.clear();
                mAdapter.addAll(imageList);
            }else if(requestCode == REQ_IMAGE_CROP){
                Bitmap bmp = (Bitmap)data.getExtras().get("bitmap");
                Log.i(TAG,"-----"+bmp.getRowBytes());
            }
        }
        */

    }

    class SelectAdapter extends ArrayAdapter<ImageItem> {

        //private int mResourceId;
        public SelectAdapter(Context context) {
            super(context, 0);
            //this.mResourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ImageItem item = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            //View view = inflater.inflate(mResourceId, null);
            int width = (screenWidth - Util.dp2px(TaskInfoUploadActivity.this, 10 * 2)) / 3;

            ImageView imageView = new ImageView(TaskInfoUploadActivity.this);
            imageView.setBackgroundColor(Color.GRAY);
            GridView.LayoutParams params = new AbsListView.LayoutParams(width, width);
            imageView.setLayoutParams(params);

            presenter.onPresentImage(imageView, item.path, width);

            return imageView;
        }

    }


    @Override
    protected void onDestroy() {
        //AndroidImagePicker.getInstance().deleteOnPictureTakeCompleteListener(this);
        AndroidImagePicker.getInstance().onDestroy();
        super.onDestroy();
    }
}
