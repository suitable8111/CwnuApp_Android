package com.yeho.cwnuapp.board.free;

import android.os.Message;
import android.util.Log;
import android.os.Handler;

import com.kakao.usermgmt.response.model.UserProfile;

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

/**
 * Created by KimDaeho on 16. 1. 11..
 */
public class BoardPaserThread extends Thread {
    private HashMap<String, String> mapA = null;
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();



    private Handler handler = null;

    private ArrayList<String> flagList = new ArrayList<>();
    private String flag = null;
    private String category = null;

    private String commentBoradId = null;
    private String searchingText = null;



    public BoardPaserThread(Handler handler, String category ){
        this.handler = handler;

        flagList.add("id");
        flagList.add("name");
        flagList.add("context");
        flagList.add("kakaoid");
        flagList.add("kakaothumbnail");
        flagList.add("posttime");
        flagList.add("commentcount");
        flagList.add("goodcount");
        flagList.add("title");
        flagList.add("viewcount");

        this.category = category;
    }
    public BoardPaserThread(Handler handler, String category, String commentBoradId){
        this.handler = handler;

        flagList.add("id");
        flagList.add("name");
        flagList.add("context");
        flagList.add("kakaoid");
        flagList.add("kakaothumbnail");
        flagList.add("posttime");
        
        this.category = category;
        this.commentBoradId = commentBoradId;
    }

    public void setSearchingText(String searchingText){
        this.searchingText = searchingText;
    }
    @Override
    public void run() {

        HttpURLConnection conn = null;
        try {
            Thread.sleep(1000);
            URL url = null;

            if (category.equals("ALL")) {
                url = new URL("http://cinavro12.cafe24.com/cwnu/board/cwnu_board_xml.php");
            }else if (category.equals("ME")){
                UserProfile userProfile = UserProfile.loadFromCache();
                String kakaoId = String.valueOf(userProfile.getId());
                url = new URL("http://cinavro12.cafe24.com/cwnu/board/cwnu_board_me_xml.php?kakaoid="+kakaoId);
            }else if (category.equals("COMMENT")){
                url = new URL("http://cinavro12.cafe24.com/cwnu/board/comment/cwnu_board_comment_xml.php?boardid="+commentBoradId);
            } else if (category.equals("SEARCH")){
                url = new URL("http://cinavro12.cafe24.com/cwnu/board/cwnu_board_search_xml.php?searchname="+searchingText+"&searchtitle="+searchingText+"&searchcontext="+searchingText);
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
            if (this.allAry.size()==0){
                msg.what = 0;
            }else{
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
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }
}
