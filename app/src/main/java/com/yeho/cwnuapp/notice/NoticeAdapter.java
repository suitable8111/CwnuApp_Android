package com.yeho.cwnuapp.notice;

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
 * Created by KimDaeho on 16. 3. 11..
 */
public class NoticeAdapter extends BaseAdapter {
    private LayoutInflater inflaterA = null;
    ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    public NoticeAdapter (Context conA, ArrayList<HashMap<String,String>> allAry){
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
            convertView = this.inflaterA.inflate(R.layout.nt_cell,null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView)convertView.findViewById(R.id.notice_title_textView);
            holder.tvName = (TextView)convertView.findViewById(R.id.notice_name_textView);
            holder.tvPostTime = (TextView)convertView.findViewById(R.id.notice_posttime_textView);
            holder.tvCount = (TextView)convertView.findViewById(R.id.notice_count_textView);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTitle.setText(allAry.get(position).get("title").trim());
        holder.tvName.setText(allAry.get(position).get("name").trim());
        holder.tvPostTime.setText(allAry.get(position).get("posttime").trim());
        holder.tvCount.setText("조회수 : "+allAry.get(position).get("count").trim());

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvName;
        TextView tvPostTime;
        TextView tvCount;
    }
}
