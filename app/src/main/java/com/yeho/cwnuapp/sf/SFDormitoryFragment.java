package com.yeho.cwnuapp.sf;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yeho.cwnuapp.R;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


//기숙사 식단 정보를 제공하는 Class Thread로 핸들링 시
public class SFDormitoryFragment extends Fragment {

    //private int page;
    //Handler에서 msg.obj에서 가져올 배열
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    //final String[] week = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
    //ListView에 실제로 데이터를 넣게 필요한 배열
    //private ArrayList<String> foods = new ArrayList<>();
    //ListViewAdapter
    //private ArrayAdapter<String> adapterDormitory = null;
    //private SFBaseAdapter adapterDormitory = null;
    //메뉴 리스트 아침 2개(정식,양식), 점심 3개(정식,일품,특식), 저녁 2개(정식,일품)
    //private ArrayList<String> flagList = new ArrayList<>();
    //타이틀 배열
    //private String[] menuList = {"아침 정식","아침 양식","점심 정식","점심 일품","점심 특식","저녁 정식","저녁 일품"};
    private HashMap<String,String> mapDM = new HashMap<>();
    static PaseDMThread thread = null;

    private TextView maTextView = null;
    private TextView mbTextView = null;
    private TextView laTextView = null;
    private TextView lbTextView = null;
    private TextView lcTextView = null;
    private TextView daTextView = null;
    private TextView dbTextView = null;

