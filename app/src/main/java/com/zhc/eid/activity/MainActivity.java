package com.zhc.eid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.zhc.eid.R;
import com.zhc.eid.client.ActivityManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sth on 3/23/16.
 */
public class MainActivity extends BaseActivity {

    private String texts[] = new String[]{"余额查询", "明细查询",
            "跨行汇款", "本行转账",
            "信用卡", "理财",
            "保险", "贷款", "银证银期"};

    private int images[] = new int[]{R.drawable.bag_icon_01, R.drawable.bag_icon_02,
            R.drawable.bag_icon_03, R.drawable.bag_icon_04,
            R.drawable.bag_icon_05, R.drawable.bag_icon_06,
            R.drawable.bag_icon_07, R.drawable.bag_icon_08, R.drawable.bag_icon_09};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.initView();
    }

    private void initView() {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < texts.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", images[i]);
            map.put("itemText", texts[i]);
            lstImageItem.add(map);
        }

        SimpleAdapter saImageItems = new SimpleAdapter(this,
                lstImageItem,// 数据源
                R.layout.night_item,// 显示布局
                new String[]{"itemImage", "itemText"},
                new int[]{R.id.itemImage, R.id.itemText});
        gridview.setAdapter(saImageItems);
        gridview.setOnItemClickListener(new ItemClickListener());
    }

    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long rowid) {
            HashMap<String, Object> item = (HashMap<String, Object>) parent.getItemAtPosition(position);

            //根据图片进行相应的跳转
            switch (position) {
                case 0: { // 余额查询
                    Intent intent = new Intent(MainActivity.this, QueryBalanceActivity.class);
                    startActivity(intent);
                }
                break;

                case 1: { // 明细查询
                    Intent intent = new Intent(MainActivity.this, QueryTransferHistoryActivity.class);
                    startActivity(intent);
                }
                break;

                case 2: { // 跨行汇款
                    Intent intent = new Intent(MainActivity.this, InterbankTransfer1Activity.class);
                    startActivity(intent);
                }
                break;

                default:
                    Toast.makeText(MainActivity.this, "服务开通中，敬请期待...", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }


    @Override
    public void onBackPressed() {
        exitApp();
    }

    private long exitTimeMillis = 0;

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTimeMillis) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTimeMillis = System.currentTimeMillis();
        } else {
            // UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU
//            MobclickAgent.onKillProcess(this); // 用来保存统计数据

            for (Activity act : ActivityManager.getInstance().getAllActivity()) {
                act.finish();
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
}
