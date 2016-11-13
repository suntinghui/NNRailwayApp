package com.lkpower.railway.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lkpower.railway.R;
import com.lkpower.railway.dto.MessageModel;

/**
 * Created by sth on 08/11/2016.
 * <p>
 * 段发信息详情
 */

public class MessageDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView titleTextView = null;
    private TextView contentTextView = null;
    private TextView authorTextView = null;
    private TextView timeTextView = null;

    private MessageModel message = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message_detail);

        message = (MessageModel) this.getIntent().getSerializableExtra("message");

        initView();
    }

    public void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("信息详细");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        contentTextView = (TextView) this.findViewById(R.id.contentTextView);
        authorTextView = (TextView) this.findViewById(R.id.authorTextView);
        timeTextView = (TextView) this.findViewById(R.id.timeTextView);

        titleTextView.setText(message.getTitle());
        contentTextView.setText(message.getContent());
        authorTextView.setText(message.getDutyUser());
        timeTextView.setText(message.getSubmitTime().trim());
    }

    @Override
    public void onClick(View view) {
        this.finish();
    }

}
