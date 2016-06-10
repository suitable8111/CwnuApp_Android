package com.yeho.cwnuapp.bf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yeho.cwnuapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BestFoodActivity extends Activity {

    private LinearLayout searchLinearLayout = null;
    private boolean isSearching = false;
    private Button searchingButton = null;
    private EditText searchEditText = null;

    private Button cafeteriaBtn = null;
    private Button deliveryBtn = null;
    private Button coffeeBtn = null;
    private Button barBtn = null;


    private ArrayList<HashMap<String, String>> allAry = new ArrayList<HashMap<String, String>>();

    private ListView bestFoodListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_food);

        bestFoodListView = (ListView)findViewById(R.id.best_food_listView);
        searchLinearLayout  = (LinearLayout)findViewById(R.id.best_food_searchLayout);
        searchingButton = (Button)findViewById(R.id.best_food_search_button);
        searchEditText = (EditText)findViewById(R.id.best_food_search_editText);

        cafeteriaBtn = (Button)findViewById(R.id.best_food_type_cafeteria);
        deliveryBtn = (Button)findViewById(R.id.best_food_type_delivery);
        coffeeBtn = (Button)findViewById(R.id.best_food_type_coffee);
        barBtn = (Button)findViewById(R.id.best_food_type_bar);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Toast.makeText(getApplicationContext(), "검색중 입니다", Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                        paserFoods(searchEditText.getText().toString());
                        return true;

                    default:

                        return false;
                }
            }
        });
        searchingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearching) {
                    disapSearchText();
                } else {
                    showSearchText();
                }
            }
        });
        cafeteriaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paserFoods("CAFETERIA");
            }
        });
        deliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paserFoods("DELIVERY");
            }
        });
        coffeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paserFoods("COFFEE");
            }
        });
        barBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paserFoods("BAR");
            }
        });

        paserFoods("CAFETERIA");
        bestFoodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentBD = new Intent(BestFoodActivity.this, BestFoodDetailActivity.class);
                intentBD.putExtra("bestFoodMapA", allAry.get(position));
                startActivity(intentBD);
            }
        });
    }

    //검색 버튼을 누르는 경우 검색 텍스트뷰가 나타나게 하는 함수
    private void showSearchText(){
        LinearLayout.LayoutParams layparam = (LinearLayout.LayoutParams) searchLinearLayout.getLayoutParams();
        layparam.height = 140;
        searchingButton.setBackgroundResource(R.drawable.cancel_icon);
        searchLinearLayout.setLayoutParams(layparam);
        isSearching = true;
    }
    //검색 버튼을 누르는 경우 검색 텍스트뷰가 사라지게 하는 함수
    private void disapSearchText(){
        LinearLayout.LayoutParams layparam = (LinearLayout.LayoutParams) searchLinearLayout.getLayoutParams();
        layparam.height = 0;
        searchingButton.setBackgroundResource(R.drawable.search_icon);
        searchLinearLayout.setLayoutParams(layparam);
        isSearching = false;
    }
    private void paserFoods(String typeText){
        //mProgressDialog = ProgressDialog.show(BestFoodActivity.this,"", "잠시만 기다려 주세요.",true);
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0://메시지 실패
                        Toast.makeText(getApplicationContext(), "검색에 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        break;
                    case 1://메시지 성공
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        BestFoodListAdapter bestFoodListAdapter = new BestFoodListAdapter(BestFoodActivity.this,allAry);
                        bestFoodListView.setAdapter(bestFoodListAdapter);
                        break;
                }
                //mProgressDialog.dismiss();
            }
        };
        //파싱 종류 ALL일 경우 모두의 정보 값을 받아 온다
        PaserBFThread thread = new PaserBFThread(handler, typeText);
        thread.start();

    }

}


