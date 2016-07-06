package com.zhc.eid.client.net;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.zhc.eid.client.Constants;
import com.zhc.eid.client.RequestEnum;
import com.zhc.eid.util.ActivityUtil;
import com.zhc.eid.util.StringUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class XMLRequest extends Request<XmlPullParser> {

    private static final int CACHE_EXPIRES_TIME = 1 * 365 * 24 * 60 * 60; // 有效期一年
    private static final int REFRESH_NEED = 0;

    private Response.Listener<XmlPullParser> mListener;

    private HashMap<String, String> reqMap = null;

    private String id = RequestEnum.HOTELORDER;

    public XMLRequest(int method, String url, Response.Listener<XmlPullParser> listener,
                      Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        mListener = listener;
    }


    public XMLRequest(Context context, String id, HashMap<String, String> map, boolean shouldCache, Response.Listener<XmlPullParser> listener, ResponseErrorListener errorListener) {
        this(RequestEnum.getRequest(id).getMethod(), RequestEnum.getRequest(id).getUrl(), listener, errorListener);

        mListener = listener;

        reqMap = createRequestData(map);


        if (reqMap != null) {
            Log.e("request data", reqMap.toString());
        }

        this.id = id;

        this.setTag(context);
        this.setShouldCache(shouldCache);

        RetryPolicy retryPolicy = new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.setRetryPolicy(retryPolicy);
    }

    // 首先所有的请求的报文头中都有Base-Token属性，详情见Request类中的getHeader方法；
    // 所有的响应报文中都需要检查Base-Token属性，每次取到该值后进行更新。特殊地，如果登录与验证TOKEN接口没有返回Base-Token属性则清空该属性。该逻辑见本类中的parseNetworkResponse；
    // 针对所有的报文，如果响应代码是LOGIN，则跳转到登录界面，其他则正常解析接口。
    // 特殊地，通过接口请求用户个人信息图片时（不同于取商品图片），上述处理方法也包括，但是从业务逻辑上不会出现请求图片接口返回要求登录的情况，因为接口中数据依赖于其他接口的返回数据。
    @Override
    protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse response) {
        try {
            TreeMap<String, String> headerMap = (TreeMap<String, String>) response.headers;
            saveToken(headerMap);

            Cache.Entry mFakeCache = HttpHeaderParser.parseCacheHeaders(response);
            mFakeCache.etag = null;
            mFakeCache.softTtl = REFRESH_NEED;
            mFakeCache.ttl = System.currentTimeMillis() + CACHE_EXPIRES_TIME * 1000;


            String xmlString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            Log.e("response","===:" + xmlString);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));
            return Response.success(xmlPullParser, HttpHeaderParser.parseCacheHeaders(response));

        } catch (Exception e) {
            e.printStackTrace();

            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(XmlPullParser response) {
        mListener.onResponse(response);
    }

    private void saveToken(TreeMap<String, String> headerMap) {
        Editor editor = ActivityUtil.getSharedPreferences().edit();
        if (headerMap.containsKey(Constants.Base_Token)) {
            editor.putString(Constants.Base_Token, headerMap.get(Constants.Base_Token));
        }

        if (headerMap.containsKey(Constants.Set_Cookie)) {
            String cookie = headerMap.get(Constants.Set_Cookie);
            HashMap<String, String> tempMap = string2Map(cookie);
            if (tempMap.containsKey("JSESSIONID")) {
                editor.putString(Constants.SESSIONID, tempMap.get("JSESSIONID"));
            }
        }
        editor.commit();
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return reqMap;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/x-www-form-urlencoded");
        params.put("ClientType", "Android");
        params.put("OSVersion", "" + android.os.Build.VERSION.SDK_INT);

        return params;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }

    // Volley以url作为Cache Key,因为本项目中有的请求地址有可能地址相同而参数不同，所以重写本方法重定义Cache Key
    // Cache Key : http://182.92.217.168:8888/rpc/goods/type/v_list.app{}
    public String getCacheKey() {
        String cacheKey = "";
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(super.getUrl());
            if (this.getBody() != null) {
                sb.append("&");
                sb.append(new String(this.getBody()));
            }

            cacheKey = sb.toString();

        } catch (Exception e) {
            e.printStackTrace();

            cacheKey = super.getCacheKey();
        }

        Log.e("===", "Cache Key : " + cacheKey);

        return StringUtil.MD5Crypto(cacheKey);
    }

    public void addMarker(String tag) {
        Log.e("===", "marker:" + tag);

        super.addMarker(tag);
    }

    private HashMap<String, String> string2Map(String text) {
        HashMap<String, String> map = new HashMap<String, String>();

        try {
            for (String temp : text.split(";")) {
                String str[] = temp.split("=");
                if (str.length == 2) {
                    map.put(str[0].trim(), str[1].trim());
                }
            }
            return map;
        } catch (Exception e) {
            return new HashMap<String, String>();
        }

    }

    private HashMap<String, String> createRequestData(HashMap<String, String> tempMap) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<CMS><eb><pub>");
        sb.append("<TransCode>").append(tempMap.get("TransCode")).append("</TransCode>");
        sb.append("<TranDate>20160413</TranDate>");
        sb.append("<TranTime>135959</TranTime>");
        sb.append("<fSeqno>01</fSeqno>");
        sb.append("</pub><in>");
        mapToXMLStr(tempMap, sb);
        sb.append("</in></eb></CMS>");

        try {
            HashMap<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("reqData", sb.toString());
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void mapToXMLStr(Map map, StringBuffer sb) {
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);
                sb.append("<" + key + ">");
                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    mapToXMLStr(hm, sb);
                }
                sb.append("</" + key + ">");

            } else {
                if (value instanceof HashMap) {
                    sb.append("<" + key + ">");
                    mapToXMLStr((HashMap) value, sb);
                    sb.append("</" + key + ">");
                } else {
                    sb.append("<" + key + ">" + value + "</" + key + ">");
                }

            }

        }
    }

}
