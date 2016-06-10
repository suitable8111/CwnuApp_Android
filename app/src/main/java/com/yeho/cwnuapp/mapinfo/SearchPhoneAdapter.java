package com.yeho.cwnuapp.mapinfo;

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
 * Created by KimDaeho on 16. 3. 19..
 */
public class SearchPhoneAdapter extends BaseAdapter {
    private LayoutInflater inflaterA = null;
    ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    public SearchPhoneAdapter (Context conA, ArrayList<HashMap<String,String>> allAry){
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
            convertView = this.inflaterA.inflate(R.layout.sp_cell,null);
            holder = new ViewHolder();
            holder.tvTitle = (TextView)convertView.findViewById(R.id.sp_cell_textView_title);
            holder.tvPhone = (TextView)convertView.findViewById(R.id.sp_cell_textView_phone);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvTitle.setTextSize(15);

//        if (position % 2 == 0) {
//            convertView.setBackgroundColor(Color.DKGRAY);
//        }
        holder.tvTitle.setText(allAry.get(position).get("name"));
        holder.tvPhone.setText(allAry.get(position).get("phone"));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvPhone;
    }
}
