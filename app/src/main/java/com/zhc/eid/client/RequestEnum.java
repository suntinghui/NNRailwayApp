package com.zhc.eid.client;


import com.zhc.eid.client.net.RequestModel;

import java.util.HashMap;


public class RequestEnum {

    private static HashMap<String, RequestModel> requestMap = null;

    public static RequestModel getRequest(String id) {
        if (null == requestMap) {
            requestMap = new HashMap<String, RequestModel>();

            requestMap.put(LOGIN, new RequestModel(LOGIN, Constants.HOST_IP_REQ + "TransCode=m99904"));
            requestMap.put(QUERYBALANCE, new RequestModel(QUERYBALANCE, Constants.HOST_IP_REQ + "TransCode=m10001"));
            requestMap.put(QUERYHISTORY, new RequestModel(QUERYHISTORY, Constants.HOST_IP_REQ + "TransCode=m10002"));
            requestMap.put(INTERBANKTRANSFER, new RequestModel(INTERBANKTRANSFER, Constants.HOST_IP_REQ + "TransCode=m30002"));

        }

        return requestMap.get(id);
    }

    public static final String LOGIN = "LOGIN"; // 登录
    public static final String QUERYBALANCE = "QUERYBALANCE"; // 余额查询
    public static final String QUERYHISTORY = "QUERYHISTORY"; // 明细查询
    public static final String INTERBANKTRANSFER = "INTERBANKTRANSFER"; // 跨行转账
}
