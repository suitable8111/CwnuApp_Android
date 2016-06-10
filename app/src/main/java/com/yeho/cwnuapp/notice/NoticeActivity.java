package com.yeho.cwnuapp.notice;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.yeho.cwnuapp.R;

public class NoticeActivity extends FragmentActivity {


    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        ViewPager viewPager = (ViewPager)findViewById(R.id.notice_pager);

        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.notice_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private static class MyPagerAdapter extends FragmentPagerAdapter {
        private final static int NUM_ITEMS = 4;

//        SFDormitoryFragment sfDormitoryFragment = null;
//        SFBongLimFragment sfBongLimFragment = null;
//        SFSaLimFragment sfSaLimFragment = null;

        NoticeInfoFragment wagleHomeNoticeFragment = null;
        NoticeInfoFragment schoolInfoFragment = null;
        NoticeInfoFragment noticeInfoFragment = null;
        NoticeInfoFragment recruitmentFragment = null;
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
                    return "와글공지";
                case 1:
                    return "학사안내";
                case 2:
                    return "공지사항";
                case 3:
                    return "모집안내";
                default:
                    return null;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (wagleHomeNoticeFragment == null){
                        wagleHomeNoticeFragment = NoticeInfoFragment.newInstance(0);

                        return wagleHomeNoticeFragment;
                    }else {
                        return wagleHomeNoticeFragment;
                    }
                case 1:
                    if (schoolInfoFragment == null){
                        schoolInfoFragment = NoticeInfoFragment.newInstance(1);
                        return schoolInfoFragment;
                    }else {
                        return schoolInfoFragment;
                    }
                case 2:
                    if (noticeInfoFragment == null){
                        noticeInfoFragment = NoticeInfoFragment.newInstance(2);
                        return noticeInfoFragment;
                    }else {
                        return noticeInfoFragment;
                    }
                case 3:
                    if (recruitmentFragment == null){
                        recruitmentFragment = NoticeInfoFragment.newInstance(3);
                        return recruitmentFragment;
                    }else {
                        return recruitmentFragment;
                    }
                default:
                    return null;
            }
        }
    }
}
