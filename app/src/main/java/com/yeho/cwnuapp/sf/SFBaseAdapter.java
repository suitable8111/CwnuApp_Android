package com.yeho.cwnuapp.sf;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yeho.cwnuapp.R;

import java.util.ArrayList;

/**
 * Created by KimDaeho on 15. 12. 18..
 */
public class SFBaseAdapter extends BaseAdapter{

    private LayoutInflater inflaterA = null;
    ArrayList<String> allAry = new ArrayList<>();
    public SFBaseAdapter (Context conA, ArrayList<String> allAry){
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
            convertView = this.inflaterA.inflate(R.layout.sf_cell,null);
            holder = new ViewHolder();
            holder.tvA = (TextView)convertView.findViewById(R.id.sf_cell_date_textview);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvA.setTextSize(15);

//        if (position % 2 == 0) {
//            convertView.setBackgroundColor(Color.DKGRAY);
//        }
        holder.tvA.setText(allAry.get(position));

        return convertView;
    }

    private static class ViewHolder {
        TextView tvA;
    }
}
