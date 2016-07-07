package com.zhc.eid.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;

/**
 * Created by sth on 3/23/16.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText phoneEditText = null;
    private EditText pwdEditText = null;

    private TextView bleTextView = null;

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private VerifyTransferPWDDialog verifyTransferPwdDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_login);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("手机银行");
        this.findViewById(R.id.backBtn).setVisibility(View.GONE);

        phoneEditText = (EditText) this.findViewById(R.id.phoneEditText);
        phoneEditText.setText(Constants.CUSTOMERNO);
        phoneEditText.setSelection(phoneEditText.getText().toString().length());

        pwdEditText = (EditText) this.findViewById(R.id.pwdEditText);
        pwdEditText.setText("888888");

        bleTextView = (TextView) this.findViewById(R.id.bleTextView);
        bleTextView.setOnClickListener(this);

        this.findViewById(R.id.bleTextView).setOnClickListener(this);

        this.findViewById(R.id.nextBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nextBtn:
                if (checkValue()) {
                    requestLogin("");
                }
                break;

            case R.id.bleTextView:
                checkBLEEnvironment();

                bleLogin();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            this.finish();
        }
    }

    private boolean checkValue() {
        if (phoneEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwdEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void requestLogin(String cardid) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("TransCode", "m99904");
        map.put("customerNo", this.phoneEditText.getText().toString().trim());
        map.put("cardId", cardid);
        map.put("handletype", "");
        map.put("username", "");
        map.put("passwdtype", "");
        map.put("passwd", this.pwdEditText.getText().toString().trim());
        map.put("signmessage", "");

        XMLRequest request = new XMLRequest(this, RequestEnum.LOGIN, map, false, new Response.Listener<XmlPullParser>() {

            @Override
            public void onResponse(XmlPullParser parser) {
                parseXML(parser);
            }

        }, new ResponseErrorListener(this));

        NetHelper.getInstance().addToRequestQueue(this, request, "正在登录请稍候...");
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
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivityForResult(intent, 0);

                                LoginActivity.this.finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
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

    private void bleLogin() {
        // checkBLEEnvironment();

        this.showProgress("请使用蓝牙盾进行身份验证");

        mBluetoothAdapter.startLeScan(callback);
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
        public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            // BLECARD_A400D400 A0:04:00:4D:00:4A
            Log.e("----", bluetoothDevice.getName() + "--" + bluetoothDevice.getAddress());

            mBluetoothAdapter.stopLeScan(callback);

            verifyTransferPwdDialog = new VerifyTransferPWDDialog(LoginActivity.this);
            verifyTransferPwdDialog.setTitle("请输入蓝牙盾密码");
            verifyTransferPwdDialog.setTip("已连接蓝牙盾，将进行身份验证");
            verifyTransferPwdDialog.setOnConfirmListener(new VerifyTransferPWDDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String pwdStr) {
                    if ("123456".equalsIgnoreCase(pwdStr)) {

                        verifyTransferPwdDialog.dismiss();


                        requestLogin(bluetoothDevice.getName());

                    } else {
                        verifyTransferPwdDialog.setError("密码错误，请重新输入");
                        verifyTransferPwdDialog.clearInputPwd();
                    }
                }
            });
            verifyTransferPwdDialog.show();
        }

    };
}
