package com.lkpower.railway.activity;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.lkpower.railway.R;
import com.lkpower.railway.client.net.NetworkHelper;
import com.lkpower.railway.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.System.load;
import static u.aly.av.F;

/**
 * Created by sth on 25/11/2016.
 */

public class ShowImageActivity extends BaseActivity {

    private SubsamplingScaleImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_showimage);

        this.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imageView = (SubsamplingScaleImageView) this.findViewById(R.id.imageView);
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setMinScale(1.0F);
        imageView.setMaxScale(5.0F);

        final String testUrl = this.getIntent().getStringExtra("url");
        Log.e("---", "---:" + testUrl);

        Toast.makeText(this, "正在加载图片,请稍候", Toast.LENGTH_SHORT).show();

        final String downDir = FileUtil.getFilePath();
        //使用Glide下载图片,保存到本地
        Glide.with(this)
                .load(testUrl)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        File file = new File(downDir, "yaoyao.jpg");

                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        FileOutputStream fout = null;
                        try {
                            //保存图片
                            fout = new FileOutputStream(file);
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                            // 将保存的地址给SubsamplingScaleImageView,这里注意设置ImageViewState
                            imageView.setImage(ImageSource.uri(file.getAbsolutePath()), new ImageViewState(1.5F, new PointF(0, 0), 0));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();

                        } finally {
                            try {
                                if (fout != null) fout.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

}
