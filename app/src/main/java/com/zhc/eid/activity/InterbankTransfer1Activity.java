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
public class InterbankTransfer1Activity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_interbank_transfer_1);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("转账汇款");

        this.findViewById(R.id.backBtn).setOnClickListener(this);

        this.findViewById(R.id.nextBtn).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.nextBtn:
                Intent intent = new Intent(this, InterbankTransfer2Activity.class);
                this.startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            this.setResult(RESULT_OK);
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
