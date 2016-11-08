package com.lkpower.railway.client;

import com.lkpower.railway.util.ActivityUtil;

public class Constants {

    public static final int SMS_MAX_TIME = 60; // 短信平台重发最大时间 秒
    public static final int INITIAL_DELAY_MILLIS = 175;

    public static final int PAGESIZE = 20;

    // 生产
//    public static final String HOST_IP = "http://www.baggugu.com";
//    public static final String HOST_IP_REQ = HOST_IP + ":8111";

    // 测试
//    public static final String HOST_IP = "http://103.43.185.166:39/LinkPower.PduOA6.Prj.nanning/InspectionWork/MobileJson.ashx";
    public static final String HOST_IP = "http://61.235.163.12:88/InspectionWork/MobileJson.ashx";

    public static final String HOST_IP_REQ = HOST_IP;

    public static String CUSTOMERNO = "sdp";
    public static String ACCOUNTNO = "9559980759778592817";
    public static String ACCOUNTNAME = "";

    public static final String PROTOCOL_IP = HOST_IP + "/app/agreement.html";

    public static boolean TIP_TIME_MODEL = true;

    public static boolean RUNNING = false;

    public static String PHONE_SERVICE = "01053812098";

    public static final String Base_Token = "Base-Token";
    public static final String SESSIONID = "Cookie";
    public static final String Set_Cookie = "Set-Cookie";

    public static final String DEVICETOKEN = "DEVICETOKEN";

    public static final String FIRST_LANUCH = "FIRST_LANUCH_" + ActivityUtil.getVersionCode();

    public static final String HEAD_RANDOM = "HEAD_RANDOM";

    public static final String UserName = "UserName";
    public static final String Password = "Password";
    public static final String USERID = "USERID";

    public static final String CITY_CODE = "CITY_CODE";

    public static final String UMengPUSHId = "UMengPUSHId";

    public static final String WX_APP_ID = "wxf7baba5c341a6655";
    public static final String WX_AppSecret = "d4624c36b6795d1d99dcf0547af5443d";

    public static final String QQ_APP_ID = "1104879229";
    public static final String QQ_APP_KEY = "qzaUsnB2t7p3uan6";

    public static boolean NEED_REFRESH_LOGIN = false;

    public static String ACTION_CHECK_TABHOST = "com.housekeeper.check.tabhost";
    public static String ACTION_CHECK_RELATION = "ACTION_CHECK_RELATION";

    public static String APP_EID_CODE = "11";

    public static String eID_APP_ID = "EFBFBDEFBFBDEFBFBDEFBFBD3330333734333038323130303533";
    public static String eID_APP_KEY = "EFBFBDEFBFBDEFBFBDEFBFBD3DEFBFBD3330343331393733373134363430";

}
