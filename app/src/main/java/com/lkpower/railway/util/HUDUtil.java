package com.lkpower.railway.util;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by sth on 18/01/2017.
 */

public class HUDUtil {

    private static KProgressHUD hud = null;

    public static void showHUD(Context context, String msg) {
        try{
            if (null == msg) {
                hud.dismiss();
                return;
            }

            if (null != hud && hud.isShowing()) {
                hud.setLabel(msg);
                return;
            }

            hud = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(msg)
                    .setCancellable(true)
                    .show();

        } catch (Exception e) {

        }
    }

    public static void dismiss(){
        if (null != hud) {
            hud.dismiss();
        }
    }
}
