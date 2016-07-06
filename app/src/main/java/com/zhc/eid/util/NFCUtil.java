package com.zhc.eid.util;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.provider.Settings;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by sth on 3/23/16.
 */
public class NFCUtil {

    /**
     * 判断手机是否可以使用NFC功能。
     *
     * @param context
     * @return
     */
    public static boolean hasNFC(Context context) {
        boolean bRet = false;
        if (context == null)
            return bRet;

        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            // adapter存在，is enable
            bRet = true;
        }
        return bRet;
    }

    /**
     * 跳转到NFC设置界面
     *
     * @param context
     */
    public static void openNFCSetting(final Context context) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("提示")
                .setContentText("该应用需要您打开手机的NFC功能。")
                .setConfirmText("去设置")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        context.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                    }
                })
                .show();

    }

}
