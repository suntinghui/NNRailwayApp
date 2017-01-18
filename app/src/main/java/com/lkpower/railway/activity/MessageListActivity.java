package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.Constants;
import com.lkpower.railway.dto.InfoPublishListDto;
import com.lkpower.railway.dto.MessageModel;
import com.lkpower.railway.util.ActivityUtil;
import com.lkpower.railway.util.ExceptionUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

/**
 * Created by sth on 08/11/2016.
 * <p>
 * 段发信息列表
 */

public class MessageListActivity extends BaseActivity implements View.OnClickListener {

    public ListView listView;

    public MessageListAdapter mAdapter = null;
    public List<MessageModel> dataList = new ArrayList<MessageModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message_list);

        init();

        showPushTip(this.getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        showPushTip(intent);
    }

    private void showPushTip(Intent tempIntent) {
        boolean push = tempIntent.getBooleanExtra("PUSH", false);
        if (push) {
            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);

            new SweetAlertDialog(MessageListActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText("提示").setContentText("收到新的段发信息,请及时阅读").setConfirmText("确定").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.cancel();

                }
            }).show();
        }
    }

    public void init() {
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("段发信息");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setOnClickListener(this);

        listView = (ListView) this.findViewById(R.id.listView);
        mAdapter = new MessageListAdapter(this);
        listView.setAdapter(mAdapter);

        ActivityUtil.setEmptyView(this, listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMessageList();
            }
        });

        requestMessageList();
    }

    private void requestMessageList() {
        OkGo.post(Constants.HOST_IP_REQ)
                .tag(this)
                .params("commondKey", "InfoPublishList")
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);

                        HUDUtil.showHUD(MessageListActivity.this, "正在查询信息...");
                    }

                    @Override
                    public void onError(Call call, okhttp3.Response response, Exception e) {
                        super.onError(call, response, e);

                        e.printStackTrace();

                        Toast.makeText(MessageListActivity.this, ExceptionUtil.getMsg(e), Toast.LENGTH_LONG).show();
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
                            InfoPublishListDto infoPublishListDto = gson.fromJson(jsonObject, InfoPublishListDto.class);
                            if (infoPublishListDto.getResult().getFlag() == 1) {
                                dataList = infoPublishListDto.getDataInfo();
                                mAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(MessageListActivity.this, infoPublishListDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        this.finish();
    }

    private class MessageListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MessageListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MessageListActivity.ViewHolder holder = null;

            if (null == convertView) {
                holder = new MessageListActivity.ViewHolder();

                convertView = mInflater.inflate(R.layout.item_message_list, null);
                holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
                holder.contentTextView = (TextView) convertView.findViewById(R.id.contentTextView);
                holder.timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);
                convertView.setTag(holder);

            } else {
                holder = (MessageListActivity.ViewHolder) convertView.getTag();
            }

            final MessageModel message = dataList.get(position);
            holder.titleTextView.setText(message.getTitle());
            holder.contentTextView.setText(message.getContent());
            holder.timeTextView.setText(message.getSubmitTime().trim());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessageListActivity.this, MessageDetailActivity.class);
                    intent.putExtra("message", message);
                    startActivity(intent);
                }

            });


            return convertView;
        }
    }

    private class ViewHolder {
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView timeTextView;
    }


}
