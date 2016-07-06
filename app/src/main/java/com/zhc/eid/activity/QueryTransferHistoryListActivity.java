package com.zhc.eid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhc.eid.R;

/**
 * Created by sth on 6/27/16.
 * <p>
 * 明细查询列表
 *
 */
public class QueryTransferHistoryListActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_query_transfer_history_list);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("明细查询结果");

        this.findViewById(R.id.backBtn).setOnClickListener(this);
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
