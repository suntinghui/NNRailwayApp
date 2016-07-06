package com.zhc.eid.client;


import com.zhc.eid.client.net.RequestModel;

import java.util.HashMap;


public class RequestEnum {

    private static HashMap<String, RequestModel> requestMap = null;

    public static RequestModel getRequest(String id) {
        if (null == requestMap) {
            requestMap = new HashMap<String, RequestModel>();

            requestMap.put(HOTELORDER, new RequestModel(HOTELORDER, Constants.HOST_IP_REQ + "TransCode=HOTELORDER"));
            requestMap.put(QUERYHOTEL, new RequestModel(QUERYHOTEL, Constants.HOST_IP_REQ + "TransCode=QUERYHOTEL"));
            requestMap.put(ROOMRESERVED, new RequestModel(ROOMRESERVED, Constants.HOST_IP_REQ + "TransCode=ROOMRESERVED"));
            requestMap.put(QUERYBINDINGS, new RequestModel(QUERYBINDINGS, Constants.HOST_IP_REQ + "TransCode=QUERYBINDINGS"));
            requestMap.put(BINDINGS, new RequestModel(BINDINGS, Constants.HOST_IP_REQ + "TransCode=BINDINGS"));

        }

        return requestMap.get(id);
    }

    public static final String HOTELORDER = "HOTELORDER"; // 查询订单
    public static final String QUERYHOTEL = "QUERYHOTEL"; // 酒店查询
    public static final String ROOMRESERVED = "ROOMRESERVED"; // 酒店预订
    public static final String QUERYBINDINGS = "QUERYBINDINGS"; // 判断银行卡是否绑定
    public static final String BINDINGS = "BINDINGS"; // 绑定银行卡
}
