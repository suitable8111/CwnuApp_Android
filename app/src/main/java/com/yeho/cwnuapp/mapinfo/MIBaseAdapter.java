package com.yeho.cwnuapp.mapinfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yeho.cwnuapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KimDaeho on 15. 12. 23..
 */
//Cursor를 이용하여 데이터를 출력시키는 CustomAdapter

public class MIBaseAdapter extends BaseAdapter {

    private LayoutInflater inflaterA = null;
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    //private Cursor cursor;
    //private SQLiteDatabase database;

    //초기 생성자, 기본key가 들어있는 배열, 서버에서 받아온 database를 생성함
    public MIBaseAdapter(Context conA){
        this.inflaterA = (LayoutInflater)conA.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    //서버를 갱신했을때, dataBase를 바꿔주는 메서드

    public void setAry(ArrayList<HashMap<String,String>> allAry){
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
            convertView = this.inflaterA.inflate(R.layout.mi_cell,null);
            holder = new ViewHolder();
            holder.tvDetail = (TextView)convertView.findViewById(R.id.mi_cell_textView_room_name);
            holder.tvPhone = (TextView)convertView.findViewById(R.id.mi_cell_textView_floor_num);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvDetail.setText(allAry.get(position).get("room_name").replace("\"",""));
        holder.tvPhone.setText(allAry.get(position).get("floor_num").replace("\"","")+"층");

        return convertView;
    }

    private static class ViewHolder {
        TextView tvDetail;
        TextView tvPhone;
    }
}
