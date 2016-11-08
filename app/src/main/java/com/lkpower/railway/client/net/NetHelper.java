package com.lkpower.railway.client.net;

import android.app.Activity;
import android.content.DialogInterface;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lkpower.railway.activity.view.NetErrorDialog;
import com.lkpower.railway.activity.view.ProgressHUD;
import com.lkpower.railway.util.NetUtil;

/**
 * Created by sth on 4/22/16.
 */
public class NetHelper {

    private Activity context = null;
    private static NetHelper instance = null;
    // Volley
    private RequestQueue mRequestQueue = null;

    public static NetHelper getInstance() {
        if (instance == null) {
            instance = new NetHelper();
        }

        return instance;
    }

    private RequestQueue getRequestQueue(Activity context) {
        // lazy initialize the request queue, the queue instance will be created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    // Adds the specified request to the global queue using the Default TAG.

    /**
     * @param <T>
     * @param req
     * @param message 如果message＝null,则不弹出提示框。如果message="",则默认弹出提示框“请稍候...”
     */
    public <T> boolean addToRequestQueue(Activity context, Request<T> req, String message) {
        this.context = context;

        if (!NetUtil.isNetworkAvailable(this.context)) {
            NetErrorDialog.getInstance().show(this.context);

            return false;
        }

        this.showProgress(message);

        req.setTag(context);
        getRequestQueue(context).add(req);
        mRequestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                hideProgress();
            }
        });

        return true;
    }

    public void cancelRequest() {
        try {
            mRequestQueue.cancelAll(this);
        } catch (Exception e) {
        }
    }

    // 等待提示框
    private ProgressHUD hud = null;

    public void showProgress(String message) {
        try {
            if (message == null)
                return;

            if ("".equals(message.trim()))
                message = "请稍候...";

            if (hud == null) {
                hud = ProgressHUD.show(context, message, true, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO
                        cancelRequest();

                        dialog.dismiss();
                    }
                });
            } else {
                hud.setMessage(message);
            }

            if (!hud.isShowing())
                hud.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideProgress() {
        try {
            hud.dismiss();
            hud = null;
        } catch (Exception e) {
        }
    }
}
