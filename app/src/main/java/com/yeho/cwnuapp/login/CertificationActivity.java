package com.yeho.cwnuapp.login;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.yeho.cwnuapp.MainActivity;
import com.yeho.cwnuapp.R;
import com.yeho.cwnuapp.push.QuickstartPerferences;
import com.yeho.cwnuapp.push.RegistrationIntentService;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

//인증절차를 거치는 MainActivity
//맨 우선 사용자가 우리쪽 서버에 존재함을 체크하면 MainActivity 이동 아니면 인증 절차를 거침
//인증 절차의 경우 정보전산원에 리퀘스트를 보내고 리스폰이 'Y'라고 오면 사용자 정보를 저장함


public class CertificationActivity extends Activity {
    //PRogressDialog 인증이 될 동안 엑티비티는 가만히 있는다
    //private ProgressDialog mProgressDialog;
    //PUSH TOKEN

    private static final int MY_PERMISSION_REQUEST_STORAGE = 3;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "UserUpdateActivity";
    private String token;
    //KEY
    private  static final String NICK_NAME_KEY = "nick";
    private  static final String NAME_KEY = "name";
    private  static final String SCHOOLNUM_KEY = "schoolnum";
    private  static final String PHONENUM_KEY = "phonenum";
    private  static final String THUMBNAIL_IMAGE = "thumb_path";
//    private  static final String BIRTH_KEY = "birth";
    //USER PROPERTY
    private HashMap<String,String> mapA = null;
    private EditText nameEditText = null;
    //private EditText schoolnumEditText = null;
    private EditText phoneEditText = null;
//    private EditText birthEditText = null;

    //인증하기 버튼
    private Button certifiBtn = null;
    //private Button ceritfiBarcode = null;

    private String barcodeNum = null;


    //동의 팝업 구하기
    private PopupWindow pwindo;
    private int mWidthPixels, mHeightPixels;


    //카카오 유저파일 콜백 함수를 통해서 카카오 서버에 유저 정보를 불러온다.
    private UserProfile userProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification);
        checkPermission();
        //사용자가 서버에 데이터가 존재한다면 바로 MainActivity로 감
        //checkUserCash();

        userProfile = UserProfile.loadFromCache();
        nameEditText = (EditText) findViewById(R.id.certifitcation_name_editText);
        //schoolnumEditText = (EditText) findViewById(R.id.certifitcation_schoolnum_editText);
        phoneEditText = (EditText) findViewById(R.id.certifitcation_phone_editText);
//        birthEditText = (EditText) findViewById(R.id.certifitcation_birth_editText);
        certifiBtn = (Button) findViewById(R.id.certifitcation_confirm_button);
        //ceritfiBarcode = (Button) findViewById(R.id.certifitcation_barcode_button);

        //ceritfiBarcode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ceritfiBarcodeProcess();
//            }
//        });
        TelephonyManager systemService = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        String PhoneNumber = systemService.getLine1Number();

        PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
        PhoneNumber="0"+PhoneNumber;
        PhoneNumber = PhoneNumberUtils.formatNumber(PhoneNumber);
        phoneEditText.setText(PhoneNumber);
        phoneEditText.setEnabled(false);
        certifiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkInput();
                    }
        });
        LinearLayout layout = (LinearLayout)findViewById(R.id.certi_Layout);

        layout.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.main_bg_white)));
                //팝업 레이아웃 크기 계산하는 함수
        makeLayoutSize();
                //팝업 생성 함수 핸들러를 싱행하여 onCreate 후 올려준다.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                initiatePopupWindow();
            }
        }, 100);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleView(findViewById(R.id.certi_Layout));
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
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(CertificationActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                ) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // Explain to the user why we need to write the permission.
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSION_REQUEST_STORAGE);
            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant
        } else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
        }
    }
