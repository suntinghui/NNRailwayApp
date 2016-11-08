package com.lkpower.railway.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.lkpower.railway.R;

import java.io.InputStream;

public class SplashActivity extends BaseActivity {

	private LinearLayout layout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏

		setContentView(R.layout.activity_splash);

		layout = (LinearLayout) this.findViewById(R.id.rootLayout);

		setBground(R.drawable.splash_1);

		new SplashTask().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		this.gotoLoginActivity();
	}

	private void gotoLoginActivity(){
		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

	private void setBground(InputStream is){
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;

		Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
		BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
		this.getWindow().setBackgroundDrawable(null);
		layout.setBackgroundDrawable(bd);
	}

	private void setBground(int resId) {
		Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
		BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
		this.getWindow().setBackgroundDrawable(null);
		layout.setBackgroundDrawable(bd);
	}

	class SplashTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			startActivityForResult(new Intent(SplashActivity.this, LoginActivity.class), 0);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	}

}
