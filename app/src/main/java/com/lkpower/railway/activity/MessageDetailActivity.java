package com.lkpower.railway.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.MessageDto;
import com.lkpower.railway.dto.MessageModel;
import com.lkpower.railway.dto.ResultMsgDto;
import com.lkpower.railway.util.DeviceUtil;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import okhttp3.Call;

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
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "UpdateInfoPublish")
                .params("DeviceId", DeviceUtil.getDeviceId(this))
                .params("InfoId", message.getID())
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        HUDUtil.showHUD(MessageDetailActivity.this, "正在请求数据...");
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);

                        e.printStackTrace();

                        Toast.makeText(MessageDetailActivity.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);

                        HUDUtil.dismiss();
                    }

                    @Override
                    public void onSuccess(String jsonObject, Call call, okhttp3.Response response) {
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
    }

    @Override
    public void onClick(View view) {
        this.finish();
    }

}
