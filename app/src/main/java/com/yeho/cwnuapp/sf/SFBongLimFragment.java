package com.yeho.cwnuapp.sf;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.kakao.usermgmt.response.model.UserProfile;
import com.yeho.cwnuapp.R;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SFBongLimFragment extends Fragment {

    private int page;
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    //    private ArrayList<String> foods = new ArrayList<>();
//    private SFBaseAdapter adapterBongLim = null;
//    private ArrayList<String> flagList = new ArrayList<>();
//    private String[] menuList = {"교직원 점심","교직원 저녁","학생 점심","학생 저녁","학생 분식"};
    final String[] week = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
    static PaserBLThread thread = null;

    private TextView tlTextView = null;
    private TextView tdTextView = null;
    private TextView slTextView = null;
    private TextView sdTextView = null;
    private TextView ssTextView = null;

    public static SFBongLimFragment newInstance(int page){
        SFBongLimFragment sfBongLimFragment = new SFBongLimFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        sfBongLimFragment.setArguments(args);
        return sfBongLimFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
//        this.flagList.add("TL");
//        this.flagList.add("TD");
//        this.flagList.add("SL");
//        this.flagList.add("SD");
//        this.flagList.add("SS");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.yeho.cwnuapp.R.layout.activity_sfbong_lim_fragment, container, false);
//        ListView sfBongLimListView = (ListView)view.findViewById(com.yeho.cwnuapp.R.id.sfbonglim_listview);
//        adapterBongLim = new SFBaseAdapter(this.getContext(),foods);
//        sfBongLimListView.setAdapter(adapterBongLim);

        tlTextView = (TextView)view.findViewById(R.id.sf_bl_tl_text);
        tdTextView = (TextView)view.findViewById(R.id.sf_bl_td_text);
        slTextView = (TextView)view.findViewById(R.id.sf_bl_sl_text);
        sdTextView = (TextView)view.findViewById(R.id.sf_bl_sd_text);
        ssTextView = (TextView)view.findViewById(R.id.sf_bl_ss_text);

        Handler handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        Log.i("blmsg.what :", "");
//                        foods.clear();
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;

                        tlTextView.setText(allAry.get(0).get("TL"));
                        tdTextView.setText(allAry.get(1).get("TD"));
                        slTextView.setText(allAry.get(2).get("SL"));
                        sdTextView.setText(allAry.get(3).get("SD"));
                        ssTextView.setText(allAry.get(4).get("SS"));

//                        for (int i = 0; i < allAry.size(); i ++){
//                            foods.add(menuList[i]);
//                            foods.add(allAry.get(i).get(flagList.get(i)));
//                        }

//                        adapterBongLim.notifyDataSetChanged();
                        break;
                    default:
//                        foods.add("메뉴를 불러오는데 실패 하였습니다ㅠㅠ");
//                        adapterBongLim.notifyDataSetChanged();
                        break;
                }
            }
        };

        thread = new PaserBLThread(handler);
        thread.start();

        return view;
    }
    private class PaserBLThread extends Thread {

        ArrayList<HashMap<String,String>> allAry = new ArrayList<HashMap<String,String>>();
        ArrayList<HashMap<String,String>> dayAry = new ArrayList<>();
        HashMap<String,String> mapA;
        ArrayList<String> flagList = new ArrayList<>();
        int flagCount = 0;

        Handler handler = null;

        public PaserBLThread (Handler handler){
            this.handler = handler;
            this.flagList.add("TL");
            this.flagList.add("TD");
            this.flagList.add("SL");
            this.flagList.add("SD");
            this.flagList.add("SS");
        }

        @Override
        public void run() {
            Source source = null;

            try {
                URL url = new URL("http://chains.changwon.ac.kr/wwwhome/html/mailbox/lunch.php?kind=B");

                InputStream html = url.openStream();

                source = new Source(new InputStreamReader(html,"euc-kr"));
                source.fullSequentialParse();

                Element table = (Element)source.getAllElements(HTMLElementName.TABLE).get(0);
                List<Element> inputs = (List<Element>)table.getAllElements(HTMLElementName.TD);

                for (Element il : inputs){
                    String nullClassValue = il.getAttributeValue("class");
                    if (nullClassValue != null){
                        if (nullClassValue.equals("pad4")){
                            mapA = new HashMap<String, String >();
                            mapA.put(flagList.get(flagCount / 5),il.getContent().toString().replace("<br>", ""));
                            allAry.add(mapA);
                            flagCount++;
                        }
                    }
                }

                for (int i = 0; i < allAry.size(); i++){
                    switch (SchoolFoodsActivity.SF_TODAY_DAY){
                        case "MON" :
                            if (i % 5 == 0){
                                dayAry.add(allAry.get(i));
                            }
                            break;
                        case "TUE" :
                            if (i % 5 == 1){
                                dayAry.add(allAry.get(i));
                            }
                            break;
                        case "WED" :
                            if (i % 5 == 2){
                                dayAry.add(allAry.get(i));
                            }
                            break;
                        case "THU" :
                            if (i % 5 == 3){
                                dayAry.add(allAry.get(i));
                            }
                            break;
                        case "FRI" :
                            if (i % 5 == 4){
                                dayAry.add(allAry.get(i));
                            }
                            break;
                        default:
                            break;
                    }
                }
                Message msg = new Message();
                if (dayAry.size()==0){
                    msg.what = 1;
                }else {
                    msg.what = 0;
                }
                msg.obj = this.dayAry;
                this.handler.sendMessage(msg);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 1;
                this.handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 2;
                this.handler.sendMessage(msg);
            } catch (Exception e){
                e.printStackTrace();
                Message msg = new Message();
                msg.what = 3;
                this.handler.sendMessage(msg);
            }



            super.run();
        }

    }




