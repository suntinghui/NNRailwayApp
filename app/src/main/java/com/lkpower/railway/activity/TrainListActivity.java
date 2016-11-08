package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lkpower.railway.R;
import com.lkpower.railway.client.RequestEnum;
import com.lkpower.railway.client.net.JSONRequest;
import com.lkpower.railway.dto.LoginDto;
import com.lkpower.railway.dto.TrainDto;
import com.lkpower.railway.util.ActivityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sth on 17/10/2016.
 *
 * 车次列表
 *
 */

public class TrainListActivity extends BaseActivity {

    private ListView listView = null;
    private TrainListAdapter adapter = null;
    private List<TrainDto.TrainDataInfo> mList = new ArrayList<TrainDto.TrainDataInfo>();
    private LoginDto loginDto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_train_list);

        loginDto = (LoginDto) this.getIntent().getSerializableExtra("INFO");

        initView();
    }

    private void initView(){
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("车次列表");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setVisibility(View.INVISIBLE);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new TrainListAdapter(this);
        listView.setAdapter(adapter);

        ActivityUtil.setEmptyView(this, listView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTrainList();
            }
        });

        requestTrainList();
    }

    private void requestTrainList(){
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("commondKey", "TrainInfo");
        tempMap.put("userId", loginDto.getDataInfo().getID());

        JSONRequest request = new JSONRequest(this, RequestEnum.LoginUserInfo, tempMap, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    Gson gson = new GsonBuilder().create();
                    TrainDto trainDto = gson.fromJson(jsonObject, TrainDto.class);
                    if (trainDto.getResult().getFlag() == 1) {
                        mList = trainDto.getDataInfo();

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(TrainListActivity.this, trainDto.getResult().getFlagInfo(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在查询车次信息...");

    }

    private class ViewHolder {
        private LinearLayout contentLayout;
        private TextView nameTextView;
        private TextView descTextView;
    }

    private class TrainListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public TrainListAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
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
            ViewHolder holder = null;

            if (null == convertView) {
                holder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.layout_train_list, null);
                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.contentLayout);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
                holder.descTextView = (TextView) convertView.findViewById(R.id.descTextView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final TrainDto.TrainDataInfo info = mList.get(position);
            StringBuffer sb = new StringBuffer();
            sb.append(info.getStartStation().getStationName());
            sb.append("(").append(info.getStartStation().getArrivalTime().trim()).append(")");
            sb.append(" -- ");
            sb.append(info.getEndStation().getStationName());
            sb.append("(").append(info.getEndStation().getArrivalTime().trim()).append(")");

            holder.nameTextView.setText(info.getTrainName());
            holder.descTextView.setText(sb.toString());
            holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TrainListActivity.this, StationListActivity.class);
                    intent.putExtra("TRAIN_INFO", info);
                    intent.putExtra("LOGIN_INFO", loginDto);
                    startActivity(intent);

                    TrainListActivity.this.finish();
                }
            });


            return convertView;
        }
    }

}
