package com.yeho.cwnuapp.bf;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yeho.cwnuapp.R;

import java.util.HashMap;

/**
 * Created by KimDaeho on 16. 3. 26..
 */
public class BestFoodDetailMapInfoFragment extends Fragment {

    //Google Map관련//

    private Double latValue = null;
    private Double longValue = null;
    private String title = null;
    private SupportMapFragment fragment;
    private GoogleMap googleMap;
    private Marker myMarker = null;
    //private LocationManager locationManager;
    private LatLng coordinates;

    public static BestFoodDetailMapInfoFragment newInstance(int page){
        BestFoodDetailMapInfoFragment bestFoodDetailMapInfoFragment = new BestFoodDetailMapInfoFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        bestFoodDetailMapInfoFragment.setArguments(args);

        return bestFoodDetailMapInfoFragment;
    }
    public void setLatLng(String latValue, String longValue, String title){
        this.latValue = Double.valueOf(latValue) ;
        this.longValue = Double.valueOf(longValue) ;
        this.title = title;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_best_food_detail_map_info, container, false);

        //locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager myFM = getChildFragmentManager();
        fragment = (SupportMapFragment) myFM
                .findFragmentById(R.id.best_food_mapinfo_fragment);
        if (fragment == null){
            fragment = SupportMapFragment.newInstance();
            myFM.beginTransaction().replace(R.id.best_food_mapinfo_fragment,fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap == null){
            googleMap = fragment.getMap();
        }
        if (googleMap != null){
            coordinates = new LatLng(latValue,longValue);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 30));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 250, null);
            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(coordinates)
                    .title(title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_blue_mark)));
            myMarker.showInfoWindow();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
