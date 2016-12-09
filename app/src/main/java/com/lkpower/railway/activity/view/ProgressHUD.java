package com.lkpower.railway.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lkpower.railway.R;

public class ProgressHUD extends Dialog {
    public ProgressHUD(Context context) {
        super(context);
    }

    public ProgressHUD(Context context, int theme) {
        super(context, theme);
    }

    public void setMessage(CharSequence message) {
        if (message != null && message.length() > 0) {
            TextView txt = (TextView) findViewById(R.id.message);
            txt.setText(message);
            txt.invalidate();
        }
    }

    public static ProgressHUD show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
        try {
            ProgressHUD dialog = new ProgressHUD(context, R.style.ProgressHUD);
            dialog.setTitle("");
            dialog.setContentView(R.layout.layout_progress_hud);
            if (message == null || message.length() == 0) {
                dialog.findViewById(R.id.message).setVisibility(View.GONE);
            } else {
                TextView txt = (TextView) dialog.findViewById(R.id.message);
                txt.setText(message);
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(cancelable);
            dialog.setOnCancelListener(cancelListener);
            dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            dialog.getWindow().setAttributes(lp);
            // dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            dialog.show();
            return dialog;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}