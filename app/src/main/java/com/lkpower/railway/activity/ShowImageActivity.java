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
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static android.R.attr.resource;
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
        imageView.setMinScale(0.5F);
        imageView.setMaxScale(3.0F);

        final String testUrl = this.getIntent().getStringExtra("url");
        Log.e("---", "---:" + testUrl);

        OkGo.get(testUrl)
                .tag(this)
                .execute(new BitmapCallback() {

                    @Override
                    public void onBefore(BaseRequest request) {

                        Toast.makeText(ShowImageActivity.this, "正在加载图片...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Bitmap bitmap, Call call, Response response) {
                        File file = new File(FileUtil.getFilePath(), "yaoyao.jpg");

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
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
                            // 将保存的地址给SubsamplingScaleImageView,这里注意设置ImageViewState
                            imageView.setImage(ImageSource.uri(file.getAbsolutePath()), new ImageViewState(1.0F, new PointF(0, 0), 0));
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
