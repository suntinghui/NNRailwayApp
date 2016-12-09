package com.lkpower.railway.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.ActivityManager;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.LoginDto;
import com.lkpower.railway.util.NotificationUtil;
import com.lkpower.railway.util.ViewUtil;
import com.umeng.analytics.MobclickAgent;


import java.util.HashMap;

/**
 * Created by sth on 3/23/16.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText phoneEditText = null;
    private EditText pwdEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("作业监控系统");
        this.findViewById(R.id.backBtn).setVisibility(View.GONE);

        phoneEditText = (EditText) this.findViewById(R.id.phoneEditText);
        phoneEditText.setText(Constants.CUSTOMERNO);
        phoneEditText.setSelection(phoneEditText.getText().toString().length());

        pwdEditText = (EditText) this.findViewById(R.id.pwdEditText);

        this.findViewById(R.id.nextBtn).setOnClickListener(this);

        phoneEditText.setText("sdp");
        pwdEditText.setText("aaaaaaa");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextBtn:

                if (checkValue()) {
                    requestLogin();
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private boolean checkValue(){
        if (TextUtils.isEmpty(phoneEditText.getText().toString().trim())) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            ViewUtil.shakeView(pwdEditText);
            return false;

        } else if (TextUtils.isEmpty(pwdEditText.getText().toString().trim())) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            ViewUtil.shakeView(pwdEditText);
            return false;
        }

        return true;
    }

    private void requestLogin() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "LoginUserInfo");
        tempMap.put("loginname", phoneEditText.getText().toString().trim());
        tempMap.put("pwdword", pwdEditText.getText().toString().trim());

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    LoginDto loginDto = gson.fromJson(jsonObject, LoginDto.class);

                    if (loginDto.getResult().getFlag() == 1) {
                        // 友盟账号统计
                        MobclickAgent.onProfileSignIn(loginDto.getDataInfo().getLoginName());

                        Intent intent = new Intent(LoginActivity.this, TrainListActivity.class);
                        intent.putExtra("INFO", loginDto);
                        LoginActivity.this.startActivity(intent);
                        LoginActivity.this.finish();

                    } else {
                        Toast.makeText(LoginActivity.this, loginDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "正在登录...");
    }

    private long exitTimeMillis = 0;
    private void exitApp() {
        if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTimeMillis = System.currentTimeMillis();

        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}
