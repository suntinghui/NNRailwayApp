package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lkpower.railway.R;
import com.lkpower.railway.dto.TrainInfo;

import java.util.ArrayList;

/**
 * Created by sth on 17/10/2016.
 *
 * 车次列表
 *
 */

public class TrainListActivityEx extends BaseActivity {

    private ListView listView = null;
    private TrainListAdapter adapter = null;
    private ArrayList<TrainInfo> mList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_train_list);

        mList = (ArrayList<TrainInfo>) this.getIntent().getSerializableExtra("INFO");

        initView();
    }

    private void initView(){
        TextView titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText("请选择车次");

        Button backButton = (Button) this.findViewById(R.id.backBtn);
        backButton.setVisibility(View.INVISIBLE);

        listView = (ListView) this.findViewById(R.id.listView);
        adapter = new TrainListAdapter(this);
        listView.setAdapter(adapter);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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

            final TrainInfo info = mList.get(position);
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
                    Intent intent = new Intent();
                    intent.putExtra("POSITION", position);

                    TrainListActivityEx.this.setResult(RESULT_OK, intent);
                    TrainListActivityEx.this.finish();
                }
            });


            return convertView;
        }
    }

}
