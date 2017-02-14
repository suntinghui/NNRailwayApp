package com.lkpower.railway.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.lkpower.railway.R;
import com.lkpower.railway.util.FileUtil;
import com.lkpower.railway.util.HUDUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by sth on 25/11/2016.
 */

public class ShowLocalImageActivity extends BaseActivity {

    private ImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_showlocalimage);

        this.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageView = (ImageView) this.findViewById(R.id.imageView);

        String name = this.getIntent().getStringExtra("name");

        imageView.setImageBitmap(BitmapFactory.decodeFile(FileUtil.getFilePath() + name + ".jpg"));
        // imageView.setImageBitmap(ImageFactory.ratio(FileUtil.getFilePath() + name + ".jpg", 800, 480));
    }

}
