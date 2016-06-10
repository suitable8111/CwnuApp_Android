package com.yeho.cwnuapp.mapinfo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yeho.cwnuapp.BaseFragment;
import com.yeho.cwnuapp.R;


import java.util.ArrayList;
import java.util.HashMap;

//Google 맵과 구내 전화번호를 연동한 Activity
public class MapInfoActivity extends BaseFragment implements GoogleMap.OnMapClickListener, AdapterView.OnItemClickListener {

    private ProgressDialog mProgressDialog;

    private static final int MY_PERMISSION_REQUEST_LOCATION = 3;
    //ListView 관련//
    private ArrayList<HashMap<String, String>> allAry = new ArrayList<>();
    private ArrayList<HashMap<String, String>> coordAry = new ArrayList<>();
    private MIBaseAdapter adapterMapInfo = null;
    private ListView mapInfoListView = null;
    private Button mapInfoFindMeBtn = null;
    private boolean isListViewUp = true;
    ////////////////

    private TypedArray drawables = null;
    //전화번호 검색 팝업 관련....
    private PopupWindow pwindo;
    private int mWidthPixels, mHeightPixels;
    ////////////////


    //Search 관련//
    EditText searchText = null;

    /////////////

    //Google Map관련//

    private SupportMapFragment fragment;
    private GoogleMap googleMap;
    private ArrayList<Marker>  marker = new ArrayList<>();
    private Marker myMarker = null;
    private LocationManager locationManager;
    //    private ArrayList<Coordn>   coordAry = new ArrayList<Coordn>();
    private LatLng coordinates;
    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;

    // 네트워크 사용유무
    boolean isNetworkEnabled = false;

    // GPS 상태값
    boolean isGetLocation = false;
    // GPS 정보 업데이트 거리 10미터
    private static final long MIN_DISTANCE_UPDATES = 10;

    // GPS 정보 업데이트 시간 1/1000
    private static final long MIN_TIME_UPDATES = 1000 * 60 * 1;

    Location location;
//    private Coordn              coordn;
    ////////////////

    //서버에 최신버전으로 불러오게 하는 버튼
    //private Button updateBtn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_info);
        checkPermission();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.activity_map_info_sub, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        addContentView(linearLayout, params);

        drawables = getResources().obtainTypedArray(R.array.mark_structure_res);
        makeLayoutSize();


