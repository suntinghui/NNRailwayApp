package com.lkpower.railway.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.lkpower.railway.R;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;


public class MyUMengPushService extends UmengMessageService {

    PushAgent mPushAgent = null;

    // 如果需要打开Activity，请调用Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)；否则无法打开Activity。
    @Override
    public void onMessage(Context context, Intent intent) {

        mPushAgent = PushAgent.getInstance(this);

        Log.e("PUSH", "----------------收到推送了------------------------");

        UMessage msg = null;
        try {
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            Log.e("===", "===:"+message);

            msg = new UMessage(new JSONObject(message));
            String PushType = msg.extra.get("PushType");

            /**
             LateType_Late    晚点
             LateType_Normal  正点
             Publish 段发信息
             MissionWarning  到点提醒
             */
            if ("LateType_Late".equalsIgnoreCase(PushType)) { // 晚点
                Intent tempIntent = new Intent(context, StationListActivityEx.class);
                tempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                tempIntent.putExtra("PushType", PushType);
                tempIntent.putExtra("LATE_TYPE", true);
                tempIntent.putExtra("LATE", true);
                showNotification(context, msg, tempIntent);

            } else if ("LateType_Normal".equalsIgnoreCase(PushType)) { // 取消晚点
                Intent tempIntent = new Intent(context, StationListActivityEx.class);
                tempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                tempIntent.putExtra("PushType", PushType);
                tempIntent.putExtra("LATE_TYPE", true);
                tempIntent.putExtra("LATE", false);
                showNotification(context, msg, tempIntent);

            } else if ("Publish".equalsIgnoreCase(PushType)) {
                Intent tempIntent = new Intent(context, MessageListActivity.class);
                tempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                tempIntent.putExtra("PUSH", true);
                context.startActivity(tempIntent);

            } else if("MissionWarning".equalsIgnoreCase(PushType)){
                Intent tempIntent = new Intent(context, StationListActivityEx.class);
                tempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                tempIntent.putExtra("PushType", PushType);
                tempIntent.putExtra("InstanceId", msg.extra.get("InstanceId"));//发车实例ID
                tempIntent.putExtra("StationId", msg.extra.get("StationId"));//站点ID
                tempIntent.putExtra("DeviceId", msg.extra.get("DeviceId"));//设备ID
                tempIntent.putExtra("MissionInstanceId", msg.extra.get("MissionInstanceId"));//任务实例ID
                tempIntent.putExtra("StationName", msg.extra.get("StationName"));// 车站名称
                tempIntent.putExtra("ArriveDate", msg.extra.get("ArriveDate"));// 到达时间

                context.startActivity(tempIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();

            //mPushAgent.setPushIntentServiceClass(null);

            Intent tempIntent = new Intent(context, StationListActivityEx.class);
            tempIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            tempIntent.putExtra("PushType", "Test");
            startActivity(tempIntent);
        }
    }

    // 通知栏显示当前播放信息，利用通知和 PendingIntent来启动对应的activity
    public void showNotification(Context context, UMessage msg, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        Notification mNotification = builder.build();
        mNotification.icon = R.drawable.logo;// notification通知栏图标
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" +R.raw.tip);
        // 自定义布局
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
        contentView.setImageViewResource(R.id.notification_large_icon, R.drawable.logo);
        contentView.setTextViewText(R.id.notification_title, msg.title);
        contentView.setTextViewText(R.id.notification_text, msg.text);
        mNotification.contentView = contentView;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 使用自定义下拉视图时，不需要再调用setLatestEventInfo()方法，但是必须定义contentIntent
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(103, mNotification);
    }
}
