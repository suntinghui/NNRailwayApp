package com.lkpower.railway.client;

import com.lkpower.railway.dto.DeviceInfo;
import com.lkpower.railway.util.ActivityUtil;

public class Constants {

    // 测试
//    public static final String HOST_IP = "http://103.43.185.166:39/InspectionWork/MobileJson.ashx";

    // 生产
    public static final String HOST_IP = "http://61.235.163.12:88/InspectionWork/MobileJson.ashx";

    public static final String HOST_IP_REQ = HOST_IP;

    public static boolean CURRENT_TRAIN_LATE = false; // 当前车次是否晚点

    public static DeviceInfo DeviceInfo = null;
    public static String CarNumberId = null;
    public static String CarNumberName = null;

    public static final String FIR_API_TOKEN = "b466e4ea1d74d418b79837f4fd6302a8";
    public static final String FIR_APP_ID = "5848e52a959d69340f002b59";


    public static boolean RUNNING = false;

}
