package com.zhc.eid.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.zhc.eid.R;
import com.zhc.eid.activity.view.VerifyTransferPWDDialog;
import com.zhc.eid.client.Constants;
import com.zhc.eid.client.RequestEnum;
import com.zhc.eid.client.net.NetHelper;
import com.zhc.eid.client.net.ResponseErrorListener;
import com.zhc.eid.client.net.XMLRequest;
import com.zhc.eid.util.ChangeCNNumber;
import com.zhc.eid.util.StringUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;

/**
 * Created by sth on 6/27/16.
 * <p/>
 * 跨行汇款
 */
public class InterbankTransfer2Activity extends BaseActivity implements View.OnClickListener {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private VerifyTransferPWDDialog verifyTransferPwdDialog = null;

    private TextView transferOutTextView = null;
    private TextView transferInTextView = null;
    private TextView transferInNameTextView = null;
    private TextView amountTextView = null;
    private TextView amountCNNTextView = null;

    @Override
    protected void onResume() {
        super.onResume();

        // 如果设备没有开启蓝牙则弹出对话框提示用户开启蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_interbank_transfer_2);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("转账汇款");

        this.findViewById(R.id.backBtn).setOnClickListener(this);

        this.findViewById(R.id.nextBtn).setOnClickListener(this);

        this.transferOutTextView = (TextView) this.findViewById(R.id.transferOutTextView);
        this.transferOutTextView.setText(StringUtil.formatCardId(Constants.ACCOUNTNO));

        this.transferInTextView = (TextView) this.findViewById(R.id.transferInTextView);
        this.transferInTextView.setText(StringUtil.formatCardId(this.getIntent().getStringExtra("payeeAccountnbr")));

        this.transferInNameTextView = (TextView) this.findViewById(R.id.transferInNameTextView);
        this.transferInNameTextView.setText(this.getIntent().getStringExtra("payeeAccountName"));

        this.amountTextView = (TextView) this.findViewById(R.id.amountTextView);
        this.amountTextView.setText(StringUtil.formatAmount(Float.parseFloat(this.getIntent().getStringExtra("amount"))));

        this.amountCNNTextView = (TextView) this.findViewById(R.id.amountCNNTextView);
        this.amountCNNTextView.setText(ChangeCNNumber.changeNumber(this.getIntent().getStringExtra("amount")));
        this.checkBLEEnvironment();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.setResult(RESULT_CANCELED);
                this.finish();
                break;

            case R.id.nextBtn:
                this.showProgress("请使用蓝牙盾进行身份验证");

                mBluetoothAdapter.startLeScan(callback);
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

    private void checkBLEEnvironment() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "该设备不支持蓝牙4.0，无法使用程序", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "该设备不支持蓝牙，无法使用程序", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            // BLECARD_A400D400 A0:04:00:4D:00:4A
            Log.e("----", bluetoothDevice.getName() + "--" + bluetoothDevice.getAddress());

            mBluetoothAdapter.stopLeScan(callback);

            verifyTransferPwdDialog = new VerifyTransferPWDDialog(InterbankTransfer2Activity.this);
            verifyTransferPwdDialog.setTitle("请输入蓝牙盾密码");
            verifyTransferPwdDialog.setTip("已连接蓝牙盾，将进行身份验证");
            verifyTransferPwdDialog.setOnConfirmListener(new VerifyTransferPWDDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String pwdStr) {
                    if ("123456".equalsIgnoreCase(pwdStr)) {

                        verifyTransferPwdDialog.dismiss();

                        InterbankTransfer2Activity.this.showProgress("身份验证通过！");

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                requestQueryHistory();
                            }
                        }, 1000);
                    } else {
                        verifyTransferPwdDialog.setError("密码错误，请重新输入");
                        verifyTransferPwdDialog.clearInputPwd();
                    }
                }
            });
            verifyTransferPwdDialog.show();
        }

    };

    private void requestQueryHistory() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("TransCode", "m30002");
        map.put("custnbr", Constants.CUSTOMERNO);
        map.put("payerAccountnbr", transferOutTextView.getText().toString().replace(" ",""));
        map.put("payeraccountName", Constants.ACCOUNTNAME);
        map.put("payeeaccountnbr", transferInTextView.getText().toString().replace(" ",""));
        map.put("payeeaccountname", transferInNameTextView.getText().toString());
        map.put("transferamt", amountTextView.getText().toString());
        map.put("fee", "0.00");


        XMLRequest request = new XMLRequest(this, RequestEnum.INTERBANKTRANSFER, map, false, new Response.Listener<XmlPullParser>() {

            @Override
            public void onResponse(XmlPullParser parser) {
                InterbankTransfer2Activity.this.hideProgress();

                parseXML(parser);
            }

        }, new ResponseErrorListener(this));

        NetHelper.getInstance().addToRequestQueue(this, request, "正在交易请稍候...");
    }

    private void parseXML(XmlPullParser parser) {

        try {
            int eventCode = parser.getEventType();
            while (eventCode != XmlPullParser.END_DOCUMENT) {
                switch (eventCode) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        if ("RetCode".equalsIgnoreCase(parser.getName())) {
                            String resultCode = parser.nextText();
                            if (resultCode.equalsIgnoreCase("0000")) {
                                Intent intent = new Intent(InterbankTransfer2Activity.this, InterbankTransfer3Activity.class);
                                intent.putExtra("transferOut", transferOutTextView.getText().toString());
                                intent.putExtra("transferIn", transferInTextView.getText().toString());
                                intent.putExtra("transferInName", transferInNameTextView.getText().toString());
                                intent.putExtra("amount", amountTextView.getText().toString());
                                InterbankTransfer2Activity.this.startActivityForResult(intent, 0);


                                InterbankTransfer2Activity.this.finish();

                            } else {
                                Toast.makeText(InterbankTransfer2Activity.this, "交易失败", Toast.LENGTH_SHORT).show();
                            }

                        } else if ("customerNo".equalsIgnoreCase(parser.getName())) {
                            Constants.CUSTOMERNO = parser.nextText();

                        } else if ("account".equalsIgnoreCase(parser.getName())) {
                            Constants.ACCOUNTNO = parser.nextText();

                        } else if ("accountName".equalsIgnoreCase(parser.getName())) {
                            Constants.ACCOUNTNAME = parser.nextText();
                        }

                        break;
                }
                eventCode = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
