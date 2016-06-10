package com.yeho.cwnuapp.bf;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yeho.cwnuapp.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by KimDaeho on 16. 3. 21..
 */
public class BestFoodListAdapter extends BaseAdapter {

    private ProgressDialog mProgressDialog;
    private ArrayList<Bitmap> BitmapAry = new ArrayList<>();
    private boolean IS_BOARD_THREAD_DONE = false;
    private SharedPreferences prefGood = null;
    //private SharedPreferences prefBad = null;


    private LayoutInflater inflaterA = null;
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();

    public BestFoodListAdapter (Context conA, ArrayList<HashMap<String,String>> allAry){
        this.inflaterA = (LayoutInflater)conA.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.allAry = allAry;
        new ImageLoadThread().execute();
        prefGood = conA.getSharedPreferences("isGoodBestFoods", Context.MODE_PRIVATE);
        mProgressDialog = ProgressDialog.show(inflaterA.getContext(), "", "자료를 받아오는 중입니다", true);

        //prefBad = conA.getSharedPreferences("isBadBestFoods", Context.MODE_PRIVATE);
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
            convertView = this.inflaterA.inflate(R.layout.bf_cell,null);
            holder = new ViewHolder();
            holder.imgBack = (ImageView)convertView.findViewById(R.id.bf_cell_imageView);

            //holder.btnBad = (ImageView)convertView.findViewById(R.id.bf_cell_bad_button);
            holder.btnGood = (ImageView)convertView.findViewById(R.id.bf_cell_good_button);
            holder.btnPhone = (ImageView)convertView.findViewById(R.id.bf_cell_phoen_button);

            holder.tvTitle = (TextView)convertView.findViewById(R.id.bf_cell_title_textview);
            holder.tvType = (TextView)convertView.findViewById(R.id.bf_cell_type_textview);
            holder.tvGood = (TextView)convertView.findViewById(R.id.bf_cell_good_count_textview);
            //holder.tvBad = (TextView)convertView.findViewById(R.id.bf_cell_bad_count_textview);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }

        //holder.btnPhone.setText(allAry.get(position).get("phone"));

        if (IS_BOARD_THREAD_DONE) {
            holder.tvTitle.setText(allAry.get(position).get("name"));
            holder.tvType.setText(returnType(allAry.get(position).get("type")));
            holder.tvGood.setText(allAry.get(position).get("goodcount"));
            //holder.tvBad.setText(allAry.get(position).get("badcount"));

            holder.btnGood.setTag(allAry.get(position).get("id"));

            //holder.btnBad.setTag(allAry.get(position).get("id"));

            holder.btnPhone.setTag(R.id.best_food_phone,allAry.get(position).get("phone"));

            holder.imgBack.setImageBitmap(BitmapAry.get(position));
            //holder.storeImg.setImageBitmap(bitmapAry.get(position));
            boolean isGood = prefGood.getBoolean(allAry.get(position).get("id"), false);
            //boolean isBad = prefBad.getBoolean(allAry.get(position).get("id"), false);
            Log.d("NO :", ""+isGood);
            if (isGood){
                holder.btnGood.setBackgroundResource(R.drawable.good_btn);
            }else {
                holder.btnGood.setBackgroundResource(R.drawable.not_good_btn);
            }
//            if (isBad){
//                holder.btnBad.setBackgroundResource(R.drawable.good_btn);
//            }else {
//                holder.btnBad.setBackgroundResource(R.drawable.not_good_btn);
//            }

            holder.btnGood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isGood = prefGood.getBoolean(v.getTag().toString(), false);
                    SharedPreferences.Editor nEditor = prefGood.edit();
                    Log.d("NO :", ""+ v.getTag().toString() + " : " + isGood);
                    mProgressDialog = ProgressDialog.show(inflaterA.getContext(),"", "서버로 전송 중입니다.",true);
                    if (!isGood){
                        v.setBackgroundResource(R.drawable.good_btn);
                        nEditor.putBoolean(v.getTag().toString(), true);
                        nEditor.commit();
                        new UploadGoodBad().execute("GOOD", v.getTag().toString());
                        paserFoods(allAry.get(0).get("type"));
                    }else {
                        v.setBackgroundResource(R.drawable.not_good_btn);
                        nEditor.putBoolean(v.getTag().toString(), false);
                        nEditor.commit();
                        new UploadGoodBad().execute("NOTGOOD", v.getTag().toString());
                        paserFoods(allAry.get(0).get("type"));
                    }
                }
            });
