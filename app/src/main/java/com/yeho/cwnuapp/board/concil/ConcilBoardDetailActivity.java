package com.yeho.cwnuapp.board.concil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yeho.cwnuapp.BaseActivity;
import com.yeho.cwnuapp.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class ConcilBoardDetailActivity extends BaseActivity implements View.OnClickListener {

    private ProgressDialog mProgressDialog;
    private ListView boardDetailListView = null;
    private ConcilCommentAdapter boardDetailAdapter = null;

    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    private Handler handler = null;

    private ConcilBoardPaserThread thread = null;

    private TextView detailTitleText = null;
    private TextView detailContentText = null;
    private TextView detailPostTimeText = null;
    private TextView detailNameText = null;
    private TextView detailCommentCountText = null;
    private TextView detailGoodCountText = null;

    private ImageView detailImageView = null;
    private Button addCommentBtn = null;
    private Button addGoodBtn = null;

    private Bitmap userBitmap = null;

    private boolean isGoodOk = false;
    private HashMap<String,String> mapA = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.activity_board_detail_sub, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
        addContentView(linearLayout, params);

        Bundle bundle = getIntent().getExtras();
        mapA = (HashMap<String,String>)bundle.getSerializable("mapA");
        //item에서 가져온 정보를 mapA에 기록

        detailTitleText = (TextView)findViewById(R.id.free_board_detail_title_text);
        detailContentText = (TextView)findViewById(R.id.free_board_detail_content_text);
        detailImageView = (ImageView)findViewById(R.id.free_board_detail_imageview);
        detailCommentCountText = (TextView)findViewById(R.id.free_board_detail_comment_count_text);
        detailGoodCountText = (TextView)findViewById(R.id.free_board_detail_good_count_text);
        detailNameText = (TextView)findViewById(R.id.free_board_detail_name_text);
        detailPostTimeText = (TextView)findViewById(R.id.free_board_detail_post_time_text);


        addCommentBtn = (Button)findViewById(R.id.free_board_detail_add_comment_button);
        addGoodBtn = (Button)findViewById(R.id.free_board_detail_add_good_button);

        boardDetailAdapter = new ConcilCommentAdapter(this, allAry,mapA.get("id"));
        boardDetailListView = (ListView)findViewById(R.id.board_detail_listView);
        new ThumbNailThread().execute("go");
        boardDetailListView.setAdapter(boardDetailAdapter);

        detailTitleText.setText(mapA.get("title"));
        detailContentText.setText(mapA.get("context"));
        if (mapA.get("commentcount").equals("null")){
            detailCommentCountText.setText("댓글 +0");
        }else{
            detailCommentCountText.setText("댓글 +"+mapA.get("commentcount"));
        }
        if (mapA.get("goodcount").equals("null")){
            detailGoodCountText.setText("좋아요 : 0");
        }else {
            detailGoodCountText.setText("좋아요 : "+mapA.get("goodcount"));
        }

        detailNameText.setText(mapA.get("name"));
        detailPostTimeText.setText(mapA.get("posttime").substring(0, 16));

        addGoodBtn.setOnClickListener(this);
        addCommentBtn.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog = ProgressDialog.show(ConcilBoardDetailActivity.this,"", "잠시만 기다려 주세요.",true);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        break;
                    case 1:
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        boardDetailAdapter.setAllAry(allAry);
                        break;
                }
                mProgressDialog.dismiss();
            }
        };
        thread = new ConcilBoardPaserThread(handler,"COMMENT",mapA.get("id"));
        thread.start();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.free_board_detail_add_comment_button) {
            Intent intentAP = new Intent(this, ConcilAddPostActivity.class);
            intentAP.putExtra("Post", "Comment");
            intentAP.putExtra("CommentBoardId", mapA.get("id"));
            startActivity(intentAP);
        }else if(v.getId() == R.id.free_board_detail_add_good_button) {
            if (!isGoodOk){
                new UploadGood(mapA.get("id")).execute();
                Toast.makeText(getApplicationContext(), "좋아요를 눌렀습니다!.. ", Toast.LENGTH_LONG).show();
                if (mapA.get("goodcount").equals("null")){
                    detailGoodCountText.setText("좋아요 : 1");
                }else {
                    Integer goodCountNum = Integer.parseInt(mapA.get("goodcount"));
                    goodCountNum = goodCountNum + 1;
                    detailGoodCountText.setText("좋아요 : " + goodCountNum);
                }
                isGoodOk = true;
                addGoodBtn.setBackgroundResource(R.drawable.good_icon_white_board_detail_s);
            }else {
                Toast.makeText(getApplicationContext(), "좋아요를 이미 눌렀습니다!.. ", Toast.LENGTH_LONG).show();
            }


        }
    }

    private class ThumbNailThread extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            userBitmap = loadImage(mapA.get("kakaothumbnail"));
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            detailImageView.setImageBitmap(userBitmap);
        }
    }
    private Bitmap loadImage(String str) {
        InputStream inputStream = null;
        try {
            HttpGet httpRequest = new HttpGet(URI.create(str));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            inputStream = bufHttpEntity.getContent();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

            Bitmap output = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            final int color = 0xff424242;

            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, myBitmap.getWidth(), myBitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = 72;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(myBitmap, rect, rect, paint);

            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //close input
            if (inputStream != null) {
                try {
                    inputStream.close();

                } catch (IOException ioex) {
                    // Handle error
                }
            }
        }
    }

    //좋아요 비동기 스레드
    private class UploadGood extends AsyncTask<Void, Void, String> {
        //업로드 url
        protected String url = "http://cinavro12.cafe24.com/cwnu/board_concil/comment_concil/cwnu_upload_good.php";

        protected String boardId;

        //생성자 제목과 context를 사용 일단 title은 안사용하지만 혹시나 몰라 놔둠 -> 추후 카테고리로 업데이트 할까 고민중
        public UploadGood(String boardId){
            this.boardId = boardId;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpPost request = new HttpPost(url);
                Vector<NameValuePair> nameValue = new Vector<>();
                //Vector를 이용하여 서버에 전송함
                nameValue.add(new BasicNameValuePair("boardid", this.boardId));

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
                Log.d("SUSSEC", total);
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
