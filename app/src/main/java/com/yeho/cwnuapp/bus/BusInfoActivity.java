package com.yeho.cwnuapp.bus;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yeho.cwnuapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class BusInfoActivity extends Fragment {

    private ListView busInfoListView = null;
    private BusInfoAdapter busInfoAdapter = null;
    private ArrayList<HashMap<String,String>> allAry = null;


    public static BusInfoActivity newInstance(int page){
        BusInfoActivity busInfoActivity = new BusInfoActivity();
        Bundle args = new Bundle();
        args.putInt("someInt",page);
        busInfoActivity.setArguments(args);

        return busInfoActivity;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_bus_info, container, false);
        busInfoListView = (ListView)view.findViewById(R.id.bus_info_listview);

        allAry = new ArrayList<>();

        HashMap<String, String>  mapA = new HashMap<>();
        mapA.put("station_num", "379002287");
        mapA.put("station_name", "우영프라자");
        mapA.put("station_detail", "우영프라자 맞은편 야구장 쪽");
        allAry.add(mapA);
        mapA = new HashMap<>();
        mapA.put("station_num", "379000621");
        mapA.put("station_name", "우영프라자");
        mapA.put("station_detail", "우영프라자 입구 쪽");
        allAry.add(mapA);
        mapA = new HashMap<>();
        mapA.put("station_num", "379000610");
        mapA.put("station_name", "창원대학교입구");
        mapA.put("station_detail", "롯데리아, GS25");
        allAry.add(mapA);
        mapA = new HashMap<>();
        mapA.put("station_num", "379000612");
        mapA.put("station_name", "창원대학교입구");
        mapA.put("station_detail", "롯데리아, GS25 맞은편");
        allAry.add(mapA);
        mapA = new HashMap<>();
        mapA.put("station_num", "379000591");
        mapA.put("station_name", "창원대학교 종점");
        mapA.put("station_detail", "창원대학교 입구 쪽");
        allAry.add(mapA);
        mapA = new HashMap<>();
        mapA.put("station_num", "379003357");
        mapA.put("station_name", "창원중앙역");
        mapA.put("station_detail", "창원중앙역");
        allAry.add(mapA);
        mapA = new HashMap<>();
        mapA.put("station_num", "379002786");
        mapA.put("station_name", "창원중부방순찰대");
        mapA.put("station_detail", "창원중앙역에서 빠져나가는 쪽");
        allAry.add(mapA);

        busInfoAdapter = new BusInfoAdapter(view.getContext(), allAry);
        busInfoListView.setAdapter(busInfoAdapter);
        busInfoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentBD = new Intent(view.getContext(), BusDetailActivity.class);
                intentBD.putExtra("ST_NUM",allAry.get(position).get("station_num"));
                intentBD.putExtra("ST_NAME",allAry.get(position).get("station_name"));
                intentBD.putExtra("ST_DETAIL",allAry.get(position).get("station_detail"));
                startActivity(intentBD);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
