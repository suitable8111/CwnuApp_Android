package com.yeho.cwnuapp.bf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yeho.cwnuapp.R;

import java.util.HashMap;

/**
 * Created by KimDaeho on 16. 3. 26..
 */
public class BestFoodDetailAboutFragment extends Fragment {

    private HashMap<String,String> mapA = null;

    public static BestFoodDetailAboutFragment newInstance(int page){
        BestFoodDetailAboutFragment bestFoodDetailAboutFragment = new BestFoodDetailAboutFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        bestFoodDetailAboutFragment.setArguments(args);

        return bestFoodDetailAboutFragment;
    }
    public void setMapA(HashMap<String,String> mapA){
        this.mapA = mapA;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_best_food_detail_abuot, container, false);
        //타입 전화번호 수용인원 내용 주소
        TextView tvType = (TextView)view.findViewById(R.id.best_food_detail_type_textview);
        TextView tvPhone = (TextView)view.findViewById(R.id.best_food_detail_phone_textview);
        TextView tvCapacity = (TextView)view.findViewById(R.id.best_food_detail_capacity_textview);
        TextView tvContext = (TextView)view.findViewById(R.id.best_food_detail_context_textview);
        TextView tvOperation = (TextView)view.findViewById(R.id.best_food_detail_operate_textview);

        tvType.setText(returnType(mapA.get("type")));
        tvPhone.setText(mapA.get("phone"));
        tvCapacity.setText(mapA.get("capacity"));
        tvContext.setText(mapA.get("context"));
        tvOperation.setText(mapA.get("opentime"));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private String returnType(String type){
        String typeKr = "";

        switch (type){
            case "CAFETERIA":
                typeKr = "식당";
                return typeKr;
            case "DELIVERY":
                typeKr = "배달";
                return typeKr;
            case "COFFEE":
                typeKr = "커피";
                return typeKr;
            case "BAR":
                typeKr = "주점";
                return typeKr;
            default:
                return typeKr;
        }
    }
}
