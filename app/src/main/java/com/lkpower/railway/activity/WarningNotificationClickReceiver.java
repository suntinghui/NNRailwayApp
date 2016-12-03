package com.lkpower.railway.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.lkpower.railway.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sth on 28/11/2016.
 */

public class WarningNotificationClickReceiver extends BroadcastReceiver {
    private static int MAX_COUNT = 3000;
    private int currentCount = 0;

    private static Timer timer = null;

    private static MediaPlayer mPlayer = null;
    private static Vibrator vibrator = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("+++", "++++++++++++++++++++++++++++++++++++++++++++");

        boolean play = intent.getBooleanExtra("PLAY", false);
        if (play) {
            currentCount = 0;
            play(context);

        } else {
            stop();
        }
    }

    private void play(final Context context) {
        if (null == timer) {
            timer = new Timer();
        }

        final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (++currentCount > MAX_COUNT) {
                    stop();
                }

                playSound(context);
                vibrator(context);
            }
        };

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        };

        timer.schedule(timerTask, 10000, 8000);
    }

    private void stop() {
        try {
            currentCount = 0;

            if (null != timer) {
                timer.cancel();
                timer = null;
            }

            stopSound();
            stopVibrator();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSound(Context context) {
        if (mPlayer == null) {
            mPlayer = MediaPlayer.create(context, R.raw.tip);
        }

        mPlayer.start();
    }

    private void stopSound() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer = null;
        }
    }

    private void vibrator(Context context) {
        if (null == vibrator) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }


        long[] pattern = {100, 400, 100, 400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1

    }

    private void stopVibrator() {
        if (null != vibrator) {
            vibrator.cancel();
            vibrator = null;
        }
    }

}
