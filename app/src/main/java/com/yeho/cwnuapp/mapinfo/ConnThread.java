package com.yeho.cwnuapp.mapinfo;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kakao.usermgmt.response.model.UserProfile;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KimDaeho on 15. 12. 27..
 */
public class ConnThread extends Thread {

    private HashMap<String, String> mapA = null;
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();

    private Handler handler = null;

    private ArrayList<String> flagList = new ArrayList<>();
    private String flag = null;
    private String category = null;
    private String searchttext = null;

    public ConnThread (Handler handler, String category, String searchttext){
        this.handler = handler;
        this.category = category;
        this.searchttext = searchttext;
        flagList.add("room_name");
        flagList.add("floor_num");
        flagList.add("building_num");
    }
    @Override
    public void run() {
        HttpURLConnection conn = null;
        try {
            Thread.sleep(500);
            URL url = null;


           if (category.equals("NUM")){
                url = new URL("http://chains.changwon.ac.kr/nonstop/building_info/building_info.php?id="+searchttext);
            } else if (category.equals("SEARCH")){
                url = new URL("http://chains.changwon.ac.kr/nonstop/building_info/building_info.php?dept="+searchttext);
            }
            conn = (HttpURLConnection)url.openConnection();
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
                                if (parserA.getName().equals("building")){
                                    this.mapA = new HashMap<String,String>();
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (parserA.getName().equals("building")) {
                                    this.allAry.add(this.mapA);
                                    //마지막 태그니깐 해쉬맵을 ArrayA로 저장
                                }
                                break;
                            case XmlPullParser.TEXT:
                                if (this.flagList.contains(this.flag)) {
                                    this.mapA.put(this.flag, parserA.getText());
                                    Log.d("Test", "text : " + parserA.getText());
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
            msg.obj = this.allAry;
            msg.what = 1;
            handler.sendMessage(msg);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 0;
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 0;
        }
    }
}

