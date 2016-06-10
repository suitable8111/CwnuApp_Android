package com.yeho.cwnuapp.board.free;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.Vector;

//사용자가 게시물을 올리고 수정하는 Activity
public class AddPostActivity extends Activity implements View.OnClickListener {

    private  static final String THUMBNAIL_IMAGE = "thumb_path";
    private  static final String NICK_NAME = "nick";

    //내용물 EditText
    private EditText    contextEdText   = null;
    //제목 EditText
    private EditText    titleEdText     = null;
    //업로드,업데이트 버튼
    private Button      postBtn         = null;
    //업로드 인지 업데이트인지 구분지을수 있는 Bundle
    private Bundle      bundle          = null;

    private CheckBox    unKnownCB    = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        
        contextEdText   = (EditText)findViewById(R.id.add_post_context_editText);
        titleEdText     = (EditText)findViewById(R.id.add_post_title_editText);
        postBtn         = (Button)findViewById(R.id.add_post_button);

        unKnownCB       = (CheckBox)findViewById((R.id.unknown_post_check));

        unKnownCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    unKnownCB.setText("익명");
                }else {
                    unKnownCB.setText("익명하기");
                }
            }
        });
        postBtn.setOnClickListener(this);

        bundle = getIntent().getExtras();
        //bundle에는 여러 Activity에서 Intent한 값들을 가져와 이들이 어디서 왔는지 알아낼 수 있다.
        switch (bundle.getString("Post")){
            case "Upload" :
                titleEdText.setVisibility(View.VISIBLE);
                titleEdText.setHint("제목을 쓰세요");
                contextEdText.setHint("내용을 쓰세요");
                postBtn.setText("올리기");
                break;
            case "Update" :
                titleEdText.setVisibility(View.VISIBLE);
                titleEdText.setText(bundle.getString("Title"));
                contextEdText.setText(bundle.getString("Context"));
                postBtn.setText("수정하기");
                break;
            case "Comment" :
                titleEdText.setVisibility(View.GONE);
                titleEdText.setText("comment");
                contextEdText.setHint("내용을 쓰세요");
                postBtn.setText("댓글 올리기");
                break;
            case "UpdateComment" :
                titleEdText.setVisibility(View.GONE);
                titleEdText.setText("comment");
                contextEdText.setText(bundle.getString("Context"));
                postBtn.setText("댓글 수정하기");
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_post_button:

                //빈칸을 채우도록 만들어야 한다 서버 DB에 공란 발생시 xml 파싱이 힘들어진다.
                if (titleEdText.getText().toString().length()==0 || contextEdText.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(), "빈칸을 채워주세요. ", Toast.LENGTH_LONG).show();
                }else {
                    final String kakaoName = UserProfile.loadFromCache().getProperties().get(NICK_NAME);
                    String kakaothumbnail = "http://cinavro12.cafe24.com/cwnu/default/thumb_story.png";
                    if (!UserProfile.loadFromCache().getProperties().get(THUMBNAIL_IMAGE).equals("")){
                        kakaothumbnail = UserProfile.loadFromCache().getProperties().get(THUMBNAIL_IMAGE);
                    }
                    final String kakaoid = String.valueOf(UserProfile.loadFromCache().getId());


                    //익명성체크
                    if (unKnownCB.isChecked()){
                        switch (bundle.getString("Post")) {
                            case "Upload":
                                //게시물 업로드인 경우
                                new PostThread().execute(bundle.getString("Post"), "익명", titleEdText.getText().toString(), contextEdText.getText().toString(), kakaoid, "http://cinavro12.cafe24.com/cwnu/default/thumb_story.png");
                                break;
                            case "Update":
                                //게시물 수정인 경우
                                new PostThread().execute(bundle.getString("Post"), titleEdText.getText().toString(), contextEdText.getText().toString(), kakaoid, bundle.getString("id"));
                                break;
                            case "Comment":
                                //댓글 업로드인 경우
                                new PostThread().execute(bundle.getString("Post"), bundle.getString("CommentBoardId"), "익명", contextEdText.getText().toString(), kakaoid, "http://cinavro12.cafe24.com/cwnu/default/thumb_story.png");
                                break;
                            case "UpdateComment":
                                //댓글 수정인 경우
                                new PostThread().execute(bundle.getString("Post"), contextEdText.getText().toString(), kakaoid, bundle.getString("id"));
                                break;
                        }
                    }else {
                        switch (bundle.getString("Post")) {
                            case "Upload":
                                //게시물 업로드인 경우
                                new PostThread().execute(bundle.getString("Post"), kakaoName, titleEdText.getText().toString(), contextEdText.getText().toString(), kakaoid, kakaothumbnail);
                                break;
                            case "Update":
                                //게시물 수정인 경우
                                new PostThread().execute(bundle.getString("Post"), titleEdText.getText().toString(), contextEdText.getText().toString(), kakaoid, bundle.getString("id"));
                                break;
                            case "Comment":
                                //댓글 업로드인 경우
                                new PostThread().execute(bundle.getString("Post"), bundle.getString("CommentBoardId"), kakaoName, contextEdText.getText().toString(), kakaoid, kakaothumbnail);
                                break;
                            case "UpdateComment":
                                //댓글 수정인 경우
                                new PostThread().execute(bundle.getString("Post"), contextEdText.getText().toString(), kakaoid, bundle.getString("id"));
                                break;
                        }
                    }
                    onBackPressed();
                }
                break;
        }
    }
    //게시물, 댓글 올리기 수정 Thread
    private class PostThread extends AsyncTask<String , Void, String> {
        private String url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                Vector<NameValuePair> nameValue = new Vector<>();
                //parms[0] == 카테고리를 뜻한다 카테고리를 비교하여 params의 인자값들에다 각각 NameValue 값을 집어넣는다
                switch (params[0]){
                    case "Upload" :
                        url = "http://cinavro12.cafe24.com/cwnu/board/cwnu_upload_post.php";
                        nameValue.add(new BasicNameValuePair("name", params[1]));
                        nameValue.add(new BasicNameValuePair("title", params[2]));
                        nameValue.add(new BasicNameValuePair("context", params[3]));
                        nameValue.add(new BasicNameValuePair("kakaoid", params[4]));
                        nameValue.add(new BasicNameValuePair("kakaothumbnail", params[5]));
                        break;
                    case "Update" :
                        url = "http://cinavro12.cafe24.com/cwnu/board/cwnu_update_post.php";
                        nameValue.add(new BasicNameValuePair("title", params[1]));
                        nameValue.add(new BasicNameValuePair("context", params[2]));
                        nameValue.add(new BasicNameValuePair("kakaoid", params[3]));
                        nameValue.add(new BasicNameValuePair("boardid", params[4]));
                        break;
                    case "Comment" :
                        url = "http://cinavro12.cafe24.com/cwnu/board/comment/cwnu_upload_comment.php";
                        nameValue.add(new BasicNameValuePair("boardid", params[1]));
                        nameValue.add(new BasicNameValuePair("name", params[2]));
                        nameValue.add(new BasicNameValuePair("context", params[3]));
                        nameValue.add(new BasicNameValuePair("kakaoid", params[4]));
                        nameValue.add(new BasicNameValuePair("kakaothumbnail", params[5]));
                        break;
                    case "UpdateComment" :
                        url = "http://cinavro12.cafe24.com/cwnu/board/comment/cwnu_update_comment.php";
                        //nameValue.add(new BasicNameValuePair("name", params[1]));
                        nameValue.add(new BasicNameValuePair("context", params[1]));
                        nameValue.add(new BasicNameValuePair("kakaoid", params[2]));
                        nameValue.add(new BasicNameValuePair("boardid", params[3]));
                        break;

                }
                HttpPost request = new HttpPost(url);
                HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
                request.setEntity(enty);

                HttpClient client = new DefaultHttpClient();
                HttpResponse res = client.execute(request);
                HttpEntity entityResponse = res.getEntity();
                InputStream im = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));

                String total = "";
                String tmp = "";
                while ((tmp = reader.readLine())!=null){
                    if (tmp!=null){
                        total += tmp;
                    }
                }

                im.close();
                Log.d("서버 전송 성공!", total);
                return total;

            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (IOException e){
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


