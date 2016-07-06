package com.zhc.eid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhc.eid.R;

/**
 * Created by sth on 6/27/16.
 * <p>
 * 余额查询
 */
public class QueryBalanceActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_query_balance);

        ((TextView) this.findViewById(R.id.titleTextView)).setText("余额查询");

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
