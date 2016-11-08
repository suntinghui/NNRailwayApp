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
import android.widget.CheckBox;
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
import com.lkpower.railway.dto.LoginDto;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationDto;
import com.lkpower.railway.dto.TaskDetailDto;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.dto.TrainDto;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.StringUtil;
import com.pizidea.imagepicker.AndroidImagePicker;
import com.pizidea.imagepicker.ImgLoader;
import com.pizidea.imagepicker.UilImgLoader;
import com.pizidea.imagepicker.Util;
import com.pizidea.imagepicker.bean.ImageItem;
import com.pizidea.imagepicker.ui.activity.ImagesGridActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by sth on 19/10/2016.
 */

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
    private StationDto stationDto = null;
    private TaskDto.TaskListInfoDto taskInfo = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task_info_upload);

        loginInfo = (LoginDto) this.getIntent().getSerializableExtra("LOGIN_INFO");
        trainInfo = (TrainDto.TrainDataInfo) this.getIntent().getSerializableExtra("TRAIN_INFO");
        stationDto = (StationDto) this.getIntent().getSerializableExtra("STATION_INFO");
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

        verifyStoragePermissions(this);
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

        this.addToRequestQueue(request, "正在上传数据...");
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

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     *
     * 我用的测试机是华为P9，运行android6.0，
     遇到的问题是这样的：在调用图库获取图片时，返回资源总是为null，也无法向SD卡存入照片，并且我已经在清单文件中配置了读写外部内存的权限
     最后成功解决的方法是：在当前Activity中添加以下代码，代码的作用是检查是否已经获取到所需要的权限，如果没有则再次请求权限


     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
