package com.lkpower.railway.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by sth on 19/10/2016.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout runInfoLayout = null; // 行车信息
    private LinearLayout messageLayout = null; // 段发信息
    private LinearLayout checkUpdateLayout = null; // 检查新版本
    private LinearLayout feedbackLayout = null;
    private LinearLayout contactLayout = null;
    private LinearLayout aboutLayout = null;
    private Button logoutBtn = null;

    private Button toggleGestureLockBtn = null;
    private TextView tipModelTextView = null;
    private boolean toggleFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        initView();
    }

    private void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("功能");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        runInfoLayout = (LinearLayout) this.findViewById(R.id.runInfoLayout);
        runInfoLayout.setOnClickListener(this);

        messageLayout = (LinearLayout) this.findViewById(R.id.messageLayout);
        messageLayout.setOnClickListener(this);

        checkUpdateLayout = (LinearLayout) this.findViewById(R.id.checkUpdateLayout);
        checkUpdateLayout.setOnClickListener(this);

        feedbackLayout = (LinearLayout) this.findViewById(R.id.feedbackLayout);
        feedbackLayout.setOnClickListener(this);

        contactLayout = (LinearLayout) this.findViewById(R.id.contactLayout);
        contactLayout.setOnClickListener(this);

        aboutLayout = (LinearLayout) this.findViewById(R.id.aboutLayout);
        aboutLayout.setOnClickListener(this);

        logoutBtn = (Button) this.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);

        toggleGestureLockBtn = (Button) this.findViewById(R.id.toggleGestureLockBtn);
        toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
        toggleGestureLockBtn.setOnClickListener(this);

        tipModelTextView = (TextView) this.findViewById(R.id.tipModelTextView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.runInfoLayout: {// 行车信息
                Intent intent = new Intent(this, DrivingInfoActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.messageLayout: {// 段发信息
                Intent intent = new Intent(this, MessageListActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.checkUpdateLayout: {
                new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE).setTitleText("\n当前已是最新版本\n").setContentText(null).setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).show();
            }
            break;

            case R.id.feedbackLayout: {
                Intent intent = new Intent(this, FeedBackActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.contactLayout: {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constants.DeviceInfo.getPhone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;

            case R.id.aboutLayout: {
                Intent intent = new Intent(this, ShowWebViewActivity.class);
                intent.putExtra("title", "南宁铁路局");
                intent.putExtra("url", "http://www.nntlj.com/staticpages/20140721/nt53cc744b-2703110.shtml");
                this.startActivity(intent);
            }
            break;

            case R.id.logoutBtn:
                break;

            case R.id.toggleGestureLockBtn: {
                if (toggleFlag) {
                    // 如果设置了手势密码，则去关闭手势密码
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_off);
                    tipModelTextView.setText("时间预警");
                    Toast.makeText(this, "列车晚点状态取消,切换到时间预警模式", Toast.LENGTH_SHORT).show();

                } else {
                    // 如果没有设置，则切换图片，并去设置
                    toggleGestureLockBtn.setBackgroundResource(R.drawable.btn_toggle_on);
                    tipModelTextView.setText("位置预警");
                    Toast.makeText(this, "列车被标记为晚点,切换到GPS地理预警模式", Toast.LENGTH_SHORT).show();
                }

                toggleFlag = !toggleFlag;
            }

            break;
        }
    }
}
