package com.zhc.eid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhc.eid.R;

/**
 * Created by sth on 6/27/16.
 * <p>
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;

            case R.id.nextBtn:
                Intent intent = new Intent(this, QueryTransferHistoryListActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
