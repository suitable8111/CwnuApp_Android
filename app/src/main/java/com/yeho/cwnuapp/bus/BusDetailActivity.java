package com.yeho.cwnuapp.bus;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yeho.cwnuapp.R;

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

public class BusDetailActivity extends Activity {


    private BusDetailAdapter busDetailAdapter = null;
    private TextView detailTitleText = null;
    private ListView busDetailListView = null;
    private Bundle      bundle          = null;
    private ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_detail);
        bundle = getIntent().getExtras();
        busDetailListView = (ListView)findViewById(R.id.bus_info_detail_listView);
        detailTitleText = (Button)findViewById(R.id.bus_info_detail_title_back_button);

        detailTitleText.setText(bundle.getString("ST_NAME"));

        startParing(bundle.getString("ST_NUM"));
    }

    private void startParing(String staionNum){

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //Handler 성공시 --> 1 실패시 --> 0
                switch (msg.what) {
                    case 1:
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        busDetailAdapter = new BusDetailAdapter(BusDetailActivity.this,allAry);
                        busDetailListView.setAdapter(busDetailAdapter);
                        break;

                    default:

                        Log.i("msg.what (실패) :", "" + msg.what);
                        break;
                }

            }
        };
        ParserBusInfoThread biThread = new ParserBusInfoThread(handler,staionNum);
        biThread.start();
    }
    private class ParserBusInfoThread extends Thread {

        private ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();
        private ArrayList<HashMap<String, String>> fillterAry = new ArrayList<HashMap<String, String>>();
        private HashMap<String, String> mapA;
        private String flag = null;
        private ArrayList<String> flagList = new ArrayList<>();
        private String stationNum = null;
        Handler handler = null;

        public ParserBusInfoThread(Handler handler, String stationNum) {
            this.handler = handler;
            this.stationNum = stationNum;
            this.flagList.add("ROUTE_ID"); //노선번호
            this.flagList.add("CALC_DATE"); //노선타입
            this.flagList.add("PREDICT_TRAV_TM"); //도착예상시간
            this.flagList.add("LEFT_STATION"); //남은 정류장
            this.flagList.add("UPDN_DIR"); // 상하행
        }

        @Override
        public void run() {


            HttpURLConnection conn = null;
            try {


                URL url = new URL("http://cinavro12.cafe24.com/cwnu/traffic/traffic_service.php?stationNum="+stationNum);
                sleep(500);
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

                ///Filltering



                Message msg = new Message();
                if (this.allAry.size() == 0) {
                    msg.what = 0;
                } else {
                    //quicksort(allAry, 0, allAry.size()-1);
                    for (int i = 0; i < allAry.size(); i++){
                        if(!allAry.get(i).get("PREDICT_TRAV_TM").equals("0")){
                            fillterAry.add(allAry.get(i));
                        }
                    }
                    quicksort(0,fillterAry.size()-1);

                    msg.obj = this.fillterAry;
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        void quicksort(int left, int right){
            if (left < right) {
                int pivot = parition(left, right);
                quicksort(left, pivot-1);
                quicksort(pivot + 1, right);

            }
        }

        int parition(int left, int right){
            int low = left;
            int high = right+1;
            int pivot = Integer.valueOf(fillterAry.get(left).get("PREDICT_TRAV_TM"));
            do {
                do {
                        low++;
                }while (Integer.valueOf(fillterAry.get(low).get("PREDICT_TRAV_TM")) < pivot && low < right);

                do {
                        high--;
                }while (Integer.valueOf(fillterAry.get(high).get("PREDICT_TRAV_TM")) > pivot && low >= high );

               if (low < high) swapAry(low, high);
            }while (low < high);

            swapAry(left,high);

            return high;
        }
        void swapAry(int left, int right){
            HashMap<String, String> tempMap = fillterAry.get(left);
            fillterAry.set(left,fillterAry.get(right));
            fillterAry.set(right,tempMap);
        }
    }
}
