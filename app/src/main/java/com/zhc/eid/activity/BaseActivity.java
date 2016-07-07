package com.zhc.eid.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zhc.eid.MyApplication;
import com.zhc.eid.activity.view.NetErrorDialog;
import com.zhc.eid.activity.view.ProgressHUD;
import com.zhc.eid.client.ActivityManager;
import com.zhc.eid.client.net.NetHelper;
import com.zhc.eid.util.NetUtil;

public class BaseActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU
//		PushAgent.getInstance(this).onAppStart();

        ActivityManager.getInstance().pushActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setCurrentActivity(this);

//		MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

//		MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        NetHelper.getInstance().cancelRequest();

        ActivityManager.getInstance().popActivity();

        // TODO 如何解决与手势的冲突问题？？？？？

        // 下面的代码是为推送准备的。如果在未启动应用的情况下通过推送打开了某一界面，希望关掉界面后能再打开应用。
        // if (!(this instanceof LoginActivity)) {
        // if (!constantsMainActivity()) {
        // this.startActivity(new Intent(this, MainActivity.class));
        // }
        // }

    }

    private boolean constantsMainActivity() {
        for (Activity act : ActivityManager.getInstance().getAllActivity()) {
            if (act instanceof MainActivity) {
                return true;
            }
        }

        return false;
    }

    // 等待提示框
    private ProgressHUD hud = null;

    public void showProgress(String message) {
        try{
            if (message == null)
                return;

            if ("".equals(message.trim()))
                message = "请稍候...";

            if (hud == null) {
                hud = ProgressHUD.show(this, message, true, new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO
                        //cancelRequest();

                        dialog.dismiss();
                    }
                });
            } else {
                hud.setMessage(message);
            }

            if (!hud.isShowing())
                hud.show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideProgress() {
        try {
            hud.dismiss();
            hud = null;
        } catch (Exception e) {
        }
    }

}
