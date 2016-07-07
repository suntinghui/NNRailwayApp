package com.zhc.eid.activity.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.jungly.gridpasswordview.GridPasswordView.OnPasswordChangedListener;
import com.zhc.eid.R;

public class VerifyTransferPWDDialog extends Dialog {

    private GridPasswordView transferPwdView = null;
    private TextView titleTextView = null;
    private TextView tipTextView = null;
    private TextView errorTextView = null;
    private Button closeBtn = null;
    private Button confirmBtn = null;

    private OnConfirmListener confirmListener = null;

    public VerifyTransferPWDDialog(Context context) {
        this(context, R.style.ProgressHUD);
    }

    public VerifyTransferPWDDialog(Context context, int theme) {
        super(context, theme);

        this.initView(context);
    }

    private void initView(Context context) {
        this.setContentView(R.layout.layout_verify_transfer_pwd);

        this.setCanceledOnTouchOutside(false);
        this.setCancelable(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        this.getWindow().setAttributes(lp);

        transferPwdView = (GridPasswordView) this.findViewById(R.id.transferPwdView);
        transferPwdView.setPasswordVisibility(false);
        transferPwdView.setOnPasswordChangedListener(new OnPasswordChangedListener() {

            @Override
            public void onTextChanged(String s) {
                errorTextView.setText("");
            }

            @Override
            public void onInputFinish(String s) {

            }
        });

        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        tipTextView = (TextView) this.findViewById(R.id.tipTextView);
        errorTextView = (TextView) this.findViewById(R.id.errorTextView);
        errorTextView.setText("");

        closeBtn = (Button) this.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValue()) {
                    if (confirmListener != null) {
                        confirmListener.onConfirm(transferPwdView.getPassWord());
                    }
                }
            }
        });

    }

    private boolean checkValue() {
        if (transferPwdView.getPassWord().length() != 6) {
            errorTextView.setText("请输入签名密码");
            //ViewUtil.shakeView(transferPwdView);

            return false;
        }

        return true;
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setTip(String tip) {
        if (tip == null) {
            tipTextView.setVisibility(View.GONE);
            return;
        }

        tipTextView.setText(tip);
    }

    public void setError(String error) {
        errorTextView.setText(error);
    }

    public String getPassword() {
        return transferPwdView.getPassWord();
    }

    public void clearInputPwd() {
        transferPwdView.setPassword("");
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.confirmListener = listener;
    }

    public interface OnConfirmListener {
        public void onConfirm(String pwdStr);
    }

}
