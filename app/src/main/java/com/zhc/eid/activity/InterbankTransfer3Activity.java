package com.zhc.eid.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhc.eid.R;
import com.zhc.eid.util.ChangeCNNumber;

/**
 * Created by sth on 6/27/16.
 * <p/>
 * 跨行汇款
 */
public class InterbankTransfer3Activity extends BaseActivity implements View.OnClickListener {

    private TextView transferOutTextVeiw = null;
    private TextView transferInTextView = null;
    private TextView transferInNameTextView = null;
    private TextView amountTextView = null;
    private TextView amountCNNTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_interbank_transfer_3);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("转账结果");

        this.findViewById(R.id.backBtn).setOnClickListener(this);

        this.transferOutTextVeiw = (TextView) this.findViewById(R.id.transferOutTextView);
        this.transferOutTextVeiw.setText(this.getIntent().getStringExtra("transferOut"));

        this.transferInTextView = (TextView) this.findViewById(R.id.transferInTextView);
        this.transferInTextView.setText(this.getIntent().getStringExtra("transferIn"));

        this.transferInNameTextView = (TextView) this.findViewById(R.id.transferInNameTextView);
        this.transferInNameTextView.setText(this.getIntent().getStringExtra("transferInName"));

        this.amountTextView = (TextView) this.findViewById(R.id.amountTextView);
        this.amountTextView.setText(this.getIntent().getStringExtra("amount"));

        this.amountCNNTextView = (TextView) this.findViewById(R.id.amountCNNTextView);
        this.amountCNNTextView.setText(ChangeCNNumber.changeNumber(this.getIntent().getStringExtra("amount")));
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