//    private void checkUserCash() {
//        //인증 절차 진행중
//        mProgressDialog = ProgressDialog.show(CertificationActivity.this,"", "잠시만 기다려 주세요...",true);
//        UserManagement.requestMe(new MeResponseCallback() {
//            @Override
//            public void onSessionClosed(ErrorResult errorResult) {
//
//            }
//
//            @Override
//            public void onNotSignedUp() {
//
//            }
//
//            @Override
//            public void onSuccess(UserProfile result) {
//                userProfile = result;
//                new CheckUserLoged().execute(String.valueOf(userProfile.getId()));
//
//            }
//        });
//    }


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
            LayoutInflater inflaterPop = (LayoutInflater) CertificationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflaterPop.inflate(R.layout.activity_certification_pop_up, (ViewGroup)findViewById(R.id.certifitcation_pop_up));

            pwindo = new PopupWindow(layout, mWidthPixels-80, mHeightPixels-200,true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pwindo.setFocusable(true);

            WebView agreeWV = (WebView)layout.findViewById(R.id.certification_agree_webview);
            agreeWV.loadUrl("http://cinavro12.cafe24.com/cwnu/agree/agree_index.html");

            CheckBox agreeCB = (CheckBox)layout.findViewById(R.id.certification_agree_checkbox);
            agreeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        if (pwindo!=null){
                            pwindo.dismiss();
                            pwindo= null;
                        }
                    }else{

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ceritfiBarcodeProcess(){
        IntentIntegrator.initiateScan(CertificationActivity.this);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.d("result", result.getContents() + ":" + result.getContents());
        if (result.getContents()==null){
            new AlertDialog.Builder(this)
                    .setTitle("학생증 인식 실패")
                    .setMessage("다시 하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ceritfiBarcodeProcess();
                        }
                    }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }else {
            String schoolnum = "";
            if (result.getContents().length() > 8){
                schoolnum = result.getContents().substring(0,8);
            }else {
                schoolnum = result.getContents();
            }

            new AlertDialog.Builder(this)
                    .setTitle("학생증 인식 완료!")
                    .setMessage("[" + schoolnum + "] 학번이 맞습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ceritfiBarcodeProcess();
                }
            }).show();

            //ceritfiBarcode.setText(schoolnum);
            barcodeNum = schoolnum;
        }

    }

    //인증절차 :: 나중에 구현해야함
    private void checkInput(){
        //boolean isPhoneNum = Pattern.matches("^[0-9]*$", phoneEditText.getText().toString());

        if (nameEditText.getText().toString().equals("") || phoneEditText.getText().toString().equals("") ){
            Toast.makeText(getApplicationContext(), "빈칸을 채워주세요. ", Toast.LENGTH_SHORT).show();
        }else if (barcodeNum == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("창원대 학생임을 인증하세요!");
            builder.setMessage("학생증(바코드 인증)을 하시면 앱으로 도서관 자동 출입이 가능합니다!");
            builder.setPositiveButton("인증하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ceritfiBarcodeProcess();
                }
            });
            builder.setNegativeButton("건너뛰기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    certifiProcess();
                }
            });
            builder.show();
        }else {
            certifiProcess();
        }
    }
    private void certifiProcess(){
        goMainActivity();
        registBroadcastReceiver();
        goTakeToken();
        getKakaoUserInfo();
    }

    //전산원에 인증 요청을 보내는 비 동기 스레드 :: 나중에 HTTPS 프로토콜을 통하여 연결함
    private class CertifiUser extends AsyncTask<String, Void, String>{

        protected String url = "http://cinavro12.cafe24.com/cwnu/user/return.php";

        @Override
        protected String doInBackground(String... params) {

            try {
                HttpPost request = new HttpPost(url);
                Vector<NameValuePair> nameValue = new Vector<>();

                nameValue.add(new BasicNameValuePair("schoolnum", params[0]));
                nameValue.add(new BasicNameValuePair("name", params[1]));
                nameValue.add(new BasicNameValuePair("phone", params[2]));
                nameValue.add(new BasicNameValuePair("birth", params[2]));

                HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
                request.setEntity(enty);

                HttpClient client = new DefaultHttpClient();
                HttpResponse res = client.execute(request);
                HttpEntity entityResponse = res.getEntity();
                InputStream im = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));

                String total = "";
                String tmp;
                while ((tmp = reader.readLine()) != null) {
                    if (tmp != null) {
                        total += tmp;
                    }
                }

                im.close();
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

            if (s.equals("Y")){
                //인증성공
                Log.d("인증 :", "성공");

            }else{
                //인증실패 다시 하라는 알람을 만든다.
                Log.d("인증 :", "실패");
                errorCertifi();
            }


        }
    }
    private abstract class UsermgmtResponseCallback<T> extends ApiResponseCallback<T> {
        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            String message = "failed to get user info. msg=" + errorResult;
            Logger.e(message);
            //KakaoToast.makeToast(self, message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
        }
    }
    //인증 성공시 카카오서버에 유저정보를 저정하고 카카오 아이디와 이미지 경로를 갱신한다.
    private void getKakaoUserInfo(){

        mapA = new HashMap<>();
        mapA.put(NAME_KEY, nameEditText.getText().toString());
        mapA.put(PHONENUM_KEY, phoneEditText.getText().toString());
        if (barcodeNum != null){
            mapA.put(SCHOOLNUM_KEY, barcodeNum);
        }
        if (userProfile == null || userProfile.getThumbnailImagePath().equals("")){
            mapA.put(THUMBNAIL_IMAGE, "http://cinavro12.cafe24.com/cwnu/default/thumb_story.png");
            mapA.put(NICK_NAME_KEY, "창대생");
        }else {
            mapA.put(THUMBNAIL_IMAGE, userProfile.getThumbnailImagePath());
            mapA.put(NICK_NAME_KEY, userProfile.getNickname());
        }



        final Map<String,String> properties = mapA;

        UserManagement.requestUpdateProfile(new UsermgmtResponseCallback<Long>() {

            @Override
            public void onSuccess(Long result) {
                userProfile.updateUserProfile(properties);
                if (userProfile != null) {
                    userProfile.saveUserToCache();
                }
                //KakaoToast.makeToast(getApplicationContext(), "succeeded to update user profile", Toast.LENGTH_SHORT).show();
                Logger.d("succeeded to update user profile" + userProfile);

            }

        }, properties);

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile result) {
                userProfile = result;
                new UploadUser().execute(nameEditText.getText().toString(),barcodeNum,phoneEditText.getText().toString(),""+result.getId(),result.getNickname());
            }
        });
    }
    //MainActivity로 이동하는 메소드
    private void goMainActivity(){
        Intent intentMA = new Intent(this, MainActivity.class);
        startActivity(intentMA);
        finish();
    }
    //에러 될시 알람창을 띄우는 메소드
    private void errorCertifi(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CertificationActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("인증 에러");
        builder.setMessage("올바르게 입력해주세요");
        builder.setNegativeButton("취소", null);
        builder.show();
    }
    private void goTakeToken(){
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPerferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPerferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPerferences.REGISTRATION_COMPLETE));
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(QuickstartPerferences.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    token = intent.getStringExtra("token");
                }
            }
        };
    }