//            holder.btnBad.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    boolean isBad = prefBad.getBoolean(v.getTag().toString(), false);
//                    SharedPreferences.Editor nEditor = prefBad.edit();
//                    Log.d("NO :", "" + v.getTag().toString() + " : " + isBad);
//                    if (!isBad){
//                        v.setBackgroundResource(R.drawable.good_btn);
//                        nEditor.putBoolean(v.getTag().toString(), true);
//                        nEditor.commit();
//                        new UploadGoodBad().execute("BAD", v.getTag().toString());
//                        paserFoods("ALL");
//                    }else {
//                        v.setBackgroundResource(R.drawable.not_good_btn);
//                        nEditor.putBoolean(v.getTag().toString(), false);
//                        nEditor.commit();
//                        new UploadGoodBad().execute("NOTBAD",v.getTag().toString());
//                        paserFoods("ALL");
//                    }
//                }
//            });

            holder.btnPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag(R.id.best_food_phone).equals("없음")){
                        Toast.makeText(inflaterA.getContext(), "전화가 불가능 합니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + v.getTag(R.id.best_food_phone)));
                        inflaterA.getContext().startActivity(intent);
                    }

                }
            });
        }

        return convertView;
    }

    private static class ViewHolder {

        TextView tvTitle;
        TextView tvType;
        TextView tvGood;
        //TextView tvBad;

        //ImageView btnBad;
        ImageView btnGood;
        ImageView btnPhone;

       ImageView imgBack;

    }

    private class ImageLoadThread extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            BitmapAry.clear();
            for (int i = 0; i < allAry.size(); i++) {
                BitmapAry.add(loadImage(allAry.get(i).get("outdoorimagepath")));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            IS_BOARD_THREAD_DONE = true;
            mProgressDialog.dismiss();
            notifyDataSetChanged();
        }
    }
    private Bitmap loadImage(String str) {
        InputStream inputStream = null;
        try {
            HttpGet httpRequest = new HttpGet(URI.create(str));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            inputStream = bufHttpEntity.getContent();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

//            Drawable drawable = new BitmapDrawable(myBitmap);

            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            //close input
            if (inputStream != null) {
                try {
                    inputStream.close();

                } catch (IOException ioex) {
                    // Handle error
                }
            }
        }
    }

    //좋아요 나빠요 비동기 스레드
    private class UploadGoodBad extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String url = null;

            switch (params[0]){
                case "GOOD" :
                    url = "http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_good.php";
                    break;
                case "NOTGOOD" :
                    url = "http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_good_cancel.php";
                    break;
                case "BAD" :
                    url = "http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_bad.php";
                    break;
                case "NOTBAD" :
                    url = "http://cinavro12.cafe24.com/cwnu/bestfood/cwnu_bestfood_bad_cancel.php";
                    break;

            }
            try {
                HttpPost request = new HttpPost(url);
                Vector<NameValuePair> nameValue = new Vector<>();
                //Vector를 이용하여 서버에 전송함
                nameValue.add(new BasicNameValuePair("id", params[1]));

                HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
                request.setEntity(enty);

                HttpClient client = new DefaultHttpClient();
                HttpResponse res = client.execute(request);
                HttpEntity entityResponse = res.getEntity();
                InputStream im = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));

                String total = "";
                String tmp = "";
                while ((tmp = reader.readLine())!=null){
                    if (tmp!=null){
                        total += tmp;
                    }
                }

                im.close();
                Log.d("SUSSEC", total);
                return total;

            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private void paserFoods(String searchText){

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0://메시지 실패
                        Toast.makeText(inflaterA.getContext(), "에러발생", Toast.LENGTH_SHORT).show();
                        break;
                    case 1://메시지 성공
                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
                        notifyDataSetChanged();
                        break;
                }
                mProgressDialog.dismiss();
            }
        };
        //파싱 종류 ALL일 경우 모두의 정보 값을 받아 온다
        PaserBFThread thread = new PaserBFThread(handler, searchText);
        thread.start();

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



