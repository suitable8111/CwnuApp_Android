package com.yeho.cwnuapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.yeho.cwnuapp.bf.BestFoodActivity;
import com.yeho.cwnuapp.board.concil.ConcilBoardActivity;
import com.yeho.cwnuapp.board.free.BoardActivity;
import com.yeho.cwnuapp.bus.TrafficInfoActivity;
import com.yeho.cwnuapp.circle.CircleActivity;
import com.yeho.cwnuapp.login.GlobalApplication;
import com.yeho.cwnuapp.login.KakaoLoginActivity;
import com.yeho.cwnuapp.mapinfo.MapInfoActivity;
import com.yeho.cwnuapp.notice.NoticeActivity;
import com.yeho.cwnuapp.notice.NoticeAdapter;
import com.yeho.cwnuapp.setting.SettingActivity;
import com.yeho.cwnuapp.sf.SchoolFoodsActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;



public class MainActivity extends BaseActivity implements View.OnClickListener {


    //공지사항 팝업
    private PopupWindow pwindo;
    private int mWidthPixels, mHeightPixels;


    //공지사항 서버 파싱
    PaserNTThread thread = null;
    ArrayList<HashMap<String,String>> noticeAry = null;

    private ArrayList<String> MENU_ID_LIST = new ArrayList<>();

    private Button menuBtn1 = null;
    private Button menuBtn2 = null;
    private Button menuBtn3 = null;
    private Button menuBtn4 = null;
    private Button menuBtn5 = null;
    private Button menuBtn6 = null;
    private Button menuBtn7 = null;
    private Button menuBtn8 = null;

    private Button settingMenu = null;
    private Button noticeBtn = null;


    private final String PROFILE = "프로필";
    private final String SCHOOL_FOOD = "식단표";
    private final String MAP_INFO = "학교지도";
    private final String FREE_BOARD = "자유게시판";
    private final String CONCIL_BOARD = "총학생회게시판";
    private final String TRAFFIC = "교통정보";
    private final String ISSUEFOOD = "맛집";
    private final String CURCLE = "동아리";

    private final String SETTING = "설정";
    private final String NOTICE = "공지사항";
    private final String NOTICEAPP = "앱공지사항";

    private long backKeyPressedTime = 0;

    private Tracker mTracker;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (SettingActivity.isMenuSetChange == true){
//            getSavedList();
//            Toast.makeText(getApplicationContext(), "갱신완료... ", Toast.LENGTH_LONG).show();
//            SettingActivity.isMenuSetChange = false;
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalApplication application = (GlobalApplication) getApplication();
        mTracker = application.getDefaultTracker();

        setContentView(R.layout.activity_main);

        menuBtn1 = (Button)findViewById(R.id.main_btn_1);
        menuBtn2 = (Button)findViewById(R.id.main_btn_2);
        menuBtn3 = (Button)findViewById(R.id.main_btn_3);
        menuBtn4 = (Button)findViewById(R.id.main_btn_4);
        menuBtn5 = (Button)findViewById(R.id.main_btn_5);
        menuBtn6 = (Button)findViewById(R.id.main_btn_6);
        menuBtn7 = (Button)findViewById(R.id.main_btn_7);
        menuBtn8 = (Button)findViewById(R.id.main_btn_8);

        settingMenu = (Button)findViewById(R.id.setting_btn);
        noticeBtn = (Button)findViewById(R.id.school_notice_info_button);

        getSavedList();
        makeLayoutSize();
        paserNotice();

//        mapInfoBtn.setOnClickListener(this);
//        kakaoLoginBtn.setOnClickListener(this);
//        freeBoardBtn.setOnClickListener(this);
//        settingBtn.setOnClickListener(this);

        //팝업 생성 함수

        LinearLayout layout = (LinearLayout)findViewById(R.id.main_bg_Layout);

        layout.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.main_bg)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleView(findViewById(R.id.main_bg_Layout));
    }
    private void recycleView(View view) {
        if(view != null) {
            Drawable bg = view.getBackground();
            if(bg != null) {
                bg.setCallback(null);
                ((BitmapDrawable)bg).getBitmap().recycle();
                view.setBackgroundDrawable(null);
            }
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getTag().toString()){

            case SCHOOL_FOOD :
                Intent intentSF = new Intent(this, SchoolFoodsActivity.class);
                startActivity(intentSF);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)학교식단표")
                        .build()));
                break;
            case MAP_INFO:
                Intent intentMI = new Intent(this, MapInfoActivity.class);
                startActivity(intentMI);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)학교지도")
                        .build()));
                break;
            case PROFILE:
                Intent intentKL = new Intent(this, KakaoLoginActivity.class);
                startActivity(intentKL);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)프로필화면")
                        .build()));
                break;
            case FREE_BOARD:
                Intent intentFB = new Intent(this, BoardActivity.class);
                startActivity(intentFB);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)대나무슾")
                        .build()));
                break;
            case SETTING:
                Intent intentST = new Intent(this, SettingActivity.class);
                startActivity(intentST);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)셋팅")
                        .build()));
                break;
            case CONCIL_BOARD:
                Intent intentCB = new Intent(this, ConcilBoardActivity.class);
                startActivity(intentCB);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)총학게시판화면")
                        .build()));
                break;
            case TRAFFIC:
                Intent intentTF = new Intent(this, TrafficInfoActivity.class);
                startActivity(intentTF);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)교통정보")
                        .build()));
                break;
            case ISSUEFOOD:
                Intent intentBF = new Intent(this, BestFoodActivity.class);
                startActivity(intentBF);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)맛집정보")
                        .build()));
                //Toast.makeText(getApplicationContext(), "맛집(쿠폰)서비스 준비 중 입니다 ^^;", Toast.LENGTH_SHORT).show();
                break;
            case CURCLE:
                Intent intentC = new Intent(this, CircleActivity.class);
                startActivity(intentC);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)동아리화면")
                        .build()));
                break;
            case NOTICE :
                Intent intentN = new Intent(this, NoticeActivity.class);
                startActivity(intentN);
                mTracker.send((new HitBuilders.EventBuilder()
                        .setCategory("Main")
                        .setAction("(안드로이드)알림화면")
                        .build()));
