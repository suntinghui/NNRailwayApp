package com.lkpower.railway.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.king.photo.util.Bimp;
import com.king.photo.util.PublicWay;
import com.king.photo.util.Res;
import com.king.photo.zoom.PhotoView;
import com.king.photo.zoom.ViewPagerFixed;
import com.lkpower.railway.R;
import com.lkpower.railway.util.FileUtil;
import com.lkpower.railway.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个是用于进行图片浏览时的界面
 *
 * @author Alan
 */
public class GalleryActivity extends BaseActivity {
    private Intent intent;
    // 返回按钮
    private Button backBtn;
    // 发送按钮
    private TextView titleTextView;
    //删除按钮
    private Button delBtn;
    //顶部显示预览图片位置的textview
    private TextView positionTextView;
    //获取前一个activity传过来的position
    private int position;
    //当前的位置
    private int location = 0;

    private ArrayList<View> listViews = null;
    private ViewPagerFixed pager;
    private MyPageAdapter adapter;

    public List<Bitmap> bmp = new ArrayList<Bitmap>();
    public List<String> drr = new ArrayList<String>();
    public List<String> del = new ArrayList<String>();

    private Context mContext;

    RelativeLayout photo_relativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_gallery);// 切屏到主界面

        PublicWay.activityList.add(this);
        mContext = this;
        backBtn = (Button) findViewById(Res.getWidgetID("backBtn"));
        titleTextView = (TextView) findViewById(Res.getWidgetID("titleTextView"));
        delBtn = (Button) findViewById(Res.getWidgetID("gallery_del"));
        delBtn.setVisibility(View.INVISIBLE);

        backBtn.setOnClickListener(new BackListener());
        delBtn.setOnClickListener(new DelListener());
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        position = Integer.parseInt(intent.getStringExtra("position"));
        isShowOkBt();
        // 为发送按钮设置文字
        pager = (ViewPagerFixed) findViewById(Res.getWidgetID("gallery01"));
        pager.setOnPageChangeListener(pageChangeListener);
        for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
            initListViews(Bimp.tempSelectBitmap.get(i).getImageId());
        }

        adapter = new MyPageAdapter(listViews);
        pager.setAdapter(adapter);
        pager.setPageMargin((int) getResources().getDimensionPixelOffset(Res.getDimenID("ui_10_dip")));
        int id = intent.getIntExtra("ID", 0);
        pager.setCurrentItem(id);
    }

    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        public void onPageSelected(int arg0) {
            location = arg0;
            titleTextView.setText(Bimp.tempSelectBitmap.size() + "/" + PublicWay.num );
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void initListViews(String name) {
        try {
            if (listViews == null)
                listViews = new ArrayList<View>();
            PhotoView img = new PhotoView(this);
            img.setBackgroundColor(0xff000000);

            Bitmap bitmap = ImageUtil.decodeSampledBitmapFromResource(FileUtil.getFilePath() + name + ".jpg", 480, 320);

            img.setImageBitmap(bitmap);
            img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            listViews.add(img);
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "图片显示失败", Toast.LENGTH_SHORT).show();
        }

    }

    // 返回按钮添加的监听器
    private class BackListener implements OnClickListener {

        public void onClick(View v) {
            finish();
        }
    }

    // 删除按钮添加的监听器
    private class DelListener implements OnClickListener {

        public void onClick(View v) {
            if (listViews.size() == 1) {
                Bimp.tempSelectBitmap.clear();
                Bimp.max = 0;
                titleTextView.setText(Bimp.tempSelectBitmap.size() + "/" + PublicWay.num );
                Intent intent = new Intent("data.broadcast.action");
                sendBroadcast(intent);
                finish();
            } else {
                Bimp.tempSelectBitmap.remove(location);
                Bimp.max--;
                pager.removeAllViews();
                listViews.remove(location);
                adapter.setListViews(listViews);
                titleTextView.setText(Bimp.tempSelectBitmap.size() + "/" + PublicWay.num);
                adapter.notifyDataSetChanged();
            }
        }
    }

    // 完成按钮的监听
    private class GallerySendListener implements OnClickListener {
        public void onClick(View v) {
            finish();
//			intent.setClass(mContext,PublishActivity.class);
//			startActivity(intent);
        }

    }

    public void isShowOkBt() {
        if (Bimp.tempSelectBitmap.size() > 0) {
            titleTextView.setText(Bimp.tempSelectBitmap.size() + "/" + PublicWay.num );
        }
    }


    class MyPageAdapter extends PagerAdapter {

        private ArrayList<View> listViews;

        private int size;

        public MyPageAdapter(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public void setListViews(ArrayList<View> listViews) {
            this.listViews = listViews;
            size = listViews == null ? 0 : listViews.size();
        }

        public int getCount() {
            return size;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
        }

        public void finishUpdate(View arg0) {
        }

        public Object instantiateItem(View arg0, int arg1) {
            try {
                ((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

            } catch (Exception e) {
            }
            return listViews.get(arg1 % size);
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }
}
