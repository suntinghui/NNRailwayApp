package com.zhc.eid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhc.eid.R;
import com.zhc.eid.client.Constants;
import com.zhc.eid.model.TransferHistory;
import com.zhc.eid.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by sth on 6/27/16.
 * <p/>
 * 明细查询列表
 */
public class QueryTransferHistoryListActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<TransferHistory> list = null;

    private TextView history11TextView = null;
    private TextView history12TextView = null;
    private TextView history13TextView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_query_transfer_history_list);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("明细查询结果");

        list = (ArrayList<TransferHistory>) this.getIntent().getSerializableExtra("LIST");

        this.findViewById(R.id.backBtn).setOnClickListener(this);

        ((TextView) this.findViewById(R.id.accountNoTextView)).setText(StringUtil.formatCardId(Constants.ACCOUNTNO));
        ((TextView) this.findViewById(R.id.accountNameTextView)).setText(Constants.ACCOUNTNAME);

        this.history11TextView = (TextView) this.findViewById(R.id.history11TextView);
        this.history12TextView = (TextView) this.findViewById(R.id.history12TextView);
        this.history13TextView = (TextView) this.findViewById(R.id.history13TextView);

        history11TextView.setText(list.get(0).getDate() + "               " + list.get(0).getType());
        history12TextView.setText("转出                                      " + StringUtil.formatAmount(Float.parseFloat(list.get(0).getTransferamt())));
        history13TextView.setText("余额                                      " + list.get(0).getBalance());
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
}