        adapterMapInfo = new MIBaseAdapter(this);
        mapInfoListView = (ListView) findViewById(R.id.mapinfo_listView);
        mapInfoFindMeBtn = (Button) findViewById(R.id.mapinfo_find_me_button);
        //updateBtn = (Button) findViewById(R.id.mapinfo_button);
        mapInfoListView.setAdapter(adapterMapInfo);
        mapInfoListView.setOnItemClickListener(this);
        anmiDwon();
        //Search Things//
        searchText = (EditText) findViewById(R.id.mapinfo_search_text);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Toast.makeText(getApplicationContext(), "검색중 입니다..", Toast.LENGTH_LONG).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);


                        Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                //Handler 성공시 --> 1 실패시 --> 0
                                switch (msg.what) {
                                    case 1:

                                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                                        adapterMapInfo.setAry(allAry);
                                        adapterMapInfo.notifyDataSetChanged();
                                        anmiUp();
                                        searchText.setText("");

                                        break;
                                    case 0:
                                        anmiDwon();
                                        Toast.makeText(getApplicationContext(), "찾을 수 없습니다.. ", Toast.LENGTH_LONG).show();
                                        searchText.setText("");
                                        break;
                                    default:
                                        Log.i("msg.what (실패) :", "" + msg.what);
                                        break;
                                }
                            }
                        };

                        ConnThread cnThread = new ConnThread(handler,"SEARCH",searchText.getText().toString());
                        cnThread.start();
                        return true;

                    default:

                        return false;
                }
            }
        });
        mapInfoFindMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findMyLocation();
            }
        });
        /////////////////

        ////Google Map////

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapinfo_fragment);
        googleMap = fragment.getMap();
        googleMap.setOnMapClickListener(this);
        setMarker();
        //updateBtn.setOnClickListener(this);

    }
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
    private void initiatePopupWindow(String checkRoom){
        try {
            //  LayoutInflater 객체와 시킴
            LayoutInflater inflaterPop = (LayoutInflater) MapInfoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflaterPop.inflate(R.layout.activity_map_info_search_phone_pop_up, (ViewGroup)findViewById(R.id.map_info_search_phone_pop_up));

            final ListView searchListView = (ListView)layout.findViewById(R.id.map_info_search_phone_ListView);
            final Button cancelBtn = (Button)layout.findViewById(R.id.map_info_search_phone_cancel_button);
            final TextView countTextView = (TextView)layout.findViewById(R.id.map_info_search_phone_count_textView);

            pwindo = new PopupWindow(layout, mWidthPixels, mHeightPixels,true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pwindo.setFocusable(false);


            mProgressDialog = ProgressDialog.show(MapInfoActivity.this,"", "해당 전화번호를 찾는 중입니다..",true);
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //Handler 성공시 --> 1 실패시 --> 0
                    switch (msg.what) {
                        case 1:

                            final ArrayList<HashMap<String,String>> searchPhoneAry = (ArrayList<HashMap<String, String>>)msg.obj;
                            final SearchPhoneAdapter searchPhoneAdapter = new SearchPhoneAdapter(layout.getContext(),searchPhoneAry);
                            countTextView.setText("총 "+searchPhoneAry.size()+"건의 전화번호가 검색 되었습니다.");
                            searchListView.setAdapter(searchPhoneAdapter);
                            searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+searchPhoneAry.get(position).get("phone")));
                                    startActivity(intent);
                                }
                            });

                            break;

                        default:
                            Toast.makeText(getApplicationContext(), "전화번호 검색 실패..", Toast.LENGTH_SHORT).show();

                            break;
                    }
                    mProgressDialog.dismiss();
                }
            };
            SearchPhoneThread spThread = new SearchPhoneThread(handler,checkRoom);
            spThread.start();

            cancelBtn.setOnClickListener(new View.OnClickListener() {
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

    private void setMarker(){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //Handler 성공시 --> 1 실패시 --> 0
                switch (msg.what) {
                    case 1:
                        coordAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        for (int i = 0; i<coordAry.size(); i++) {
                            double lat = Double.parseDouble(coordAry.get(i).get("lat"));
                            double lng = Double.parseDouble(coordAry.get(i).get("lng"));
                            coordinates = new LatLng(lat,lng);
                            Marker markerA = googleMap.addMarker(new MarkerOptions()
                                    .position(coordinates)
                                    .title(coordAry.get(i).get("structure_name"))
                                    .icon(BitmapDescriptorFactory.fromResource(drawables.getResourceId(i,-1))));
                            marker.add(markerA);
                        }
                        break;

                    default:

                        Log.i("msg.what (실패) :", "" + msg.what);
                        break;
                }
            }
        };
        CoordThread cdThread = new CoordThread(handler);
        cdThread.start();

        coordinates = new LatLng(35.246374,128.692472);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 30));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 250, null);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String name = marker.getTitle();
                if (!name.equals("내위치")) {
                    String num = "";
                    for (int i = 0; i < coordAry.size(); i++) {
                        if (coordAry.get(i).get("structure_name").equals(name)) {
                            num = coordAry.get(i).get("structure_num");
                            break;
                        }
                    }
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            //Handler 성공시 --> 1 실패시 --> 0
                            switch (msg.what) {
                                case 1:
                                    allAry = (ArrayList<HashMap<String, String>>) msg.obj;
                                    adapterMapInfo.setAry(allAry);
                                    adapterMapInfo.notifyDataSetChanged();
                                    anmiUp();
                                    break;
                                case 0:
                                    anmiDwon();
                                    Toast.makeText(getApplicationContext(), "찾을 수 없습니다.. ", Toast.LENGTH_LONG).show();
                                    break;

                                default:
                                    Log.i("msg.what (실패) :", "" + msg.what);
                                    break;
                            }
                        }
                    };
                    ConnThread cnThread = new ConnThread(handler, "NUM", num);
                    cnThread.start();
                }

                return false;
            }
        });
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.mapinfo_button) {
//            Handler handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    //Handler 성공시 --> 1 실패시 --> 0
//                    switch (msg.what) {
//                        case 1:
//                            allAry = (ArrayList<HashMap<String, String>>) msg.obj;
//                            adapterMapInfo.setAry(allAry);
//                            adapterMapInfo.notifyDataSetChanged();
//                            Toast.makeText(getApplicationContext(), "갱신 되었습니다 ", Toast.LENGTH_LONG).show();
//                            break;
//                        default:
//                            Toast.makeText(getApplicationContext(), "갱신에 실패하였습니다.. ", Toast.LENGTH_LONG).show();
//                            Log.i("msg.what (실패) :", "" + msg.what);
//                            break;
//                    }
//                }
//            };
//            ConnThread cnThread = new ConnThread(handler, "ALL");
//            cnThread.start();
//        }
//    }
    @Override
    public void onMapClick(LatLng latLng) {
        anmiDwon();
    }

    @Override
    public void onBackPressed() {
        if (isListViewUp) {
            anmiDwon();
        }else {
            super.onBackPressed();
        }
    }

    private void anmiDwon(){
        if (isListViewUp) {
            Animation anim = new TranslateAnimation(0,0,0,798);
            anim.setDuration(500);
            mapInfoListView.startAnimation(anim);
            isListViewUp = false;
            mapInfoListView.setVisibility(View.GONE);
        }
    }

    private void anmiUp(){
        if (!isListViewUp) {
            mapInfoListView.setVisibility(View.VISIBLE);
            Animation anim = new TranslateAnimation(0,0,798,0);
            anim.setFillAfter(true);
            anim.setDuration(500);
            mapInfoListView.startAnimation(anim);
            isListViewUp = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        for (int i = 0; i < coordAry.size(); i++){
            String coorBuildNum = coordAry.get(i).get("structure_num");
            String searchedBuildNum = allAry.get(position).get("building_num").replace("\"", "");
            if (coorBuildNum.equals(searchedBuildNum)){
                double lat = Double.parseDouble(coordAry.get(i).get("lat"));
                double lng = Double.parseDouble(coordAry.get(i).get("lng"));
                coordinates = new LatLng(lat, lng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                marker.get(i).showInfoWindow();

                initiatePopupWindow(allAry.get(position).get("room_name").replace("\"", ""));

                break;
            }
        }

//        final int postionNum = position;
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(MapInfoActivity.this, R.style.MyAlertDialogStyle);
//        builder.setTitle("전화걸기");
//        builder.setMessage(allAry.get(position).get("small_cate")+"(055-213-" + allAry.get(postionNum).get("id_number")+")로 전화를 거시겠습니까?");
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:055-213"+allAry.get(postionNum).get("id_number")));
//                startActivity(intent);
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//        builder.show();

    }
    private void findMyLocation(){
        GpsInfo gps = new GpsInfo(MapInfoActivity.this);
        if (gps.isGetLocation()){
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            coordinates = new LatLng(latitude, longitude);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            if (myMarker!=null){
                myMarker.remove();
            }

            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title("내위치")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue_mark)));
            myMarker.showInfoWindow();

        }else{
            gps.showSettingsAlert();
        }


    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MapInfoActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MapInfoActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MapInfoActivity.this,Manifest.permission.CALL_PHONE)
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
