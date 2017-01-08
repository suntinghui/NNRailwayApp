package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.lkpower.railway.util.FileUtil;
import com.lkpower.railway.util.ImageUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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

    private static String remarkTemp = "";

    private String localTempImgFileName = null;
    private ArrayList<String> tempImgList = new ArrayList<String>();

    private TaskDto.TaskListInfoDto task = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = (TaskDto.TaskListInfoDto) this.getIntent().getSerializableExtra("TASK_INFO");

        refreshImageList();

        Res.init(this);

        parentView = getLayoutInflater().inflate(R.layout.activity_taskinfo_upload_ex, null);
        setContentView(parentView);

        backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        sendBtn = (Button) this.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

        remarkEditText = (EditText) this.findViewById(R.id.remarkEditText);
        remarkEditText.setText(remarkTemp);

        imgTipTextView = (TextView) this.findViewById(R.id.imgTipTextView);
        boolean imgMustUploadFlag = task.getIsUploadPhoto().equalsIgnoreCase("1");
        imgTipTextView.setText(imgMustUploadFlag ? "请拍照上传图片 (*必选项)" : "请拍照上传图片 (*非必选项)");

        toggleGestureLockBtn = (Button) this.findViewById(R.id.toggleGestureLockBtn);
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

        toggleFlag = false;
        sendBtn.setVisibility(View.GONE);
        toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);

        ActivityUtil.verifyStoragePermissions(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                backAction();
                break;

            case R.id.sendBtn: {
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

                new SweetAlertDialog(TaskInfoUploadActivityEx.this, SweetAlertDialog.WARNING_TYPE).setTitleText("提示").setContentText("已经标记为完成状态, 提交后该任务将不能再修改").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();

                        requestUpdateTask();

                    }
                }).setCancelText("取消").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {

                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                }).show();
            }
            break;

            case R.id.toggleGestureLockBtn:
                if (toggleFlag) {
                    // 如果设置了手势密码，则去关闭手势密码
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
                    //Toast.makeText(this, "已经标记为未完成", Toast.LENGTH_SHORT).show();

                    sendBtn.setVisibility(View.GONE);

                } else {
                    // 如果没有设置，则切换图片，并去设置
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_on);
                    //Toast.makeText(this, "已经标记为完成", Toast.LENGTH_SHORT).show();

                    sendBtn.setVisibility(View.VISIBLE);
                }

                toggleFlag = !toggleFlag;

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        backAction();
    }

    private void backAction() {
        remarkTemp = remarkEditText.getText().toString();

        this.finish();
    }

    private void refreshImageList() {
        Bimp.tempSelectBitmap.clear();
        tempImgList.clear();

        HashSet<String> set = (HashSet<String>) ActivityUtil.getSharedPreferences().getStringSet(task.getID(), new HashSet<String>());
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            tempImgList.add(name);
            ImageItem imageItem = new ImageItem();
            imageItem.setImageId(name);
            Bimp.tempSelectBitmap.add(imageItem);
        }
    }

    private void requestUpdateTask() {
        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("ID", task.getID());
        jsonMap.put("missionId", task.getMissionId());
        jsonMap.put("executor", task.getExecutor());
        jsonMap.put("state", toggleFlag ? "2" : "1"); // 1未完成 2已完成
        jsonMap.put("remark", remarkEditText.getText().toString());
        jsonMap.put("updateUser", null == Constants.DeviceInfo ? "" : Constants.DeviceInfo.getUserName());
        jsonMap.put("updateTime", DateUtil.getCurrentDateTime());

        StringBuffer sb = new StringBuffer(new GsonBuilder().create().toJson(jsonMap));

        if (!Bimp.tempSelectBitmap.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
            sb.append(",\"ImgInfo\":[");

            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                Bitmap bitmap = ImageUtil.decodeSampledBitmapFromResource(FileUtil.getFilePath() + Bimp.tempSelectBitmap.get(i).getImageId() + ".jpg", 480, 320);

                sb.append("{\"imgData\":\"");
                sb.append(ImageUtil.bitmapToBase64(bitmap));
                sb.append("\"}");

                if (i != Bimp.tempSelectBitmap.size() - 1) {
                    sb.append(",");
                }

                // 先判断是否已经回收
                if (bitmap != null && !bitmap.isRecycled()) {
                    // 回收并且置为null
                    bitmap.recycle();
                    bitmap = null;
                }
            }

            sb.append("]}");
        }


        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "UpdateMissionInfo");
        tempMap.put("jsonData", sb.toString());

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);

                    if (resultMsgDto.getResult().getFlag() == 1) {
                        Bimp.tempSelectBitmap.clear();
                        remarkEditText.setText("");
                        remarkTemp = "";


                        for (String name : tempImgList) {
                            FileUtil.deleteFile("", name + ".jpg");
                        }

                        SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
                        editor.remove(task.getID());
                        editor.commit();

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
                Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getFilePath() + tempImgList.get(position) + ".jpg", null);
                holder.image.setImageBitmap(bitmap);
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
        /*
        // 这处方法取到的其实只是缩略图
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
        */

        localTempImgFileName = System.currentTimeMillis() + "";

        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileUtil.getFilePath() + localTempImgFileName + ".jpg")));
            startActivityForResult(intent, TAKE_PICTURE);

        } catch (Exception e) {
            e.printStackTrace();

            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("提示").setContentText("非常抱歉,应用程序无法调用手机拍照功能,建议您重置手机或换一个手机使用。").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.cancel();

                }
            }).show();
        }

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


                    File photoFile = new File(FileUtil.getFilePath() + localTempImgFileName + ".jpg");
                    Bitmap bitmap = ImageUtil.decodeSampledBitmapFromResource(photoFile.getAbsolutePath(), 512, 384);
                    FileUtil.saveBitmap(bitmap);

                    SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
                    HashSet<String> set = new HashSet<String>(ActivityUtil.getSharedPreferences().getStringSet(task.getID(), new HashSet<String>()));
                    set.add(localTempImgFileName);
                    editor.putStringSet(task.getID(), set);
                    editor.commit();

                    refreshImageList();

                    // 先判断是否已经回收
                    if (bitmap != null && !bitmap.isRecycled()) {
                        // 回收并且置为null
                        bitmap.recycle();
                        bitmap = null;
                    }
                    System.gc();

                }
                break;
        }
    }

}