//                initiatePopupWindow();
                break;
            case NOTICEAPP :
                initiatePopupWindow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
                backKeyPressedTime = System.currentTimeMillis();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                finish();
            }

    }

    void getSavedList(){
//        SharedPreferences prefsCheck = getSharedPreferences("MenuList", MODE_PRIVATE);
//
//        MENU_ID_LIST.clear();
//        if (!prefsCheck.getString("menu1","").equals("")){
//            for (int i = 0; i < 5; i++){
//                MENU_ID_LIST.add(prefsCheck.getString("menu"+i,""));
//            }
//        }else {
        MENU_ID_LIST.add(PROFILE);
        MENU_ID_LIST.add(MAP_INFO);
        MENU_ID_LIST.add(SCHOOL_FOOD);
        MENU_ID_LIST.add(TRAFFIC);
        MENU_ID_LIST.add(FREE_BOARD);
        MENU_ID_LIST.add(CONCIL_BOARD);
        MENU_ID_LIST.add(NOTICE);
        MENU_ID_LIST.add(CURCLE);
        MENU_ID_LIST.add(SETTING);
        MENU_ID_LIST.add(NOTICEAPP);

        //}

        menuBtn1.setTag(MENU_ID_LIST.get(0));
        menuBtn2.setTag(MENU_ID_LIST.get(1));
        menuBtn3.setTag(MENU_ID_LIST.get(2));
        menuBtn4.setTag(MENU_ID_LIST.get(3));
        menuBtn5.setTag(MENU_ID_LIST.get(4));
        menuBtn6.setTag(MENU_ID_LIST.get(5));
        menuBtn7.setTag(MENU_ID_LIST.get(6));
        menuBtn8.setTag(MENU_ID_LIST.get(7));

        settingMenu.setTag(MENU_ID_LIST.get(8));
        noticeBtn.setTag(MENU_ID_LIST.get(9));

//        menuBtn1.setText(MENU_ID_LIST.get(0));
//        menuBtn2.setText(MENU_ID_LIST.get(1));
//        menuBtn3.setText(MENU_ID_LIST.get(2));
//        menuBtn4.setText(MENU_ID_LIST.get(3));
//        menuBtn5.setText(MENU_ID_LIST.get(4));

        menuBtn1.setOnClickListener(this);
        menuBtn2.setOnClickListener(this);
        menuBtn3.setOnClickListener(this);
        menuBtn4.setOnClickListener(this);
        menuBtn5.setOnClickListener(this);
        menuBtn6.setOnClickListener(this);
        menuBtn7.setOnClickListener(this);
        menuBtn8.setOnClickListener(this);

        noticeBtn.setOnClickListener(this);
        settingMenu.setOnClickListener(this);

    }


    private void paserNotice(){

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0://메시지 실패

                        break;
                    case 1://메시지 성공
                        noticeAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        noticeBtn.setText(noticeAry.get(0).get("title"));
                        break;
                }
            }
        };
        //파싱 종류 ALL일 경우 모두의 정보 값을 받아 온다
        thread = new PaserNTThread(handler);
        thread.start();

    }
    //팝업 관련 함수

    private void makeLayoutSize(){
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;


        //상태바 메뉴바 크기 포함 재 계산
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        }
        // 상태바와 메뉴바의 크기를 포함
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {

            }
        }
    }
    private void initiatePopupWindow(){
        try {
            //  LayoutInflater 객체와 시킴
            LayoutInflater inflaterPop = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflaterPop.inflate(R.layout.activity_main_notice_pop_up, (ViewGroup)findViewById(R.id.main_notice_pop_up));

            final ListView noticeListView = (ListView)layout.findViewById(R.id.notice_info_ListView);
            final Button cancelBtn = (Button)layout.findViewById(R.id.notice_info_button);
            final MainNoticeAdapter mainNoticeAdapter = new MainNoticeAdapter(layout.getContext(),noticeAry);

            pwindo = new PopupWindow(layout, mWidthPixels, mHeightPixels,true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pwindo.setFocusable(false);

            noticeListView.setAdapter(mainNoticeAdapter);
            noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intentNT = new Intent(layout.getContext(), NoticeWebViewActivity.class);
                    intentNT.putExtra("url", noticeAry.get(position).get("link"));
                    startActivity(intentNT);
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    pwindo=null;
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PaserNTThread extends Thread{

        ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapA;
        private String flag = null;
        ArrayList<String> flagList = new ArrayList<>();


        Handler handler = null;

        public PaserNTThread(Handler handler) {
            this.handler = handler;
            this.flagList.add("id");
            this.flagList.add("title");
            this.flagList.add("link");
            this.flagList.add("posttime");
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            try {

                URL url = null;
                url = new URL("http://cinavro12.cafe24.com/cwnu/notice/cwnu_notice_xml.php");


                conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        Log.d("Test", "connect OK");
                        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                        BufferedReader br = new BufferedReader(isr);

                        XmlPullParserFactory parserFac = XmlPullParserFactory.newInstance();
                        XmlPullParser parserA = parserFac.newPullParser();
                        parserA.setInput(br);
                        parserA.next();

                        int parserEvent = parserA.getEventType();
                        while (parserEvent != XmlPullParser.END_DOCUMENT) {

                            switch (parserEvent){
                                case XmlPullParser.START_DOCUMENT:
                                    break;
                                case XmlPullParser.START_TAG:
                                    this.flag = parserA.getName();
                                    if (parserA.getName().equals("row")){
                                        this.mapA = new HashMap<String,String>();
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    if (parserA.getName().equals("row")) {
                                        this.allAry.add(this.mapA);
                                        //마지막 태그니깐 해쉬맵을 ArrayA로 저장
                                    }
                                    break;
                                case XmlPullParser.TEXT:
                                    if (this.flagList.contains(this.flag)) {
                                        this.mapA.put(this.flag, parserA.getText());

                                        this.flag = "NO";
                                        //flag를 NO로하는 이유는 END_TAG 다음 TEXT는 빈텍스트지만 안드로이드에서 넣으려한다 따라서 flagList에서 없는 flag를 넣어 getText 못하게한다
                                    }
                                    break;
                            }
                            parserEvent = parserA.next();
                        }
                        br.close();
                    }
                }

                Message msg = new Message();
                if (this.allAry.size() == 0) {
                    msg.what = 0;
                } else {
                    msg.obj = this.allAry;
                    msg.what = 1;
                }
                handler.sendMessage(msg);

            } catch (XmlPullParserException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 0;
            } catch (IOException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 0;
            }
        }
    }
}
