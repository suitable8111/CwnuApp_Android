package com.yeho.cwnuapp.bus;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.yeho.cwnuapp.BaseFragment;
import com.yeho.cwnuapp.R;

public class TrafficInfoActivity extends BaseFragment {

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_info);
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager_trafiic);
        adapterViewPager = new MyTrafficPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs_traffic);
        tabLayout.setupWithViewPager(viewPager);



    }

    private static class MyTrafficPagerAdapter extends FragmentPagerAdapter {
        private final static int NUM_ITEMS = 3;

        BusInfoActivity busInfoActivity = null;
        TrainActivity trainActivity = null;
        SchoolBusActivity schoolBusActivity = null;

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;

        }

        public MyTrafficPagerAdapter(FragmentManager fragmentManager){
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
                    return "버스정보";
                case 1:
                    return "교내버스";
                case 2:
                    return "기차정보";
                default:
                    return null;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (busInfoActivity == null){
                        busInfoActivity = BusInfoActivity.newInstance(0);
                        return busInfoActivity;
                    }else {
                        return busInfoActivity;
                    }
                case 2:
                    if (trainActivity == null){
                        trainActivity = TrainActivity.newInstance(1);
                        return trainActivity;
                    }else {
                        return trainActivity;
                    }

                case 1:
                    if (schoolBusActivity == null){
                        schoolBusActivity = SchoolBusActivity.newInstance(2);
                        return schoolBusActivity;
                    }else {
                        return schoolBusActivity;
                    }
                default:
                    return null;
            }
        }
    }
}
