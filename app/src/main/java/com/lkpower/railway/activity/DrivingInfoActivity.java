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
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.king.photo.util.Bimp;
import com.king.photo.util.ImageItem;
import com.king.photo.util.Res;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.FileUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lkpower.railway.util.ImageFactory;
import com.lkpower.railway.util.ImageUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.lzy.okgo.request.PostRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

import static com.king.photo.activity.BaseActivity.bimap;


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

    private String localTempImgFileName = null;
    private ArrayList<String> tempImgList = new ArrayList<String>();

    private static String TAG = "DRIVINGINFO";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);

        refreshImageList();

        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.icon_addpic_unfocused);

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

    private void backAction() {
        remarkTemp = remarkEditText.getText().toString();

        this.finish();
    }

    private void refreshImageList() {
        Bimp.tempSelectBitmap.clear();
        tempImgList.clear();

        HashSet<String> set = (HashSet<String>) ActivityUtil.getSharedPreferences().getStringSet(TAG, new HashSet<String>());
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            tempImgList.add(name);
            ImageItem imageItem = new ImageItem();
            imageItem.setImageId(name);
            Bimp.tempSelectBitmap.add(imageItem);
        }
    }

    private void upload() {
        // 行车信息可以不输入,但是图片必须上传
        if (Bimp.tempSelectBitmap.isEmpty()) {
            Toast.makeText(this, "请拍照上传图片", Toast.LENGTH_SHORT).show();
            return;
        }

        PostRequest request = OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params("commondKey", "UpdateDrivingInfoExt")
                .params("CarNumberId", Constants.CarNumberId)
                .params("CarNumberName", Constants.CarNumberName)
                .params("GroupId", null == Constants.DeviceInfo ? "" : Constants.DeviceInfo.getID())
                .params("GroupName", null == Constants.DeviceInfo ? "" : Constants.DeviceInfo.getUserName())
                .params("SubmitTime", DateUtil.getCurrentDateTime())
                .params("Remark", remarkEditText.getText().toString().trim());

        if (!Bimp.tempSelectBitmap.isEmpty()) {
            ArrayList<File> fileList = new ArrayList<File>();
            for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
                fileList.add(new File(FileUtil.getFilePath() + Bimp.tempSelectBitmap.get(i).getImageId() + ".jpg"));
            }

            request.addFileParams("ImgInfo", fileList);
        }

        request.execute(new StringCallback() {

            @Override
            public void onBefore(BaseRequest request) {
                HUDUtil.showHUD(DrivingInfoActivity.this, "正在上传数据...");
            }

            @Override
            public void onAfter(String s, Exception e) {
                HUDUtil.dismiss();
            }

            @Override
            public void onError(Call call, okhttp3.Response response, Exception e) {
                super.onError(call, response, e);

                e.printStackTrace();

                Toast.makeText(DrivingInfoActivity.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String jsonObject, Call call, okhttp3.Response response) {
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

                                for (String name : tempImgList) {
                                    FileUtil.deleteFile("", name + ".jpg");
                                }

                                SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
                                editor.remove(TAG);
                                editor.commit();

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
                holder.image.setImageBitmap(ImageFactory.ratio(FileUtil.getFilePath() + tempImgList.get(position) + ".jpg", 96, 54));
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
                    File photoFile = new File(FileUtil.getFilePath() + localTempImgFileName + ".jpg");
                    Bitmap bitmap = ImageUtil.decodeSampledBitmapFromResource(photoFile.getAbsolutePath(), 480, 270);
                    FileUtil.saveBitmap(bitmap, photoFile.getAbsolutePath());

                    SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
                    HashSet<String> set = new HashSet<String>(ActivityUtil.getSharedPreferences().getStringSet(TAG, new HashSet<String>()));
                    set.add(localTempImgFileName);
                    editor.putStringSet(TAG, set);
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

