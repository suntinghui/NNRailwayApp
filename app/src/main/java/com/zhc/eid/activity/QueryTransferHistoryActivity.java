package com.zhc.eid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.internal.widget.ActivityChooserModel;
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
import com.zhc.eid.model.TransferHistory;
import com.zhc.eid.util.StringUtil;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sth on 6/27/16.
 * <p/>
 * 明细查询
 */
public class QueryTransferHistoryActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_query_transfer_history);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("明细查询");

        this.findViewById(R.id.backBtn).setOnClickListener(this);
        this.findViewById(R.id.nextBtn).setOnClickListener(this);

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
                                Toast.makeText(QueryTransferHistoryActivity.this, "查询信息失败", Toast.LENGTH_SHORT).show();

                                return;
                            }
                        } else if ("account".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.accountTextView)).setText(StringUtil.formatCardId(parser.nextText()));

                        } else if ("accountName".equalsIgnoreCase(parser.getName())) {
                            ((TextView) this.findViewById(R.id.accountNameTextView)).setText(parser.nextText());

                        }
                        break;
                }
                eventCode = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.nextBtn:
                requestQueryHistory();
                break;
        }
    }

    private void requestQueryHistory() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("TransCode", "m10002");
        map.put("custnbr", Constants.CUSTOMERNO);
        map.put("accountnbr", Constants.ACCOUNTNO);

        XMLRequest request = new XMLRequest(this, RequestEnum.QUERYHISTORY, map, false, new Response.Listener<XmlPullParser>() {

            @Override
            public void onResponse(XmlPullParser parser) {
                parseXMLHistory(parser);
            }

        }, new ResponseErrorListener(this));

        NetHelper.getInstance().addToRequestQueue(this, request, "正在查询请稍候...");
    }

    private void parseXMLHistory(XmlPullParser parser) {
        ArrayList<TransferHistory> list = new ArrayList<TransferHistory>();

        TransferHistory history = null;

        try {
            int eventCode = parser.getEventType();
            while (eventCode != XmlPullParser.END_DOCUMENT) {
                switch (eventCode) {

                    case XmlPullParser.START_TAG:
                        if ("RetCode".equalsIgnoreCase(parser.getName())) {
                            String resultCode = parser.nextText();
                            if (resultCode.equalsIgnoreCase("0000")) {

                            } else {
                                Toast.makeText(QueryTransferHistoryActivity.this, "查询信息失败", Toast.LENGTH_SHORT).show();

                                return;
                            }
                        } else if ("rd".equalsIgnoreCase(parser.getName())) {
                            history = new TransferHistory();
                        } else if ("credencetype".equalsIgnoreCase(parser.getName())) {
                            history.setType("跨行汇款");
                        } else if ("transferdate".equalsIgnoreCase(parser.getName())) {
                            history.setDate(parser.nextText());
                        } else if ("transferamt".equalsIgnoreCase(parser.getName())) {
                            history.setTransferamt(parser.nextText());
                        } else if ("debitloanindicator".equalsIgnoreCase(parser.getName())) {
                            history.setDebitloanindicator(parser.nextText());
                        } else if ("bal".equalsIgnoreCase(parser.getName())) {
                            history.setBalance(parser.nextText());
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("rd".equalsIgnoreCase(parser.getName())) {
                            list.add(history);

                        } else if ("out".equalsIgnoreCase(parser.getName())) {
                            Intent intent = new Intent(QueryTransferHistoryActivity.this, QueryTransferHistoryListActivity.class);
                            intent.putExtra("LIST", list);
                            QueryTransferHistoryActivity.this.startActivity(intent);
                        }
                        break;
                }
                eventCode = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
