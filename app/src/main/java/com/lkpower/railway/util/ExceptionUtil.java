package com.lkpower.railway.util;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by sth on 18/01/2017.
 */

public class ExceptionUtil {

    public static String getMsg(Exception e) {
        String msg = "出现错误,请重试。";

        if (e instanceof SocketTimeoutException) {
            msg = "请求超时,有可能网络连接不畅,请稍候重试。";
        } else if (e instanceof SocketException) {
            msg = "网络连接故障,请与管理员联系或稍候重试。";
        } else if (e instanceof ConnectException) {
            msg = "网络连接故障,请与管理员联系或稍候重试。";
        } else {
            msg = "服务器异常,请与管理员联系或稍候重试。(" + e.getMessage() + ")";
        }

        return msg;

    }
}
