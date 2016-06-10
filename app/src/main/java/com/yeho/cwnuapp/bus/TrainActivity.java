package com.yeho.cwnuapp.bus;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yeho.cwnuapp.R;

public class TrainActivity extends Fragment {

    public static TrainActivity newInstance(int page){
        TrainActivity trainActivity = new TrainActivity();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        trainActivity.setArguments(args);

        return trainActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_train, container, false);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