//    private int page;
//    private ArrayList<String> myAry = new ArrayList<>();
//
////    private ArrayList<String> foods = new ArrayList<>();
////    private SFBaseAdapter adapterBongLim = null;
////    private ArrayList<String> flagList = new ArrayList<>();
////    private String[] menuList = {"교직원 점심","교직원 저녁","학생 점심","학생 저녁","학생 분식"};
//    final String[] week = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
//    static PaserBLThread thread = null;
//
//    private TextView tlTextView = null;
//    private TextView tdTextView = null;
//    private TextView slTextView = null;
//    private TextView sdTextView = null;
//    private TextView ssTextView = null;
//
//    public static SFBongLimFragment newInstance(int page){
//        SFBongLimFragment sfBongLimFragment = new SFBongLimFragment();
//        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        sfBongLimFragment.setArguments(args);
//        return sfBongLimFragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        page = getArguments().getInt("someInt", 0);
////        this.flagList.add("TL");
////        this.flagList.add("TD");
////        this.flagList.add("SL");
////        this.flagList.add("SD");
////        this.flagList.add("SS");
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(com.yeho.cwnuapp.R.layout.activity_sfbong_lim_fragment, container, false);
////        ListView sfBongLimListView = (ListView)view.findViewById(com.yeho.cwnuapp.R.id.sfbonglim_listview);
////        adapterBongLim = new SFBaseAdapter(this.getContext(),foods);
////        sfBongLimListView.setAdapter(adapterBongLim);
//
//        tlTextView = (TextView)view.findViewById(R.id.sf_bl_tl_text);
//        tdTextView = (TextView)view.findViewById(R.id.sf_bl_td_text);
//        slTextView = (TextView)view.findViewById(R.id.sf_bl_sl_text);
//        sdTextView = (TextView)view.findViewById(R.id.sf_bl_sd_text);
//        ssTextView = (TextView)view.findViewById(R.id.sf_bl_ss_text);
//
//        Handler handler = new Handler(){
//
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                switch (msg.what){
//                    case 1:
//                        Log.i("blmsg.what :", "");
////                        foods.clear();
//                        //allAry = (ArrayList<HashMap<String,String>>)msg.obj;
//                        myAry = (ArrayList<String>)msg.obj;
////                        tlTextView.setText(sfMap.get("C1"));
////                        tdTextView.setText(sfMap.get("C3"));
////                        slTextView.setText(sfMap.get("C4"));
////                        sdTextView.setText(sfMap.get("C5"));
////                        ssTextView.setText(sfMap.get("C6"));
//
//                        tlTextView.setText(myAry.get(2));
//                        tdTextView.setText(myAry.get(1));
//                        slTextView.setText(myAry.get(2));
//                        sdTextView.setText(myAry.get(3));
//                        ssTextView.setText(myAry.get(4));
//
////                        for (int i = 0; i < allAry.size(); i ++){
////                            foods.add(menuList[i]);
////                            foods.add(allAry.get(i).get(flagList.get(i)));
////                        }
//
////                        adapterBongLim.notifyDataSetChanged();
//                        break;
//                    default:
////                        foods.add("메뉴를 불러오는데 실패 하였습니다ㅠㅠ");
////                        adapterBongLim.notifyDataSetChanged();
//                        break;
//                }
//            }
//        };
//
//        thread = new PaserBLThread(handler);
//        thread.start();
//
//        return view;
//    }
//    private class PaserBLThread extends Thread {
//
//        ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();
//        ArrayList<String> resultAry = new ArrayList<String>();
//        HashMap<String, String> mapA;
//        private String flag = null;
//        ArrayList<String> flagList = new ArrayList<>();
//        int flagCount = 0;
//
//        Handler handler = null;
//
//        public PaserBLThread(Handler handler) {
//            this.handler = handler;
//            this.flagList.add("room_id");
//            this.flagList.add("mon");
//            this.flagList.add("tue");
//            this.flagList.add("wed");
//            this.flagList.add("thu");
//            this.flagList.add("fri");
//            this.flagList.add("sat");
//        }
//
//        @Override
//        public void run() {
//            HttpURLConnection conn = null;
//            try {
//
//                URL url = null;
//                url = new URL("http://chains.changwon.ac.kr/nonstop/food/food.php?type=B");
//
//
//                conn = (HttpURLConnection) url.openConnection();
//                if (conn != null) {
//                    conn.setConnectTimeout(10000);
//                    conn.setUseCaches(false);
//                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                        Log.d("Test", "connect OK");
//                        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
//                        BufferedReader br = new BufferedReader(isr);
//
//                        XmlPullParserFactory parserFac = XmlPullParserFactory.newInstance();
//                        XmlPullParser parserA = parserFac.newPullParser();
//                        parserA.setInput(br);
//                        parserA.next();
//
//                        int parserEvent = parserA.getEventType();
//                        while (parserEvent != XmlPullParser.END_DOCUMENT) {
//
//                            switch (parserEvent){
//                                case XmlPullParser.START_DOCUMENT:
//                                    break;
//                                case XmlPullParser.START_TAG:
//                                    this.flag = parserA.getName();
//                                    if (parserA.getName().equals("food_menu")){
//                                        this.mapA = new HashMap<String,String>();
//                                    }
//                                    break;
//                                case XmlPullParser.END_TAG:
//                                    if (parserA.getName().equals("food_menu")) {
//                                        this.allAry.add(this.mapA);
//                                        //마지막 태그니깐 해쉬맵을 ArrayA로 저장
//                                    }
//                                    break;
//                                case XmlPullParser.TEXT:
//                                    if (this.flagList.contains(this.flag)) {
//                                        String realget = parserA.getText().toString();
//                                        this.mapA.put(this.flag, parserA.getText().replace("\"",""));
//                                        Log.d("GET 봉림 " + flag, realget);
//                                        this.flag = "NO";
//
//                                        //flag를 NO로하는 이유는 END_TAG 다음 TEXT는 빈텍스트지만 안드로이드에서 넣으려한다 따라서 flagList에서 없는 flag를 넣어 getText 못하게한다
//                                    }
//                                    break;
//                            }
//                            parserEvent = parserA.next();
//                        }
//                        br.close();
//                    }
//                }
//
//                for (int i = 0; i < allAry.size(); i++){
//                    switch (SchoolFoodsActivity.SF_TODAY_DAY){
//                        case "MON" :
//                            resultAry.add(allAry.get(i).get("mon"));
//                            break;
//                        case "TUE" :
//                            resultAry.add(allAry.get(i).get("tue"));
//                            break;
//                        case "WED" :
//                            resultAry.add(allAry.get(i).get("wed"));
//                            break;
//                        case "THU" :
//                            resultAry.add(allAry.get(i).get("thu"));
//                            break;
//                        case "FRI" :
//                            resultAry.add(allAry.get(i).get("fri"));
//                            break;
//                        case "SUN" :
//                            resultAry.add("주말 휴무");
//                            break;
//                        case "SAT" :
//                            resultAry.add("주말 휴무");
//                            break;
//
//                    }
//                }
//                Message msg = new Message();
//                if (this.resultAry.size() == 0) {
//                    msg.what = 0;
//                } else {
//                    msg.obj = this.resultAry;
//                    msg.what = 1;
//                }
//                handler.sendMessage(msg);
//
//            } catch (XmlPullParserException e) {
//                e.printStackTrace();
//                Message msg = new Message();
//                msg.what = 0;
//            } catch (IOException e) {
//                e.printStackTrace();
//                Message msg = new Message();
//                msg.what = 0;
//            }
//        }
//    }

}
