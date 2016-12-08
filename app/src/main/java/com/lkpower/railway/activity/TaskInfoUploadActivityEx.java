package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageItem;
import com.king.photo.util.Res;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * 上传任务数据
 */
public class TaskInfoUploadActivityEx extends BaseActivity implements OnClickListener {

    private View parentView;
    private GridView noScrollgridview;
    private GridAdapter adapter;

    private Button backBtn = null;
    private Button sendBtn = null;
    private EditText remarkEditText = null;
    private TextView imgTipTextView = null;
    private Button toggleGestureLockBtn = null;
    private boolean toggleFlag = false;

    private TaskDto.TaskListInfoDto task = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = (TaskDto.TaskListInfoDto) this.getIntent().getSerializableExtra("TASK_INFO");

        Res.init(this);

        Bimp.tempSelectBitmap.clear();
        parentView = getLayoutInflater().inflate(R.layout.activity_taskinfo_upload_ex, null);
        setContentView(parentView);

        backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        sendBtn = (Button) this.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

        remarkEditText = (EditText) this.findViewById(R.id.remarkEditText);

        imgTipTextView = (TextView) this.findViewById(R.id.imgTipTextView);
        boolean imgMustUploadFlag = task.getIsUploadPhoto().equalsIgnoreCase("1");
        imgTipTextView.setText(imgMustUploadFlag ? "请拍照上传图片 (*必选项)" : "请拍照上传图片 (*非必选项)");

        toggleGestureLockBtn = (Button) this.findViewById(R.id.toggleGestureLockBtn);
        toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
        toggleGestureLockBtn.setOnClickListener(this);

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    photo();

                } else {
                    Intent intent = new Intent(TaskInfoUploadActivityEx.this, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

        ActivityUtil.verifyStoragePermissions(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.sendBtn:
                requestUpdateTask();
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
        }
    }


    private void requestUpdateTask() {
        boolean remarkFlag = TextUtils.isEmpty(remarkEditText.getText().toString());
        boolean imgEmptyFlag = Bimp.tempSelectBitmap.isEmpty();
        boolean imgMustUploadFlag = task.getIsUploadPhoto().equalsIgnoreCase("1");

        // 如果要求必须拍照,则检查图片是否为空
        if (imgMustUploadFlag) {
            if (imgEmptyFlag) {
                Toast.makeText(this, "请拍照上传图片", Toast.LENGTH_SHORT).show();
                return;
            }

        } else {
            // 如果没有要求必须拍照,则检查都不为空即可
            if (remarkFlag && imgEmptyFlag) {
                Toast.makeText(this, "文字描述与图片不能同时为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("ID", task.getID());
        jsonMap.put("missionId", task.getMissionId());
        jsonMap.put("executor", task.getExecutor());
        //jsonMap.put("state", toggleFlag ? "4" : "1"); // 1未完成 2已完成
        jsonMap.put("state", "2");
        jsonMap.put("remark", remarkEditText.getText().toString());
        jsonMap.put("updateUser", Constants.DeviceInfo.getUserName());
        jsonMap.put("updateTime", DateUtil.getCurrentDateTime());

        if (!Bimp.tempSelectBitmap.isEmpty()) {
            ArrayList imgList = new ArrayList();
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                HashMap<String, String> imgMap = new HashMap<String, String>();
                imgMap.put("imgData", ImageUtil.bitmapToBase64(Bimp.tempSelectBitmap.get(i).getBitmap()));
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
                        showSuccess();


                    } else {
                        Toast.makeText(TaskInfoUploadActivityEx.this, resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "正在上传数据...");

    }

    private void showSuccess() {
        new SweetAlertDialog(TaskInfoUploadActivityEx.this, SweetAlertDialog.WARNING_TYPE).setTitleText("提示").setContentText("任务信息提交成功").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.cancel();

                TaskInfoUploadActivityEx.this.finish();
            }
        }).show();
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            if (Bimp.tempSelectBitmap.size() == 9) {
                return 9;
            }
            return (Bimp.tempSelectBitmap.size() + 1);
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
                convertView = inflater.inflate(R.layout.item_published_grida, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == Bimp.tempSelectBitmap.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                holder.image.setImageBitmap(Bimp.tempSelectBitmap.get(position).getBitmap());
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.tempSelectBitmap.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            Bimp.max += 1;
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                }
            }).start();
        }
    }

    protected void onRestart() {
        adapter.update();
        super.onRestart();
    }

    private static final int TAKE_PICTURE = 0x000001;

    public void photo() {
        // 这处方法取到的其实只是缩略图
/*
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
*/


        File photoFile = new File(Environment.getExternalStorageDirectory() + "/my_camera/0.jpg");
        if (!photoFile.getParentFile().exists()) {
            photoFile.getParentFile().mkdirs();
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, TAKE_PICTURE);//如果用 RESULT_OK 做requestCode，就不会回调onActivityResult()了
        //这种方法onActivityResult()中不能调用data.getExtra()，否则报错

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
                    /*
                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    String path = FileUtils.saveGetUrl(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setImagePath(path);
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                    */


                    File photoFile = new File(Environment.getExternalStorageDirectory() + "/my_camera/0.jpg");
                    try {
                        Uri uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(getContentResolver(),
                                photoFile.getAbsolutePath(), null, null));

                        Bitmap bm = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                        ImageItem takePhoto = new ImageItem();
                        takePhoto.setImagePath(photoFile.getAbsolutePath());
                        takePhoto.setBitmap(ImageUtil.martixBitmap(bm));
                        Bimp.tempSelectBitmap.add(takePhoto);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

}

