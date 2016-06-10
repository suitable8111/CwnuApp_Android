package com.yeho.cwnuapp.notice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.yeho.cwnuapp.R;

public class NoticeDetailFragment extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail_fragment);

        Bundle bundle = getIntent().getExtras();

        Log.d("TEST 1:", bundle.getString("noticeBoarid"));
        String boardid = bundle.getString("noticeBoarid");


        WebView webView = (WebView)findViewById(R.id.notice_webView);
        String urlString = "";
        switch (bundle.getInt("currentPage")){
            case 0:
                //와글 홈공지
                urlString = "http://portal.changwon.ac.kr/portalMain/mainonHomePostRead.do?homecd=&bno=1507&postno="+bundle.getString("noticeBoarid");
                break;
            case 1:
                //학사안내
                urlString = "http://portal.changwon.ac.kr/homePost/read.do?homecd=portal&bno=3305&postno="+bundle.getString("noticeBoarid");
                break;
            case 2:
                //공지사항
                urlString = "http://portal.changwon.ac.kr/homePost/read.do?homecd=portal&bno=1291&postno="+bundle.getString("noticeBoarid");
                break;
            case 3:
                //모집안내
                urlString = "http://portal.changwon.ac.kr/homePost/read.do?homecd=portal&bno=1293&postno="+bundle.getString("noticeBoarid");
                break;
            default:
                break;
        }
        webView.loadUrl(urlString);
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(true);
        set.setSupportZoom(true);
        set.setUseWideViewPort(true);
        set.setLoadWithOverviewMode(true);

    }
}