//    private class CheckUserLoged extends AsyncTask<String, Void, String> {
//        //업로드 url
//        protected String url = "http://cinavro12.cafe24.com/cwnu/user/cwnu_check_user_info.php";
//
//        @Override
//        protected String doInBackground(String... params) {
//
//
//            try {
//                HttpPost request = new HttpPost(url);
//                Vector<NameValuePair> nameValue = new Vector<>();
//                //Vector를 이용하여 서버에 전송함
//
//                nameValue.add(new BasicNameValuePair("kakaoid", params[0]));
//
//
//                HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
//                request.setEntity(enty);
//
//                HttpClient client = new DefaultHttpClient();
//                HttpResponse res = client.execute(request);
//                HttpEntity entityResponse = res.getEntity();
//                InputStream im = entityResponse.getContent();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));
//
//                String total = "";
//                String tmp = "";
//                while ((tmp = reader.readLine()) != null) {
//                    if (tmp != null) {
//                        total += tmp;
//                    }
//                }
//
//                im.close();
//                Log.d("SUSSEC", total);
//                return total;
//
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            mProgressDialog.dismiss();
//            if (s.equals("Y")){
//                goMainActivity();
//                Log.d(" 로그인 :","성공");
//            }else {
//                Log.d(" 로그인 :","실패 인증절차 필요");
//            }
//
//        }
//    }

    private class UploadUser extends AsyncTask<String, Void, String> {
        //업로드 url
        protected String url = "http://cinavro12.cafe24.com/cwnu/user/cwnu_upload_user_info.php";

        @Override
        protected String doInBackground(String... params) {


            try {
                Thread.sleep(6000);
                String thumbnailImage;

                if (userProfile == null || userProfile.getThumbnailImagePath().equals("")){
                    thumbnailImage = "http://cinavro12.cafe24.com/cwnu/default/thumb_story.png";
                }else{
                    thumbnailImage = userProfile.getThumbnailImagePath();
                }


                HttpPost request = new HttpPost(url);
                Vector<NameValuePair> nameValue = new Vector<>();
                //Vector를 이용하여 서버에 전송함

                nameValue.add(new BasicNameValuePair("realname", params[0]));
                nameValue.add(new BasicNameValuePair("schoolnum", params[1]));
                nameValue.add(new BasicNameValuePair("phonenum", params[2]));
                nameValue.add(new BasicNameValuePair("kakaoid", params[3]));
                nameValue.add(new BasicNameValuePair("nickname", params[4]));
                nameValue.add(new BasicNameValuePair("thumbnailimage", thumbnailImage));
                nameValue.add(new BasicNameValuePair("pushtoken", token));



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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "권한 설정 완료", Toast.LENGTH_SHORT).show();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Toast.makeText(this, "권한 거부", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

}
