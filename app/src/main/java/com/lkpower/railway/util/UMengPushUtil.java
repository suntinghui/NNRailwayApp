package com.lkpower.railway.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

public class UMengPushUtil {

	public class AddAliasTask extends AsyncTask<Void, Void, Boolean> {

		private Context context;

		public AddAliasTask(Context context) {
			this.context = context;
		}

		protected Boolean doInBackground(Void... params) {
			try {

				PushAgent.getInstance(this.context).addAlias(DeviceUtil.getDeviceId(context), "USER_ID", new UTrack.ICallBack() {
					@Override
					public void onMessage(boolean isSuccess, String message) {
						Log.e("UMENG", "isSuccess:" + isSuccess + "," + message);
						if (isSuccess)
							Log.e("UMENG", "alias was set successfully.");
						else
							Log.e("UMENG", "ERROR : alias was set failure.");
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

	}

}
