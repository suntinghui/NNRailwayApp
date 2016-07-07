package com.zhc.eid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.zhc.eid.R;
import com.zhc.eid.client.Constants;
import com.zhc.eid.client.RequestEnum;
import com.zhc.eid.client.net.NetHelper;
import com.zhc.eid.client.net.ResponseErrorListener;
import com.zhc.eid.client.net.XMLRequest;
import com.zhc.eid.util.StringUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;

/**
 * Created by sth on 6/27/16.
 * <p/>
 * 跨行汇款
 */
public class InterbankTransfer1Activity extends BaseActivity implements View.OnClickListener {

    private EditText amountEditText = null;
    private EditText payeeAccountNameEditText = null; // 转入账户名
    private EditText payeeAccountnbrEditText = null; // 转入账户名

    private float balance = 0.00f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_interbank_transfer_1);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("转账汇款");

        this.findViewById(R.id.backBtn).setOnClickListener(this);

        this.findViewById(R.id.nextBtn).setOnClickListener(this);

        this.amountEditText = (EditText) this.findViewById(R.id.amountEditText);
        this.payeeAccountNameEditText = (EditText) this.findViewById(R.id.payeeAccountNameEditText);
        this.payeeAccountnbrEditText = (EditText) this.findViewById(R.id.payeeAccountnbrEditText);

        requestQueryBalance();
    }

    private void requestQueryBalance() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("TransCode", "m10001");
        map.put("customerNo", Constants.CUSTOMERNO);
        map.put("account", Constants.ACCOUNTNO);
        map.put("currency", "");
        map.put("speculateflag", "");
        map.put("outfit", "");
        map.put("password", "");
        map.put("startcount", "");
        map.put("selectcount", "");

        XMLRequest request = new XMLRequest(this, RequestEnum.QUERYBALANCE, map, false, new Response.Listener<XmlPullParser>() {

            @Override
            public void onResponse(XmlPullParser parser) {
                parseXML(parser);
            }

        }, new ResponseErrorListener(this));

        NetHelper.getInstance().addToRequestQueue(this, request, "正在查询请稍候...");
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

                            } else {
                                Toast.makeText(InterbankTransfer1Activity.this, "查询信息失败", Toast.LENGTH_SHORT).show();

                                return;
                            }
                        } else if ("account".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.accountTextView)).setText(StringUtil.formatCardId(parser.nextText()));

                        } else if ("accountName".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.accountNameTextView)).setText(parser.nextText());

                        } else if ("accountBalance".equalsIgnoreCase(parser.getName())) {
                            String str = parser.nextText();
                            balance = Float.parseFloat(str);
                            ((TextView) this.findViewById(R.id.accountBalanceTextView)).setText(str);

                        }
                        break;
                }
                eventCode = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkValue() {
        if (amountEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "请输入转账金额", Toast.LENGTH_SHORT).show();
            return false;

        } else if (this.payeeAccountNameEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "请输入收款人姓名", Toast.LENGTH_SHORT).show();
            return false;

        } else if (this.payeeAccountnbrEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "请输入收款账号", Toast.LENGTH_SHORT).show();
            return false;

        } else if (Float.parseFloat(amountEditText.getText().toString().trim()) > balance) {
            Toast.makeText(this, "转账金额不能大于余额", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.nextBtn:
                if (checkValue()) {
                    Intent intent = new Intent(this, InterbankTransfer2Activity.class);
                    intent.putExtra("payeeAccountName", this.payeeAccountNameEditText.getText().toString());
                    intent.putExtra("payeeAccountnbr", this.payeeAccountnbrEditText.getText().toString());
                    intent.putExtra("amount", this.amountEditText.getText().toString());
                    this.startActivityForResult(intent, 0);
                }
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
