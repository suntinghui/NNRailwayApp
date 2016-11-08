package com.lkpower.railway.client;


import com.lkpower.railway.client.net.RequestModel;

import java.util.HashMap;


public class RequestEnum {

    private static HashMap<String, RequestModel> requestMap = null;

    public static RequestModel getRequest(String id) {
        if (null == requestMap) {
            requestMap = new HashMap<String, RequestModel>();

            requestMap.put(LoginUserInfo, new RequestModel(LoginUserInfo, Constants.HOST_IP_REQ));


        }

        return requestMap.get(id);
    }

    public static final String LoginUserInfo = "LoginUserInfo"; // 登录
}
