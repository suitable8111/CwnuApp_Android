package com.yeho.cwnuapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KimDaeho on 16. 5. 21..
 */
public class MainNoticeAdapter extends BaseAdapter {
    private LayoutInflater inflaterA = null;
    ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    public MainNoticeAdapter (Context conA, ArrayList<HashMap<String,String>> allAry){
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
            convertView = this.inflaterA.inflate(R.layout.main_nt_cell,null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView)convertView.findViewById(R.id.main_notice_title_textView);
            holder.tvPostTime = (TextView)convertView.findViewById(R.id.main_notice_posttime_textView);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTitle.setText(allAry.get(position).get("title").trim());
        holder.tvPostTime.setText(allAry.get(position).get("posttime").substring(0, 16).trim());

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvPostTime;
    }
}