package com.yeho.cwnuapp.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.kakao.usermgmt.response.model.UserProfile;
import com.yeho.cwnuapp.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Vector;

public class SettingActivity extends Activity {


    private CheckBox pushCB = null;
    private WebView developWebView = null;
//    public static boolean isMenuSetChange = false;
//    private int cheackCount = 0;
//    private ArrayList<String> MENU_LIST = new ArrayList<>();
//    private CheckBox profileCB = null;
//    private CheckBox freeboardCB = null;
//    private CheckBox schoolfoodCB = null;
//    private CheckBox mapinfoCB = null;
//    private CheckBox settingCB = null;

//    private CheckBox concilboardCB = null;
//    private CheckBox trafficCB = null;

//    private final String PROFILE = "프로필";
//    private final String SCHOOL_FOOD = "식단표";
//    private final String MAP_INFO = "학교지도";
//    private final String FREE_BOARD = "자유게시판";
//    private final String SETTING = "설정";
//    private final String CONCIL_BOARD = "총학생회게시판";
//    private final String TRAFFIC = "교통정보";



    private Button resetBtn = null;
    private Button setBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        pushCB = (CheckBox) findViewById(R.id.checkBox_push_setting);
        developWebView = (WebView) findViewById(R.id.setting_develop_note);
        developWebView.loadUrl("http://cinavro12.cafe24.com/cwnu/develop/developnote.html");
//        developWebView.loadUrl("http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_xml.php?type=식당");
//        profileCB = (CheckBox)findViewById(R.id.checkBox_profile);
//        freeboardCB = (CheckBox)findViewById(R.id.checkBox_free_board);
//        schoolfoodCB = (CheckBox)findViewById(R.id.checkBox_school_food);
//        mapinfoCB = (CheckBox)findViewById(R.id.checkBox_map_info);
//        settingCB = (CheckBox)findViewById(R.id.checkBox_setting);

//        concilboardCB = (CheckBox)findViewById(R.id.checkBox_concil_board);
//        trafficCB = (CheckBox)findViewById(R.id.checkBox_traffic_info);
//
//        resetBtn = (Button)findViewById(R.id.setting_reset_btn);
//        setBtn = (Button)findViewById(R.id.setting_set_btn);
//

        SharedPreferences prefPush = getSharedPreferences("isPush", MODE_PRIVATE);

        if (prefPush.getString("push", "TRUE").equals("TRUE")) {
            pushCB.setChecked(true);
        } else {
            pushCB.setChecked(false);
        }

        pushCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences nPrefPush = getSharedPreferences("isPush", MODE_PRIVATE);
                SharedPreferences.Editor nEditor = nPrefPush.edit();
                Log.d("kakaoid:", String.valueOf(UserProfile.loadFromCache().getId()));
                if (isChecked) {
                    new PushThread().execute("TRUE", String.valueOf(UserProfile.loadFromCache().getId()));
                    nEditor.putString("push", "TRUE");
                    nEditor.commit();
                    Toast.makeText(getApplicationContext(), "알람설정", Toast.LENGTH_SHORT).show();
                } else {
                    new PushThread().execute("FALSE", String.valueOf(UserProfile.loadFromCache().getId()));
                    nEditor.putString("push", "FALSE");
                    nEditor.commit();
                    Toast.makeText(getApplicationContext(), "알람설정 해제", Toast.LENGTH_SHORT).show();
                }
            }
        });
//
//        profileCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    checkOver(profileCB, PROFILE);
//                }else{
//                    reSetBackGround(profileCB, PROFILE);
//                }
//            }
//        });
//        freeboardCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    checkOver(freeboardCB, FREE_BOARD);
//                }else{
//                    reSetBackGround(freeboardCB, FREE_BOARD);
//                }
//            }
//        });
//        schoolfoodCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    checkOver(schoolfoodCB, SCHOOL_FOOD);
//                }else{
//                    reSetBackGround(schoolfoodCB, SCHOOL_FOOD);
//                }
//            }
//        });
//        mapinfoCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    checkOver(mapinfoCB, MAP_INFO);
//                }else{
//                    reSetBackGround(mapinfoCB, MAP_INFO);
//                }
//            }
//        });
//        settingCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    checkOver(settingCB, SETTING);
//                }else{
//                    reSetBackGround(settingCB, SETTING);
//                }
//            }
//        });
//        concilboardCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    checkOver(concilboardCB, CONCIL_BOARD);
//                }else{
//                    reSetBackGround(concilboardCB, CONCIL_BOARD);
//                }
//            }
//        });
//        trafficCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    checkOver(trafficCB, TRAFFIC);
//                }else{
//                    reSetBackGround(trafficCB, TRAFFIC);
//                }
//            }
//        });
//
//        resetBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clearAry();
//
//                SharedPreferences prefUser = getSharedPreferences("MenuList", MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefUser.edit();
//
//                MENU_LIST.clear();
//
//                editor.clear();
//                editor.putString("menu1","");
//                editor.commit();
//
//                isMenuSetChange = true;
//                onBackPressed();
//            }
//        });
//
//        setBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (cheackCount != 5 ){
//                    clearAry();
//                    Toast.makeText(getApplicationContext(), "5개를 채워 주세요", Toast.LENGTH_LONG).show();
//                }else {
//                    SharedPreferences prefUser = getSharedPreferences("MenuList", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = prefUser.edit();
//
//                    for (int i = 0; i < MENU_LIST.size(); i++){
//                        editor.putString("menu"+i,MENU_LIST.get(i));
//                        editor.commit();
//                    }
//                    isMenuSetChange = true;
//                    onBackPressed();
//                }
//            }
//        });
//
//    }


