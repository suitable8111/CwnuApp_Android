package com.yeho.cwnuapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class NoticeWebViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_web_view);

        Bundle bundle = getIntent().getExtras();

        String link = bundle.getString("url");

        link = link.replace("&amp;", "&");
        link = link.replace("&#63;", "?");

        WebView webView = (WebView)findViewById(R.id.notice_webView);
        webView.loadUrl(link);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);

    }
}
