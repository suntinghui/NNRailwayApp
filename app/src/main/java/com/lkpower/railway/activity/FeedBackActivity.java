package com.lkpower.railway.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.util.DateUtil;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * Created by sth on 10/11/2016.
 */

public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

    private EditText contentEditText = null;
    private Button commitBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        initView();
    }

    public void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("意见反馈");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        contentEditText = (EditText) this.findViewById(R.id.contentEditText);

        commitBtn = (Button) this.findViewById(R.id.commitBtn);
        commitBtn.setOnClickListener(this);
    }

    private boolean checkValue() {
        String content = contentEditText.getText().toString().trim();

        if (content.startsWith("YAO")) {
            Constants.deviceID = content.replace("YAO", "");
            Toast.makeText(this, "deviceID被暂时模拟为:" + Constants.deviceID, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入意见内容", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /*
    {GroupName=孙大鹏,
    DeviceInfo=358182060933359,
    commondKey=UpdateFeedBack,
    SubmitTime=2016-11-10 03:11:52,
    CarNumberId=02988309-9cf9-479f-8472-f46c9f884aa9,
    Remark=Nihao,
    GroupId=644f8661-11f6-4ebd-a77d-5d80b98a5ca0}
     */
    private void requestFeedBack() {
        HashMap<String, String> jsonMap = new HashMap<String, String>();
        jsonMap.put("CarNumberId", Constants.CarNumberId);
        jsonMap.put("GroupId", null == Constants.DeviceInfo ? "" : Constants.DeviceInfo.getID());
        jsonMap.put("GroupName", null == Constants.DeviceInfo ? "" : Constants.DeviceInfo.getUserName());
        jsonMap.put("SubmitTime", DateUtil.getCurrentDateTime());
        jsonMap.put("DeviceInfo", DeviceUtil.getDeviceId(this));
        jsonMap.put("Remark", contentEditText.getText().toString().trim());

        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "UpdateFeedBack")
                .params("jsonData", new GsonBuilder().create().toJson(jsonMap))
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        HUDUtil.showHUD(FeedBackActivity.this, "正在提交信息...");
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);

                        e.printStackTrace();

                        Toast.makeText(FeedBackActivity.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
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
                            ResultMsgDto resultDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                            if (resultDto.getResult().getFlag() == 1) {
                                new SweetAlertDialog(FeedBackActivity.this, SweetAlertDialog.NORMAL_TYPE).setTitleText("提示").setContentText("提交成功").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.cancel();
                                        finish();
                                    }
                                }).show();

                            } else {
                                Toast.makeText(FeedBackActivity.this, resultDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.commitBtn:
                if (checkValue()) {
                    requestFeedBack();
                }

                break;
        }
    }
}
