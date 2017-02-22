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
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.dto.StationModel;
import com.lkpower.railway.dto.TaskDto;
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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Response;


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
                if (arg2 == tempImgList.size()) {
                    photo();

                } else {
                    Intent intent = new Intent(TaskInfoUploadActivityEx.this, ShowLocalImageActivity.class);
                    intent.putExtra("name", tempImgList.get(arg2));
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
                boolean imgEmptyFlag = tempImgList.isEmpty();
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
        tempImgList.clear();

        HashSet<String> set = (HashSet<String>) ActivityUtil.getSharedPreferences().getStringSet(task.getID(), new HashSet<String>());
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            tempImgList.add(name);
        }
    }

    private void requestUpdateTask() {
        PostRequest request = OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .isMultipart(true)       // 强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                .params("commondKey", "UpdateMissionInfoExt")
                .params("ID", task.getID())
                .params("missionId", task.getMissionId())
                .params("executor", task.getExecutor())
                .params("state", toggleFlag ? "4" : "1") // 1未完成 4已完成
                .params("remark", remarkEditText.getText().toString())
                .params("updateUser", null == Constants.DeviceInfo ? "" : Constants.DeviceInfo.getUserName())
                .params("serialNumber", DateUtil.getCurrentDate())
                .params("updateTime", DateUtil.getCurrentDateTime());

        if (!tempImgList.isEmpty()) {
            ArrayList<File> fileList = new ArrayList<File>();
            for (int i = 0; i < tempImgList.size(); i++) {
                fileList.add(new File(FileUtil.getFilePath() + tempImgList.get(i) + ".jpg"));
            }

            request.addFileParams("ImgInfo", fileList);
        }

        request.execute(new StringCallback() {

            @Override
            public void onBefore(BaseRequest request) {
                HUDUtil.showHUD(TaskInfoUploadActivityEx.this, "正在上传数据...");
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);

                Toast.makeText(TaskInfoUploadActivityEx.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onAfter(String s, Exception e) {
                HUDUtil.dismiss();
            }

            @Override
            public void onSuccess(String jsonObject, Call call, okhttp3.Response response) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);

                    if (resultMsgDto.getResult().getFlag() == 1) {
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

        public int getCount() {
            if (tempImgList.size() == 9) {
                return 9;
            }
            return (tempImgList.size() + 1);
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

            if (position == tempImgList.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon_addpic_unfocused));
                if (position == 9) {
                    holder.image.setVisibility(View.GONE);
                }
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一
                Bitmap bitmap = BitmapFactory.decodeFile(FileUtil.getFilePath() + tempImgList.get(position) + ".jpg", options);
                holder.image.setImageBitmap(bitmap);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }
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
                if (tempImgList.size() < 9 && resultCode == RESULT_OK) {
                    String path = FileUtil.getFilePath() + localTempImgFileName + ".jpg";
                    Bitmap bitmap = ImageFactory.ratio(path, 800, 480);
                    FileUtil.saveBitmap(bitmap, path);

                    SharedPreferences.Editor editor = ActivityUtil.getSharedPreferences().edit();
                    HashSet<String> set = new HashSet<String>(ActivityUtil.getSharedPreferences().getStringSet(task.getID(), new HashSet<String>()));
                    set.add(localTempImgFileName);
                    editor.putStringSet(task.getID(), set);
                    editor.commit();

                    refreshImageList();

                    adapter.notifyDataSetChanged();

                    // 先判断是否已经回收
                    if (bitmap != null && !bitmap.isRecycled()) {
                        // 回收并且置为null
                        bitmap.recycle();
                        bitmap = null;
                    }

                }
                break;
        }
    }

}

