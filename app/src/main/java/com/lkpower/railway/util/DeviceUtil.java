package com.lkpower.railway.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by sth on 09/11/2016.
 */

public class DeviceUtil {

    public static String getDeviceId(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId))
            return "0";

        return  deviceId;
//        return "864103029118723";
    }

}
