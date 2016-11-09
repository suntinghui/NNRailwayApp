package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.dto.MessageDto;
import com.lkpower.railway.util.ActivityUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sth on 08/11/2016.
 *
 * 段发信息列表
 */

public class MessageListActivity extends BaseActivity {
    public TextView mTvTitle;
    public ListView mLvMessage;
    public MessageListAdapter mAdapter = null;
    public ArrayList<MessageDto.Message> datas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        init();
    }

    public void init(){

        mTvTitle = (TextView) findViewById(R.id.titleTextView);
        mTvTitle.setText("任务信息列表");

        mLvMessage = (ListView) findViewById(R.id.lv_message);
        mAdapter = new MessageListAdapter(this);
        mLvMessage.setAdapter(mAdapter);
        ActivityUtil.setEmptyView(this, mLvMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMessageList();
            }
        });
        requestMessageList();

    }


    private void requestMessageList() {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "commondKey");
        tempMap.put("serialNumber", "serialNumber");
        tempMap.put("userId", "userId");
        tempMap.put("stationId", "stationId");

        JSONRequest request = new JSONRequest(this, RequestEnum.MessageList, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    MessageDto messageDto = gson.fromJson(jsonObject, MessageDto.class);
                    if (messageDto.getResult().getFlag() == 1) {
                        datas = messageDto.getDataInfo();
                        mAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(MessageListActivity.this, messageDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在查询任务信息...");
    }

    private class MessageListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public MessageListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return datas.size();
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
                holder.mTvTaskName = (TextView) convertView.findViewById(R.id.tv_taskname);
                holder.mTvState = (TextView) convertView.findViewById(R.id.tv_state);
                holder.mTvTaskContent = (TextView) convertView.findViewById(R.id.tv_task_content);
                holder.mTvSerialNumber = (TextView) convertView.findViewById(R.id.tv_serial_number);
                holder.mTvName = (TextView) findViewById(R.id.tv_serial_people);
                holder.mTvUpdateTime = (TextView) findViewById(R.id.tv_update_time);
                convertView.setTag(holder);
            } else {
                holder = (MessageListActivity.ViewHolder) convertView.getTag();
            }

            final MessageDto.Message message = datas.get(position);
            String taskName = message.getMisName();
            if(taskName!=null&&!"".equals(taskName)){
                holder.mTvTaskName.setText(taskName);
            }else{
                holder.mTvTaskName.setText("");
            }

            String state = message.getState();
            if(state!=null&&!"".equals(state)){
                if(state.equals("1")){
                    holder.mTvState.setText("未完成");
                    holder.mTvState.setTextColor(Color.BLUE);
                }else{
                    holder.mTvState.setText("已完成");
                    holder.mTvState.setTextColor(Color.RED);
                }
            }else{
                holder.mTvState.setText("");
            }

            String taskContent = message.getMisDistance();
            if(taskContent!=null&&!"".equals(taskContent)){
                holder.mTvTaskContent.setText(taskContent);
            }else{
                holder.mTvTaskContent.setText("");
            }

            String serialNumber = message.getSerialNumber();
            if(serialNumber!=null&&!"".equals(serialNumber)){
                holder.mTvSerialNumber.setText(serialNumber);
            }else{
                holder.mTvSerialNumber.setText("");
            }

            String name = message.getMisName();
            if(name!=null&&!"".equals(name)){
                holder.mTvName.setText(name);
            }else{
                holder.mTvName.setText("");
            }

            String updateTime = message.getUpdateTime();
            if(updateTime!=null&&!"".equals(updateTime)){
                holder.mTvUpdateTime.setText(updateTime);
            }else{
                holder.mTvUpdateTime.setText(updateTime);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessageListActivity.this, MessageDetailActivity.class);
                    intent.putExtra("commondKey","commondKey");
                    intent.putExtra("missionStateId","missionStateId");
                    startActivity(intent);
                }

          });


            return convertView;
        }
    }

    private class ViewHolder {
        private TextView mTvTaskName,mTvState,mTvTaskContent,mTvSerialNumber,mTvName,mTvUpdateTime;
    }


}
