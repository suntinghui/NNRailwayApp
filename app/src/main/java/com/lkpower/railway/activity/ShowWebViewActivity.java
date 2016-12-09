package com.lkpower.railway.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.lkpower.railway.R;
import com.lkpower.railway.activity.view.NetErrorDialog;
import com.lkpower.railway.activity.view.NoZoomControllWebView;
import com.lkpower.railway.client.ActivityManager;
import com.lkpower.railway.util.NetUtil;

public class ShowWebViewActivity extends BaseActivity implements OnClickListener {

    private Button backBtn = null;

    private TextView titleTextView = null;
    private NoZoomControllWebView webView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_webview);

        String title = this.getIntent().getStringExtra("title");
        String url = this.getIntent().getStringExtra("url");

        backBtn = (Button) this.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(this);

        titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        webView = (NoZoomControllWebView) this.findViewById(R.id.webview);
        WebSettings setting = webView.getSettings();

        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);

        setting.setSupportZoom(true);
        setting.setLoadsImagesAutomatically(true);

        setting.setBuiltInZoomControls(true);

        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);

        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void onResume() {
        super.onResume();

        if (!NetUtil.isNetworkAvailable(this)) {
            NetErrorDialog.getInstance().show(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backBtn) {
            this.backAction();
        }
    }

    public void onBackPressed() {
        this.backAction();
    }

    private void backAction() {
        this.finish();
    }

}
