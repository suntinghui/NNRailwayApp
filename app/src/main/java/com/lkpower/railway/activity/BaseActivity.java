package com.lkpower.railway.activity;

import android.app.Activity;
import android.os.Bundle;

import com.lkpower.railway.MyApplication;
import com.lkpower.railway.client.ActivityManager;
import com.lkpower.railway.client.net.NetworkHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public class BaseActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

        MyApplication.getInstance().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetworkHelper.getInstance().cancelRequest();

        NetworkHelper.getInstance().hideProgress();
    }

}
