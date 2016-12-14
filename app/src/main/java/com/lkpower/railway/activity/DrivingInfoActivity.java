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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.king.photo.util.Bimp;
import com.king.photo.util.FileUtils;
import com.king.photo.util.ImageItem;
import com.king.photo.util.PublicWay;
import com.king.photo.util.Res;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.ImageUtil;
import com.lkpower.railway.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.king.photo.activity.BaseActivity.bimap;
import static com.lkpower.railway.util.ImageUtil.martixBitmap;


/**
 * 行车信息
 */
public class DrivingInfoActivity extends BaseActivity implements OnClickListener {

    private View parentView;
    private GridView noScrollgridview;
    private GridAdapter adapter;

    private Button backBtn = null;
    private Button sendBtn = null;
    private EditText remarkEditText = null;

    private static String remarkTemp = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);

        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);

        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_driving_info, null);
        setContentView(parentView);

        backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        sendBtn = (Button) this.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

        remarkEditText = (EditText) this.findViewById(R.id.remarkEditText);
        remarkEditText.setText(remarkTemp);

        noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        //adapter.update();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    photo();

                } else {
                    Intent intent = new Intent(DrivingInfoActivity.this, GalleryActivity.class);
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
                this.backAction();
                break;

            case R.id.sendBtn:
                upload();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        backAction();
    }

    private void backAction(){
        remarkTemp = remarkEditText.getText().toString();

        this.finish();
    }

    private void upload() {
        // 行车信息可以不输入,但是图片必须上传
        if (Bimp.tempSelectBitmap.isEmpty()) {
            Toast.makeText(this, "请拍照上传图片", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("CarNumberId", Constants.CarNumberId);
        jsonMap.put("CarNumberName", Constants.CarNumberName);
        jsonMap.put("GroupId", Constants.DeviceInfo.getID());
        jsonMap.put("GroupName", Constants.DeviceInfo.getUserName());
        jsonMap.put("SubmitTime", DateUtil.getCurrentDateTime());
        jsonMap.put("Remark", remarkEditText.getText().toString().trim());

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
        tempMap.put("commondKey", "UpdateDrivingInfo");
        tempMap.put("jsonData", new GsonBuilder().create().toJson(jsonMap));

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);

                    if (resultMsgDto.getResult().getFlag() == 1) {
                        new SweetAlertDialog(DrivingInfoActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText("提示").setContentText("行车信息提交成功").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                                Bimp.tempSelectBitmap.clear();
                                remarkEditText.setText("");
                                remarkTemp = "";

                                DrivingInfoActivity.this.finish();
                            }
                        }).show();


                    } else {
                        Toast.makeText(DrivingInfoActivity.this, resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "正在上传数据...");

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
                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
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
        File photoFile = new File(Environment.getExternalStorageDirectory() + "/my_camera/0.jpg");
        if (!photoFile.getParentFile().exists()) {
            photoFile.getParentFile().mkdirs();
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(intent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {
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

