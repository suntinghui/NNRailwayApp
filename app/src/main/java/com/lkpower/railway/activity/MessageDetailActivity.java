package com.lkpower.railway.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.dto.InfoPublishListDto;
import com.lkpower.railway.dto.MessageModel;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.util.DeviceUtil;

import java.util.HashMap;

import static com.king.photo.activity.ShowAllPhoto.dataList;

/**
 * Created by sth on 08/11/2016.
 * <p>
 * 段发信息详情
 */

public class MessageDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView nameTextView = null;
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

        requestReaded();
    }

    public void initView() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("信息详情");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        nameTextView = (TextView) this.findViewById(R.id.nameTextView);
        contentTextView = (TextView) this.findViewById(R.id.contentTextView);
        authorTextView = (TextView) this.findViewById(R.id.authorTextView);
        timeTextView = (TextView) this.findViewById(R.id.timeTextView);
    }

    private void refreshView(){
        nameTextView.setText(message.getTitle());
        contentTextView.setText(message.getContent());
        authorTextView.setText(message.getDutyUser());
        timeTextView.setText(message.getSubmitTime().trim());
    }

    private void requestReaded(){
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "UpdateInfoPublish");
        tempMap.put("DeviceId", DeviceUtil.getDeviceId(this));
        tempMap.put("InfoId", message.getID());


        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    ResultMsgDto resultMsgDto = gson.fromJson(jsonObject, ResultMsgDto.class);
                    if (resultMsgDto.getResult().getFlag() == 1) {

                    } else {
                        Toast.makeText(MessageDetailActivity.this, resultMsgDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    refreshView();
                }

            }
        });

        NetworkHelper.getInstance().addToRequestQueue(request, "正在请求数据...");
    }

    @Override
    public void onClick(View view) {
        this.finish();
    }

}
