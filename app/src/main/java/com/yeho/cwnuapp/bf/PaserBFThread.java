package com.yeho.cwnuapp.bf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KimDaeho on 16. 3. 22..
 */
public class PaserBFThread extends Thread{

    private ProgressDialog mProgressDialog;
    private ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();
    private HashMap<String, String> mapA;
    private String flag = null;
    private ArrayList<String> flagList = new ArrayList<>();
    private String typeText = null;

    private Handler handler = null;

    public PaserBFThread(Handler handler, String typeText) {
        this.handler = handler;
        this.flagList.add("id");
        this.flagList.add("name");
        this.flagList.add("phone");
        this.flagList.add("innerimagepath");
        this.flagList.add("menuimagepath");
        this.flagList.add("outdoorimagepath");
        this.flagList.add("type");
        this.flagList.add("context");
        this.flagList.add("capacity");
        this.flagList.add("opentime");
        this.flagList.add("deilvery");
        this.flagList.add("goodcount");
        this.flagList.add("location");
        this.flagList.add("lat");
        this.flagList.add("long");

        this.typeText = typeText;
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        try {

            URL url = null;
            switch (typeText){
                case "CAFETERIA" :
                    url = new URL("http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_xml.php?type=CAFETERIA");
                    break;
                case "DELIVERY" :
                    url = new URL("http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_xml.php?type=DELIVERY");
                    break;
                case "COFFEE" :
                    url = new URL("http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_xml.php?type=COFFEE");
                    break;
                case "BAR" :
                    url = new URL("http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_xml.php?type=BAR");
                    break;
                default:
                    url = new URL("http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_search_xml.php?searchname="+typeText);
                    break;
            }

            sleep(1000);

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
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}