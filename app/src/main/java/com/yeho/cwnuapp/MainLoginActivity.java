package com.yeho.cwnuapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.yeho.cwnuapp.login.CertificationActivity;

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
import java.util.Vector;

public class MainLoginActivity extends BaseActivity {


    private SessionCallback callback;
    private UserProfile userProfile;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_common_kakao_login_intro);

//        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
//        pref.getString("check", "");
//        if(pref.getString("check", "").isEmpty()){
//            Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
//            shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//            shortcutIntent.setClassName(this, getClass().getName());
//            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
//                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            Intent intent = new Intent();
//
//            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.app_name));
//            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.kakao_account_logo));
//            intent.putExtra("duplicate", false);
//            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//
//            sendBroadcast(intent);
//        }
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("check", "exist");
//        editor.commit();

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        final Button justGoMain = (Button) findViewById(R.id.com_kakao_just_go_intro);

            justGoMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainLoginActivity.this);

                    builder.setTitle("카카오 로그인을 추천합니다!");
                    builder.setMessage("로그인을 건너뛰시면 일부 서비스에 제한이 있습니다! 건너뛰시겠습니까?");
                    builder.setPositiveButton("건너뛰기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent intent = new Intent(MainLoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

                }
            });

    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            redirectSignupActivity();

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }

            setContentView(R.layout.layout_common_kakao_login_intro);
        }
    }
    protected void redirectSignupActivity() {
        checkUserCash();


    }

    private void checkUserCash() {
        //인증 절차 진행중
        mProgressDialog = ProgressDialog.show(MainLoginActivity.this, "", "잠시만 기다려 주세요...", true);
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
                new CheckUserLoged().execute(String.valueOf(userProfile.getId()));
            }
        });
    }
    private class CheckUserLoged extends AsyncTask<String, Void, String> {
        //업로드 url
        protected String url = "http://cinavro12.cafe24.com/cwnu/user/cwnu_check_user_info.php";

        @Override
        protected String doInBackground(String... params) {


            try {
                HttpPost request = new HttpPost(url);
                Vector<NameValuePair> nameValue = new Vector<>();
                //Vector를 이용하여 서버에 전송함

                nameValue.add(new BasicNameValuePair("kakaoid", params[0]));


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
            mProgressDialog.dismiss();
            if (s.equals("Y")){
                Log.d(" 로그인 :","성공");
                final Intent intent = new Intent(MainLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                Log.d(" 로그인 :","실패 인증절차 필요");
                final Intent intent = new Intent(MainLoginActivity.this, CertificationActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }


}