    //private WebView webView = null;
    //private Typeface font = null;
    public static SFDormitoryFragment newInstance(int page) {
        SFDormitoryFragment sfDormitoryFragment = new SFDormitoryFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        sfDormitoryFragment.setArguments(args);

        return sfDormitoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //page = getArguments().getInt("someInt",0);
        mapDM.put("SUN","opt07");
        mapDM.put("MON","opt01");
        mapDM.put("TUE","opt02");
        mapDM.put("WED","opt03");
        mapDM.put("THU","opt04");
        mapDM.put("FRI","opt05");
        mapDM.put("SAT","opt06");

        //flagList는 Thread애서 가져온 타이틀을 비교함
//        this.flagList.add("MA");
//        this.flagList.add("MB");
//        this.flagList.add("LA");
//        this.flagList.add("LB");
//        this.flagList.add("LC");
//        this.flagList.add("DA");
//        this.flagList.add("DB");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sfdormitory_fragment, container, false);
        //ListView sfDormitoryListView = (ListView)view.findViewById(R.id.sfdormitory_listview);
        //adapterDormitory = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1,foods);
        //adapterDormitory = new SFBaseAdapter(this.getContext(),foods);
        //sfDormitoryListView.setAdapter(adapterDormitory);

        maTextView = (TextView)view.findViewById(R.id.sf_dom_ma_text);
        mbTextView = (TextView)view.findViewById(R.id.sf_dom_mb_text);
        laTextView = (TextView)view.findViewById(R.id.sf_dom_la_text);
        lbTextView = (TextView)view.findViewById(R.id.sf_dom_lb_text);
        lcTextView = (TextView)view.findViewById(R.id.sf_dom_lc_text);
        daTextView = (TextView)view.findViewById(R.id.sf_dom_da_text);
        dbTextView = (TextView)view.findViewById(R.id.sf_dom_db_text);

//        webView = (WebView)view.findViewById(R.id.dormitory_webView);
//        webView.loadUrl("http://libero-news.it.feedsportal.com/c/34068/f/618095/s/2e34796f/l/0L0Sliberoquotidiano0Bit0Cnews0C12735670CI0Esaggi0Eper0Ele0Eriforme0Ecostituzionali0EChiaccherano0Ee0Eascoltano0Bhtml/story01.htm");
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                Log.d("My Webview", url);
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                Log.d("My Webview", url);
//            }
//
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d("My Webview", url);
//                return true;
//            }
//        });

//        String url = webView.getUrl();
//        maTextView.setTypeface(font);
//        mbTextView.setTypeface(font);
//        laTextView.setTypeface(font);
//        lbTextView.setTypeface(font);
//        lcTextView.setTypeface(font);
//        daTextView.setTypeface(font);
//        dbTextView.setTypeface(font);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        //foods.clear();
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        //for (int i = 0; i < allAry.size(); i ++){

                            //foods.add(menuList[i]);
                            //foods.add(allAry.get(i).get(flagList.get(i)));
                        //}

                        maTextView.setText(allAry.get(0).get("MA"));
                        mbTextView.setText(allAry.get(1).get("MB"));
                        laTextView.setText(allAry.get(2).get("LA"));
                        lbTextView.setText(allAry.get(3).get("LB"));
                        lcTextView.setText(allAry.get(4).get("LC"));
                        daTextView.setText(allAry.get(5).get("DA"));
                        dbTextView.setText(allAry.get(6).get("DB"));
                        //adapterDormitory.notifyDataSetChanged();
                        break;
                    default:
                        //foods.add("메뉴를 불러오는데 실패 하였습니다ㅠㅠ");
                        //adapterDormitory.notifyDataSetChanged();
                        break;
                }
            }
        };


        thread = new PaseDMThread(handler);
        thread.start();
        
        return view;
    }


    private class PaseDMThread extends Thread {

        ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> mapA;
        ArrayList<String> flagList = new ArrayList<>();

        Handler handler = null;


        //Domitory
        public PaseDMThread(Handler handler){
            this.handler = handler;
            this.flagList.add("MA");
            this.flagList.add("MB");
            this.flagList.add("LA");
            this.flagList.add("LB");
            this.flagList.add("LC");
            this.flagList.add("DA");
            this.flagList.add("DB");
        }

        @Override
        public void run() {


            Source source = null;
            String content = null;

            //String today = findDay();
            //String today = "opt02";

            try {
                String redirectUrlString = "http://portal.changwon.ac.kr/homePost/list.do?bno=2382";
                URL redirectUrl = new URL(redirectUrlString);

                InputStream redirectIs = redirectUrl.openStream();
                source = new Source(new InputStreamReader(redirectIs,"UTF-8"));
                source.fullSequentialParse();

                Element script = (Element)source.getAllElements(HTMLElementName.SCRIPT).get(9);
                String valueScript = script.getContent().toString();
                valueScript = valueScript.substring(valueScript.indexOf("'")+1,valueScript.length()-1);
                valueScript = valueScript.substring(0,valueScript.indexOf("'"));

                redirectIs.close();

                String urlString = "http://portal.changwon.ac.kr/homePost/"+valueScript;

                URL url = new URL(urlString);
                InputStream is = url.openStream();
                source = new Source(new InputStreamReader(is,"UTF-8"));
                source.fullSequentialParse();

                Element div = (Element)source.getAllElements(HTMLElementName.DIV).get(0);

                List<Element> inputs = (List<Element>)div.getAllElements(HTMLElementName.INPUT);

                for (Element li : inputs){
                    String name = li.getAttributeValue("name");

                    if (name.equals(mapDM.get(SchoolFoodsActivity.SF_TODAY_DAY))){
                        String value = li.getAttributeValue("value");
                        String replaceValue = null;
                        if (value.contains("|||||")){
                            value = value.replace("|||||", "|||");
                        }

                        if (value.contains("||||")) {
                            replaceValue = value.replace("||||", "|||");
                        }else if (value.contains("||")) {
                            replaceValue = value.replace("||", "|");
                        }
                        //주말일경우 주중인 경우
                        if (mapDM.get(SchoolFoodsActivity.SF_TODAY_DAY) == "opt06" || mapDM.get(SchoolFoodsActivity.SF_TODAY_DAY) == "opt07"){
                            cutWeekendString(replaceValue);
                        }else {
                            cutWeekString(replaceValue);
                        }
                    }

                }
                is.close();
                Message msg = new Message();
                msg.what = 0;
                msg.obj = this.allAry;
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
        }

//        private String findDay(){
//            String findToday;
//
//            Calendar oCalender = Calendar.getInstance();
//
//            final String[] week = {"opt07","opt01","opt02","opt03","opt04","opt05","opt06"};
//
//            return week[oCalender.get(Calendar.DAY_OF_WEEK)-1];
//        }
        private void cutWeekString(String value){

            String resultValue;

            for (int i = 0; i < flagList.size()-1; i++){

                resultValue = value.substring(0,value.indexOf("|"));

                mapA = new HashMap<String, String>();
                mapA.put(flagList.get(i), resultValue);
                allAry.add(mapA);

                if (value.length()>=1){
                    value = value.substring(value.indexOf("|") + 1, value.length());
                }else{
                    break;
                }
            }
            mapA = new HashMap<String, String>();
            mapA.put(flagList.get(flagList.size()-1), value);
            allAry.add(mapA);
        }
        private void cutWeekendString(String value){
            String resultValue;



            for (int i = 0; i < flagList.size()-1; i++){
                resultValue = value.substring(0,value.indexOf("|"));
                mapA = new HashMap<String, String>();
                mapA.put(flagList.get(i), resultValue);
                allAry.add(mapA);

                if (value.length()>=1){
                    value = value.substring(value.indexOf("|") + 1, value.length());
                }else{
                    break;
                }
            }

            if (value.length()>=1) {
                mapA = new HashMap<String, String>();
                mapA.put(flagList.get(flagList.size() - 1), value.substring(0, value.length()));
            }else{
                mapA = new HashMap<String, String>();
                mapA.put(flagList.get(flagList.size() - 1), "");
            }
                allAry.add(mapA);

        }

//        private InputStream openConnectionCheckRedirects(URLConnection c) throws IOException
//        {
//            boolean redir;
//            int redirects = 0;
//            InputStream in = null;
//            do
//            {
//                if (c instanceof HttpURLConnection)
//                {
//                    ((HttpURLConnection) c).setInstanceFollowRedirects(false);
//                }
//                in = c.getInputStream();
//                redir = false;
//                if (c instanceof HttpURLConnection)
//                {
//                    HttpURLConnection http = (HttpURLConnection) c;
//                    int stat = http.getResponseCode();
//                    if (stat >= 300 && stat <= 307 && stat != 306 &&
//                            stat != HttpURLConnection.HTTP_NOT_MODIFIED)
//                    {
//                        URL base = http.getURL();
//                        String loc = http.getHeaderField("Location");
//                        URL target = null;
//                        if (loc != null)
//                        {
//                            target = new URL(base, loc);
//                        }
//                        http.disconnect();
//                        if (target == null || !(target.getProtocol().equals("http")
//                                || target.getProtocol().equals("https"))
//                                || redirects >= 5)
//                        {
//                            throw new SecurityException("illegal URL redirect");
//                        }
//                        redir = true;
//                        c = target.openConnection();
//                        redirects++;
//                    }
//                }
//            }
//            while (redir);
//            return in;
//        }


    }
}
