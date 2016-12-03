package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import com.lkpower.railway.util.ActivityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "InfoPublishList");

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
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

        NetworkHelper.getInstance().addToRequestQueue(request, "正在查询信息...");
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
