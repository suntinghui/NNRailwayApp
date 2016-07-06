package com.zhc.eid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.EidFindEidActivity;
import com.zhc.eid.R;
import com.zhc.eid.activity.view.VerifyTransferPWDDialog;
import com.zhc.eid.client.Constants;
import com.zhc.eid.nfc.EIDCardReader;
import com.zhc.eid.util.ActivityUtil;
import com.zhc.eid.util.NFCUtil;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by sth on 3/23/16.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("手机银行");
        this.findViewById(R.id.backBtn).setVisibility(View.GONE);

        this.findViewById(R.id.eidTextView).setOnClickListener(this);
        this.findViewById(R.id.nextBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextBtn:
                Intent intent = new Intent(this, MainActivity.class);
                this.startActivity(intent);
                break;

            case R.id.eidTextView:
                eidLogin();
                break;
        }
    }

    private void eidLogin() {
        Intent intent = new Intent(this, EidFindEidActivity.class);
        intent.putExtra("APP_ID", Constants.eID_APP_ID);
        intent.putExtra("APP_KEY", Constants.eID_APP_KEY);
        intent.putExtra("BIZ_SEQ_ID", new Random().nextInt(10000) + "");
        intent.putExtra("SERVICE_ID", "SignVerifyPKI");
        intent.putExtra("HANDLER", "com.zhc.eid.handler.LoginHandler");
        this.startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            this.finish();
        }
    }
}
