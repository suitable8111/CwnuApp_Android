package com.yeho.cwnuapp.bus;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.yeho.cwnuapp.R;

public class SchoolBusActivity extends Fragment {

    public static SchoolBusActivity newInstance(int page){
        SchoolBusActivity schoolBusActivity = new SchoolBusActivity();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        schoolBusActivity.setArguments(args);

        return schoolBusActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_school_bus, container, false);
        WebView webView = (WebView)view.findViewById(R.id.school_bus_webView);
        webView.loadUrl("http://cinavro12.cafe24.com/cwnu/schoolbus/");
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setSupportZoom(false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
