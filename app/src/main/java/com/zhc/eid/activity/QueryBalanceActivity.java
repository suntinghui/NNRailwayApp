package com.zhc.eid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
 * 余额查询
 */
public class QueryBalanceActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_query_balance);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("余额查询");

        this.findViewById(R.id.backBtn).setOnClickListener(this);

        requestQueryBalance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
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
                                Toast.makeText(QueryBalanceActivity.this, "查询余额失败", Toast.LENGTH_SHORT).show();

                                return;
                            }
                        } else if ("account".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.accountTextView)).setText(StringUtil.formatCardId(parser.nextText()));

                        } else if ("accountName".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.accountNameTextView)).setText(parser.nextText());

                        } else if ("outfitname".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.outfitnameTextView)).setText(parser.nextText());

                        } else if ("accountBalance".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.accountBalanceTextView)).setText(StringUtil.formatAmount(Float.parseFloat(parser.nextText())));
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
