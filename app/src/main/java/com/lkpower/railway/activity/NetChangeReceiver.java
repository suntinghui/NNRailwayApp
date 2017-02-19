package com.lkpower.railway.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.lkpower.railway.MyApplication;
import com.lkpower.railway.activity.view.NetErrorDialog;

public class NetChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		try {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();

			if (null != activeInfo && activeInfo.isConnected()) {
				Log.e("NET", "＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋＋");
				//Toast.makeText(context, "已连接网络 " + activeInfo.getTypeName(), Toast.LENGTH_SHORT).show();

				NetErrorDialog.getInstance().hide();

			} else {
				// 没有网络链接
				Log.e("NET", "－－－－－－－－－－－－－－－－－－－－－－－－－－网络已断开");

				NetErrorDialog.getInstance().show(MyApplication.getInstance().getCurrentActivity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
