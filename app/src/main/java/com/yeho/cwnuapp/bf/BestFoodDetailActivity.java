package com.yeho.cwnuapp.bf;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.yeho.cwnuapp.BaseFragment;
import com.yeho.cwnuapp.R;
import com.yeho.cwnuapp.circle.CircleActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

public class BestFoodDetailActivity extends BaseFragment {

    private static final int MY_PERMISSION_REQUEST_LOCATION = 3;

    private ProgressDialog mProgressDialog;
    private HashMap<String,String> mapA = null;
    private FragmentPagerAdapter adapterViewPager;

    private ViewFlipper viewFlipper = null;
    private TextView titleTextView = null;
    //private ToggleButton toggleButton = null;
    private ImageView indoorStoreImage = null;
    private ImageView outdoorStoreImage = null;


    private Button menuButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_food_detail);
        checkPermission();
        Bundle bundle = getIntent().getExtras();
        mapA = (HashMap<String,String>)bundle.getSerializable("bestFoodMapA");

        mProgressDialog = ProgressDialog.show(BestFoodDetailActivity.this,"", "잠시만 기다려 주세요.",true);
        titleTextView = (TextView)findViewById(R.id.best_food_detail_title);
        outdoorStoreImage = (ImageView)findViewById(R.id.best_food_detail_out_door_imageView);
        indoorStoreImage = (ImageView)findViewById(R.id.best_food_detail_in_door_imageView);
        menuButton = (Button)findViewById(R.id.best_food_detail_menu_Button);

        titleTextView.setText(mapA.get("name"));
        ViewPager viewPager = (ViewPager)findViewById(R.id.pager_best_food);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.sliding_tabs_best_food);

        adapterViewPager = new MyBestFoodAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);
        tabLayout.setupWithViewPager(viewPager);

        viewFlipper = (ViewFlipper)findViewById(R.id.best_food_detail_viewFlipper);

        Animation showIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        viewFlipper.setInAnimation(showIn);

        viewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

        viewFlipper.startFlipping();

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDD = new Intent(BestFoodDetailActivity.this, BestFoodMenuImageDetailActivity.class);
                intentDD.putExtra("menuImg",mapA.get("menuimagepath"));
                startActivity(intentDD);

            }
        });




//        titleText = (TextView)findViewById(R.id.best_food_detail_title);
//        typeText = (TextView)findViewById(R.id.best_food_detail_type_textView);
//        addressText = (TextView)findViewById(R.id.best_food_detail_address_textView);
//        menuText = (TextView)findViewById(R.id.best_food_detail_menu_textView);
//        phoneButton = (Button)findViewById(R.id.best_food_detail_phone_Button);
//        goodText = (TextView)findViewById(R.id.best_food_detail_good_textView);
//
//        titleText.setText(mapA.get("name"));
//        typeText.setText(mapA.get("type"));
//        addressText.setText(mapA.get("location"));
//        menuText.setText(mapA.get("menu"));
//        goodText.setText("좋아요 수 : "+mapA.get("goodcount"));
//
//        phoneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mapA.get("phone").equals("없음")) {
//                    Toast.makeText(BestFoodDetailActivity.this, "전화가 불가능 합니다.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mapA.get("phone")));
//                    startActivity(intent);
//                }
//            }
//        });

        new ImageLoadThread().execute(mapA.get("outdoorimagepath"),mapA.get("innerimagepath"));

    }

    private class ImageLoadThread extends AsyncTask<String,Void,Void> {

        Bitmap[] imageBitmaps = new Bitmap[2];

        @Override
        protected Void doInBackground(String... params) {
            for (int i = 0; i < imageBitmaps.length; i++){
                imageBitmaps[i] = loadImage(params[i]);
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if (imageBitmaps[0] == null || imageBitmaps[1] == null){
                Toast.makeText(getApplicationContext(), "이미지를 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }else {
                outdoorStoreImage.setImageBitmap(imageBitmaps[0]);
                indoorStoreImage.setImageBitmap(imageBitmaps[1]);
            }
            mProgressDialog.dismiss();
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


            return myBitmap;
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

    private class MyBestFoodAdapter extends FragmentPagerAdapter {
        private final static int NUM_ITEMS = 2;

        BestFoodDetailAboutFragment bestFoodDetailAboutFragment = null;
//        BestFoodDetailImageFragment bestFoodDetailImageFragment = null;
        BestFoodDetailMapInfoFragment bestFoodDetailMapInfoFragment = null;

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;

        }

        public MyBestFoodAdapter(FragmentManager fragmentManager){
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
                    return "정보";
                case 1:
                    return "위치";
                default:
                    return null;
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    if (bestFoodDetailAboutFragment == null){
                        bestFoodDetailAboutFragment = BestFoodDetailAboutFragment.newInstance(0);
                        bestFoodDetailAboutFragment.setMapA(mapA);
                        return bestFoodDetailAboutFragment;
                    }else {
                        return bestFoodDetailAboutFragment;
                    }
                case 1:
                    if (bestFoodDetailMapInfoFragment == null){
                        bestFoodDetailMapInfoFragment = BestFoodDetailMapInfoFragment.newInstance(2);
                        bestFoodDetailMapInfoFragment.setLatLng(mapA.get("lat"),mapA.get("long"),mapA.get("name"));
                        return bestFoodDetailMapInfoFragment;
                    }else {
                        return bestFoodDetailMapInfoFragment;
                    }
                default:
                    return null;
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(BestFoodDetailActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(BestFoodDetailActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explain to the user why we need to write the permission.
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE},
                    MY_PERMISSION_REQUEST_LOCATION);
            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant
        } else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION:
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
