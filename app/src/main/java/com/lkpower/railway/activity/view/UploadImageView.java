package com.lkpower.railway.activity.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.lkpower.railway.R;
import com.lkpower.railway.activity.BaseActivity;
import com.lkpower.railway.util.ImageUtil;

import java.io.File;
import java.io.IOException;

import static android.R.attr.width;
import static u.aly.av.ac;

/**
 * Created by sth on 27/10/2016.
 */

public class UploadImageView extends RelativeLayout implements View.OnClickListener, InvokeListener, TakePhoto.TakeResultListener {

    private RelativeLayout uploadBgLayout = null;
    private CustomNetworkImageView picImageView = null;
    private ImageView addImageView = null;
    private ImageView cameraImageView = null;

    private BaseActivity context;

    private TakePhoto takePhoto = null;
    private InvokeParam invokeParam = null;

    public UploadImageView(BaseActivity context) {
        super(context);

        init(context);
    }

    public UploadImageView(BaseActivity context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private UploadImageView init(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_upload_image, this);

        uploadBgLayout = (RelativeLayout) this.findViewById(R.id.upload_bg);
        picImageView = (CustomNetworkImageView) this.findViewById(R.id.picImageView);
        addImageView = (ImageView) this.findViewById(R.id.addImageView);
        cameraImageView = (ImageView) this.findViewById(R.id.cameraImageView);

        uploadBgLayout.setOnClickListener(this);
        picImageView.setOnClickListener(this);
        addImageView.setOnClickListener(this);
        cameraImageView.setOnClickListener(this);

        return this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_bg:
            case R.id.picImageView:
            case R.id.addImageView:
            case R.id.cameraImageView:

                takePhoto();

                break;
        }
    }

    private void takePhoto() {
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(file);

        getTakePhoto().onPickFromCaptureWithCrop(imageUri, getCropOptions());
    }

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this.context, this));
        }
        return takePhoto;
    }

    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();
        builder.setAspectX(800).setAspectY(800);
        builder.setWithOwnCrop(false); //是否使用自带的裁剪工具
        return builder.create();
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this.context), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void takeSuccess(TResult result) {
        try {
            String path = result.getImage().getPath();
            Log.e("TakePhoto", "path:" + path);
            Bitmap bitmap = ImageUtil.getBitmapFormUri(this.context, Uri.parse(path));
            this.picImageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.i("TakePhoto", "take fail : " + msg);
    }

    @Override
    public void takeCancel() {
        Log.i("TakePhoto", "操作被取消");
    }
}
