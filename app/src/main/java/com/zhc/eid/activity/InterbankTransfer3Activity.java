package com.zhc.eid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhc.eid.R;

/**
 * Created by sth on 6/27/16.
 * <p>
 * 跨行汇款
 */
public class InterbankTransfer3Activity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_interbank_transfer_3);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("转账结果");

        this.findViewById(R.id.backBtn).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.setResult(RESULT_OK);
                this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_OK);
        this.finish();
    }
}
