package com.yeho.cwnuapp.bus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yeho.cwnuapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KimDaeho on 16. 3. 6..
 */
public class BusDetailAdapter extends BaseAdapter {
    private LayoutInflater inflaterA = null;
    private ArrayList<HashMap<String,String>> allAry = null;

    public BusDetailAdapter(Context conA, ArrayList<HashMap<String,String>> allAry){
        this.inflaterA = (LayoutInflater)conA.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.allAry = allAry;
    }


    @Override
    public int getCount() {
        return allAry.size();
    }

    @Override
    public Object getItem(int position) {
        return this.allAry.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null){
            convertView = this.inflaterA.inflate(R.layout.bus_info_detail_cell,null);
            holder = new ViewHolder();
            holder.tvBusNum = (TextView)convertView.findViewById(R.id.bus_info_detail_bus_num_textView);
            holder.tvLeftStaion = (TextView)convertView.findViewById(R.id.bus_info_detail_bus_left_staion_textView);
            holder.tvLeftTime = (TextView)convertView.findViewById(R.id.bus_info_detail_bus_left_time_textView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }


        holder.tvBusNum.setText(allAry.get(position).get("ROUTE_ID").substring(5,8)+"번 버스");
        holder.tvLeftTime.setText(allAry.get(position).get("PREDICT_TRAV_TM")+"분 남음");
        holder.tvLeftStaion.setText(allAry.get(position).get("LEFT_STATION")+"정거장 남음");


        return convertView;
    }

    private static class ViewHolder {
        TextView tvBusNum; //버스번호
        TextView tvLeftTime; //남은시간
        TextView tvLeftStaion; //남은 정류장

    }
}