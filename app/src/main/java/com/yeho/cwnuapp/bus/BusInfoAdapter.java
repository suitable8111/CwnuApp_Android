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
public class BusInfoAdapter extends BaseAdapter {
    private LayoutInflater inflaterA = null;
    private ArrayList<HashMap<String,String>> allAry = null;

    public BusInfoAdapter(Context conA, ArrayList<HashMap<String,String>> allAry){
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
            convertView = this.inflaterA.inflate(R.layout.bus_info_cell,null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView)convertView.findViewById(R.id.bus_info_cell_title_textview);
            holder.tvDetail = (TextView)convertView.findViewById(R.id.bus_info_cell_bus_pos_textview);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTitle.setText(allAry.get(position).get("station_name"));
        holder.tvDetail.setText(">>" + allAry.get(position).get("station_detail"));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvDetail;
    }
}
