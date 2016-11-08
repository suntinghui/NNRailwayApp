package com.lkpower.railway.activity.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CustomNetworkImageView extends NetworkImageView {

	private Bitmap mLocalBitmap;

	private boolean mShowLocal;

	public void setLocalImageBitmap(int resId) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
		if (bitmap != null) {
			mShowLocal = true;
		}
		this.mLocalBitmap = bitmap;
		requestLayout();
	}

	@Override
	public void setImageUrl(String url, ImageLoader imageLoader) {
		mShowLocal = false;
		super.setImageUrl(url, imageLoader);
	}

	public CustomNetworkImageView(Context context) {
		this(context, null);
	}

	public CustomNetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

		super.onLayout(changed, left, top, right, bottom);
		if (mShowLocal) {
			setImageBitmap(mLocalBitmap);
		}
	}

}
