package com.yeho.cwnuapp.sf;

import android.content.Context;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yeho.cwnuapp.BaseFragment;
import com.yeho.cwnuapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//봉림관, 사림관, 기숙사 식당의 Fragment들을 관리하는 FragmentActivity
public class SchoolFoodsActivity extends BaseFragment {

    TextView dateTextView = null;
    //FragementPager를 실질적으로 관리하는 ViewPager
    FragmentPagerAdapter adapterViewPager;

    public static String SF_TODAY_DAY = null;

    final String[] week = {"MON","TUE","WED","THU","FRI","SAT","SUN"};

    private Button settingDate = null;
    private PopupWindow pwindo;
    private int mWidthPixels, mHeightPixels;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_food);

        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        //MyPagerAdapter라는 클래스를 객체화 시킴
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        SF_TODAY_DAY = findToday();
        dateTextView = (TextView)findViewById(R.id.sf_date_textview);
        dateTextView.setText(findDate());

        //레이아웃 크기 계산
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;


        //상태바 메뉴바 크기 포함 재 계산
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        // 상태바와 메뉴바의 크기를 포함
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                mWidthPixels = realSize.x;
                mHeightPixels = realSize.y;
            } catch (Exception ignored) {

            }

        settingDate = (Button)findViewById(R.id.sf_date_setting_button);
        settingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });
    }

    private void initiatePopupWindow() {
        try {
            //  LayoutInflater 객체와 시킴
            LayoutInflater inflaterPop = (LayoutInflater) SchoolFoodsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflaterPop.inflate(R.layout.sf_school_food_date_pop_up, (ViewGroup)findViewById(R.id.sf_school_food_date_pop_up_element));

            pwindo = new PopupWindow(layout, mWidthPixels, mHeightPixels,true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pwindo.setFocusable(true);

            final ListView dateListView = (ListView)layout.findViewById(R.id.sf_food_date_pop_up_listView);
            final Button cancelButton = (Button)layout.findViewById(R.id.sf_pop_up_cancel_button);

            ArrayList<String> dateAry = null;

            dateAry = putWeek();

            SFBaseAdapter baseAdapter = new SFBaseAdapter(layout.getContext(),dateAry);

            dateListView.setAdapter(baseAdapter);
            dateListView.setOnItemClickListener(cancel_button_click_listener);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    pwindo = null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private AdapterView.OnItemClickListener cancel_button_click_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SF_TODAY_DAY = week[position];
            dateTextView.setText(putWeek().get(position));
            adapterViewPager.notifyDataSetChanged();
            onBackPressed();
        }
    };
    //MyPagerAdatapter 추상 클래스인 FragmentPagerAdapter를 상속받아 오버라이딩 시켜 개발한다
    private static class MyPagerAdapter extends FragmentPagerAdapter {
        private final static int NUM_ITEMS = 3;

        SFDormitoryFragment sfDormitoryFragment = null;
        SFBongLimFragment sfBongLimFragment = null;
        SFSaLimFragment sfSaLimFragment = null;

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;

        }

        public MyPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "기숙사 식단";
                case 1:
                    return "봉림관 식단";
                case 2:
                    return "사림관 식단";
                default:
                    return null;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (sfDormitoryFragment == null){
                        sfDormitoryFragment = SFDormitoryFragment.newInstance(0);
                        return sfDormitoryFragment;
                    }else {
                        return sfDormitoryFragment;
                    }
                case 1:
                    if (sfBongLimFragment == null){
                        sfBongLimFragment = SFBongLimFragment.newInstance(1);
                        return sfBongLimFragment;
                    }else {
                        return sfBongLimFragment;
                    }

                case 2:
                    if (sfSaLimFragment == null){
                        sfSaLimFragment = SFSaLimFragment.newInstance(2);
                        return sfSaLimFragment;
                    }else {
                        return sfSaLimFragment;
                    }
                default:
                    return null;
            }
        }
    }
    private ArrayList<String> putWeek(){
        ArrayList<String> resultAry = new ArrayList<>();

        //String today = findDay();
        Calendar oCalender = Calendar.getInstance();
        while (oCalender.get(Calendar.DAY_OF_WEEK) != 1){
            oCalender.add(Calendar.DATE, -1);
        }
        while (oCalender.get(Calendar.DAY_OF_WEEK) != 7){
            oCalender.add(Calendar.DATE, 1);
            resultAry.add(new SimpleDateFormat("yyyy 년 MM 월 dd 일 E요일", Locale.KOREA).format(oCalender.getTime()));
        }
        oCalender.add(Calendar.DATE, 1);
        resultAry.add(new SimpleDateFormat("yyyy 년 MM 월 dd 일 E요일", Locale.KOREA).format(oCalender.getTime()));

        return resultAry;
    }
    private String findDate(){

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat ( "yyyy 년 MM 월 dd 일 E요일", Locale.KOREA );
        Date currentTime = new Date ();
        String mTime = mSimpleDateFormat.format ( currentTime );


        return mTime;
    }
    private String findToday(){

        Calendar oCalender = Calendar.getInstance();
        String[] weekDefault = {"SUN","MON","TUE","WED","THU","FRI","SAT"};
        return weekDefault[oCalender.get(Calendar.DAY_OF_WEEK)-1];
    }

    @Override
    public void onBackPressed() {
        if (pwindo!=null){
            pwindo.dismiss();
            pwindo= null;
        }else {
            super.onBackPressed();
        }
    }
}
