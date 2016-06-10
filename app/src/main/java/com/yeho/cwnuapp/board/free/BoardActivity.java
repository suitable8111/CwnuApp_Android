package com.yeho.cwnuapp.board.free;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kakao.usermgmt.response.model.UserProfile;
import com.yeho.cwnuapp.BaseActivity;
import com.yeho.cwnuapp.R;
import com.yeho.cwnuapp.board.concil.ConcilBoardPaserThread;

import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Vector;


//게시판 Activity Made By Kim

public class BoardActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ProgressDialog mProgressDialog;

    private TextView        titleTextView       = null;
    //게시판 올리기 Activity로 이동시키는 버튼
    private Button          addPostBtn          = null;
    //게시판 검색 기능 버튼
    private Button          searchBoardBtn      = null;
    //검색 기능 EditText
    private EditText        searchEdText        = null;
    //검색 화면을 보여주게 하는 레이아웃
    private LinearLayout    searchLinearLayout  = null;
    //게시판 리스트 뷰
    //private ListView        boardListview       = null;

    private PullToRefreshListView pullToRefreshListView = null;
    //리스트 뷰 커스텀 어댑터
    private BoardAdapter    boardAdapter        = null;
    //검색 상태인지 아닌지 비교하는 boolean 값
    private boolean         isSearching         = false;
    //Board xml 파싱 스레드
    private Handler                             handler = null;
    private BoardPaserThread                    thread  = null;
    private ArrayList<HashMap<String,String>>   allAry  = new ArrayList<>();
    

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.activity_board_sub, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
        addContentView(linearLayout, params);

        addPostBtn          = (Button)findViewById(R.id.free_board_add_post_button);
        searchBoardBtn      = (Button)findViewById(R.id.free_board_search_button);
        searchEdText        = (EditText)findViewById(R.id.free_board_search_text);
        searchLinearLayout  = (LinearLayout)findViewById(R.id.free_board_search_linearLayout);
        pullToRefreshListView       = (PullToRefreshListView)findViewById(R.id.board_listView);
        titleTextView       = (TextView)findViewById(R.id.board_title_textview);

        boardAdapter = new BoardAdapter(this, allAry);
        pullToRefreshListView.setAdapter(boardAdapter);
        titleTextView.setText("창원대 대나무 숲");
        pullToRefreshListView.setOnItemClickListener(this);
        addPostBtn.setOnClickListener(this);
        searchBoardBtn.setOnClickListener(this);
        startParsing();
        searchEdText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Toast.makeText(getApplicationContext(), "검색중 입니다", Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEdText.getWindowToken(), 0);


                        Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                //Handler 성공시 --> 1 실패시 --> 0
                                switch (msg.what) {
                                    case 1:
                                        allAry = (ArrayList<HashMap<String, String>>) msg.obj;
                                        boardAdapter.setAllAry(allAry);
                                        boardAdapter.notifyDataSetChanged();
                                        searchEdText.setText("");
                                        Toast.makeText(getApplicationContext(), "검색이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 0:
                                        Toast.makeText(getApplicationContext(), "검색에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Log.i("msg.what (실패) :", "" + msg.what);
                                        break;
                                }
                            }
                        };

                        BoardPaserThread searchThread = new BoardPaserThread(handler, "SEARCH");
                        searchThread.setSearchingText(searchEdText.getText().toString());
                        searchThread.start();
                        return true;

                    default:

                        return false;
                }
            }
        });
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                startRefreshParsing();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (UserProfile.loadFromCache() == null) {
            //사용자 로그인이 되지 않았을때
            Toast.makeText(getApplicationContext(), "로그인 후 글을 써주세요!", Toast.LENGTH_LONG).show();
        }else {
        switch (v.getId()) {
            case R.id.free_board_search_button:
                if (isSearching){
                    disapSearchText();
                }else {
                    showSearchText();
                }
                break;
            case R.id.free_board_add_post_button:
                Intent intentAP = new Intent(this, AddPostActivity.class);
                intentAP.putExtra("Post", "Upload");
                startActivity(intentAP);
                break;
            }
        }
    }

    //DetailActivity로 가는 버튼 putExtra를 통하여 map값을 들고간다
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intentBD = new Intent(this, BoardDetailActivity.class);
        intentBD.putExtra("mapA",allAry.get(position-1));
        startActivity(intentBD);
        new UploadViewCount(allAry.get(position-1).get("id")).execute();
    }
    
    //검색 버튼을 누르는 경우 검색 텍스트뷰가 나타나게 하는 함수
    private void showSearchText(){
        LinearLayout.LayoutParams layparam = (LinearLayout.LayoutParams) searchLinearLayout.getLayoutParams();
        layparam.height = 140;
        searchLinearLayout.setLayoutParams(layparam);
        searchBoardBtn.setBackgroundResource(R.drawable.cancel_icon);
        isSearching = true;
    }
    //검색 버튼을 누르는 경우 검색 텍스트뷰가 사라지게 하는 함수
    private void disapSearchText(){
        LinearLayout.LayoutParams layparam = (LinearLayout.LayoutParams) searchLinearLayout.getLayoutParams();
        layparam.height = 0;
        searchLinearLayout.setLayoutParams(layparam);
        searchBoardBtn.setBackgroundResource(R.drawable.search_icon);
        isSearching = false;
    }

    private void startParsing(){
        mProgressDialog = ProgressDialog.show(BoardActivity.this,"", "잠시만 기다려 주세요.",true);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0://메시지 실패

                        break;
                    case 1://메시지 성공
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        boardAdapter.setAllAry(allAry);
                        boardAdapter.notifyDataSetChanged();
                        disapSearchText();
                        break;
                }

                mProgressDialog.dismiss();
            }
        };
        //파싱 종류 ALL일 경우 모두의 정보 값을 받아 온다
        thread = new BoardPaserThread(handler, "ALL");
        thread.start();
    }
    private void startRefreshParsing(){
        //xml파싱 핸들러 xml에 성공하면 정보들을 배열에 담는다
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0://메시지 실패

                        break;
                    case 1://메시지 성공
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        boardAdapter.setAllAry(allAry);
                        boardAdapter.notifyDataSetChanged();
                        disapSearchText();
                        break;
                }

                pullToRefreshListView.onRefreshComplete();

            }
        };
        //파싱 종류 ALL일 경우 모두의 정보 값을 받아 온다
        thread = new BoardPaserThread(handler, "ALL");
        thread.start();
    }
    @Override
    public void onBackPressed() {
        if (isSearching) {
            disapSearchText();
        }else {
            super.onBackPressed();
        }
    }
    private class UploadViewCount extends AsyncTask<Void, Void, String> {

        protected String url = "http://cinavro12.cafe24.com/cwnu/board/cwnu_upload_view_count.php";


        private String boardId;

        public UploadViewCount(String boardId){
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
                Log.d("SUSSEC ViewCount", total);
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
