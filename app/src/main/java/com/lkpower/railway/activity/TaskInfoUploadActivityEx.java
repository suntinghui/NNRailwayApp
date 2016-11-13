package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.Toast;

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
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.TaskDto;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static com.lkpower.railway.R.id.toggleGestureLockBtn;


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

    private Button toggleGestureLockBtn = null;
    private boolean toggleFlag = false;

    private TaskDto.TaskListInfoDto task = null;

    public static Bitmap bimap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = (TaskDto.TaskListInfoDto) this.getIntent().getSerializableExtra("TASK");

        Res.init(this);

        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);

        Bimp.tempSelectBitmap.clear();

        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_taskinfo_upload_ex, null);
        setContentView(parentView);

        backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        sendBtn = (Button) this.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

        remarkEditText = (EditText) this.findViewById(R.id.remarkEditText);

        toggleGestureLockBtn = (Button) this.findViewById(R.id.toggleGestureLockBtn);
        toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
        toggleGestureLockBtn.setOnClickListener(this);

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
                    Intent intent = new Intent(TaskInfoUploadActivityEx.this, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });

        noScrollgridview.setVisibility(task.getIsUploadPhoto().equalsIgnoreCase("1")?View.VISIBLE:View.GONE);
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
        if (TextUtils.isEmpty(remarkEditText.getText().toString())) {
            Toast.makeText(this, "请输入任务描述", Toast.LENGTH_SHORT).show();
            return;
        }

        if (task.getIsUploadPhoto().equalsIgnoreCase("1") && Bimp.tempSelectBitmap.isEmpty()) {
            Toast.makeText(this, "请拍照上传图片", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("ID", task.getID());
        jsonMap.put("missionId", task.getMissionId());
        jsonMap.put("executor", task.getExecutor());
        jsonMap.put("state", toggleFlag ? "4" : "1");
        jsonMap.put("remark", remarkEditText.getText().toString());
        jsonMap.put("updateUser", Constants.DeviceInfo.getUserName());
        jsonMap.put("updateTime", DateUtil.getCurrentDateTime());

        if (!Bimp.tempSelectBitmap.isEmpty()) {
            ArrayList imgList = new ArrayList();
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                HashMap<String, String> imgMap = new HashMap<String, String>();
                imgMap.put("imgData", StringUtil.Image2Base64(Bimp.tempSelectBitmap.get(i).getImagePath()));
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
                        Toast.makeText(TaskInfoUploadActivityEx.this, "数据提交成功", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(TaskInfoUploadActivityEx.this, resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在上传数据...");

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
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    String path = FileUtils.saveGetUrl(bm, fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setImagePath(path);
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
        }
    }

}

