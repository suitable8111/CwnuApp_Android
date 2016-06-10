package com.yeho.cwnuapp.bf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yeho.cwnuapp.R;

/**
 * Created by KimDaeho on 16. 3. 26..
 */
public class BestFoodDetailImageFragment extends Fragment {
    public static BestFoodDetailImageFragment newInstance(int page){
        BestFoodDetailImageFragment bestFoodDetailImageFragment = new BestFoodDetailImageFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        bestFoodDetailImageFragment.setArguments(args);

        return bestFoodDetailImageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_best_food_detail_image, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