//    void clearAry(){
//        MENU_LIST.clear();
//        profileCB.setChecked(false);
//        freeboardCB.setChecked(false);
//        schoolfoodCB.setChecked(false);
//        mapinfoCB.setChecked(false);
//        settingCB.setChecked(false);
//        concilboardCB.setChecked(false);
//        trafficCB.setChecked(false);
//
//        cheackCount = 0;
//        Toast.makeText(getApplicationContext(), "다시 해 주세요.", Toast.LENGTH_LONG).show();
//    }
//    void checkOver(CheckBox checkBox, String menu){
//        cheackCount++;
//        if (cheackCount > 5){
//            clearAry();
//            Toast.makeText(getApplicationContext(), "5개 이상 넘을 수 없습니다", Toast.LENGTH_LONG).show();
//        }
//        MENU_LIST.add(menu);
//        switch (cheackCount){
//            case 1:
//                checkBox.setBackgroundResource(R.drawable.check_1);
//                break;
//            case 2:
//                checkBox.setBackgroundResource(R.drawable.check_2);
//                break;
//            case 3:
//                checkBox.setBackgroundResource(R.drawable.check_3);
//                break;
//            case 4:
//                checkBox.setBackgroundResource(R.drawable.check_4);
//                break;
//            case 5:
//                checkBox.setBackgroundResource(R.drawable.check_5);
//                break;
//            default:
//                break;
//        }
//
//    }
//    void reSetBackGround(CheckBox checkBox, String menu){
//        cheackCount--;
//        if (cheackCount < 0){
//            cheackCount = 0;
//        }
//        MENU_LIST.remove(menu);
//        checkBox.setBackgroundResource(R.color.cwnu_main_color8);
//
//        for (int i = 0; i < MENU_LIST.size(); i++){
//            switch (MENU_LIST.get(i)){
//                case PROFILE :
//                    profileCB.setBackgroundResource(R.drawable.check_1+i);
//                    break;
//                case SCHOOL_FOOD :
//                    schoolfoodCB.setBackgroundResource(R.drawable.check_1+i);
//                    break;
//                case MAP_INFO :
//                    mapinfoCB.setBackgroundResource(R.drawable.check_1+i);
//                    break;
//                case FREE_BOARD :
//                    freeboardCB.setBackgroundResource(R.drawable.check_1+i);
//                    break;
//                case SETTING :
//                    settingCB.setBackgroundResource(R.drawable.check_1+i);
//                    break;
//                case CONCIL_BOARD :
//                    concilboardCB.setBackgroundResource(R.drawable.check_1+i);
//                    break;
//                case TRAFFIC :
//                    concilboardCB.setBackgroundResource(R.drawable.check_1+i);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
    }
        private class PushThread extends AsyncTask<String, Void, String> {

            protected String url = "http://cinavro12.cafe24.com/cwnu/push/cwnu_user_push_setting.php";

            @Override
            protected String doInBackground(String... params) {
                //UserProfile userProfile = UserProfile.loadFromCache();
                //this.kakaoId = String.valueOf(userProfile.getId());
                try {
                    HttpPost request = new HttpPost(url);
                    Vector<NameValuePair> nameValue = new Vector<>();
                    //Vector를 이용하여 서버에 전송함
                    nameValue.add(new BasicNameValuePair("push", params[0]));
                    nameValue.add(new BasicNameValuePair("kakaoid", params[1]));


                    HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
                    request.setEntity(enty);

                    HttpClient client = new DefaultHttpClient();
                    HttpResponse res = client.execute(request);
                    HttpEntity entityResponse = res.getEntity();
                    InputStream im = entityResponse.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));

                    String total = "";
                    String tmp = "";
                    while ((tmp = reader.readLine()) != null) {
                        if (tmp != null) {
                            total += tmp;
                        }
                    }

                    im.close();
                    Log.d("SUSSEC", total);
                    return total;

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
    }

