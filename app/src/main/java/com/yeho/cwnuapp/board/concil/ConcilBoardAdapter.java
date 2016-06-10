package com.yeho.cwnuapp.board.concil;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.usermgmt.response.model.UserProfile;
import com.yeho.cwnuapp.R;
import com.yeho.cwnuapp.RoundedAvatarDrawable;

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
 * Created by KimDaeho on 16. 1. 11..
 */
public class ConcilBoardAdapter extends BaseAdapter {
    private LayoutInflater inflaterA = null;
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();

    private ArrayList<RoundedAvatarDrawable> roundedAvatarDrawable = new ArrayList<>();
    private static boolean IS_BOARD_THREAD_DONE = false;

    public ConcilBoardAdapter(Context conA,ArrayList<HashMap<String,String>> allAry){
        this.inflaterA = (LayoutInflater)conA.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.allAry = allAry;
    }

    public void setAllAry(ArrayList<HashMap<String,String>> allAry){
        this.allAry = allAry;
        IS_BOARD_THREAD_DONE = false;
        new ThumbNailCommentThread().execute();
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
        Log.d("DONE", "Notifi");
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = this.inflaterA.inflate(R.layout.board_cell, null);
            holder = new ViewHolder();

            //holder.tvContext = (TextView)convertView.findViewById(R.id.board_cell_context_textView);
            holder.tvName = (TextView) convertView.findViewById(R.id.board_cell_name_textView);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.board_cell_title_textView);
            holder.thumbnailImg = (ImageView) convertView.findViewById(R.id.board_cell_thumbnail_imageView);
//            holder.tvCommnetCount = (TextView) convertView.findViewById(R.id.board_cell_comment_count_textView);
//            holder.tvGoodCount = (TextView) convertView.findViewById(R.id.board_cell_good_count_textView);
            holder.tvViewCount = (TextView) convertView.findViewById(R.id.board_cell_viewcount_textView);
            holder.tvPostTime = (TextView) convertView.findViewById(R.id.board_cell_posttime_textView);
            holder.btnDelete = (ImageView) convertView.findViewById(R.id.board_cell_delete_button);
            holder.btnUpdate = (ImageView) convertView.findViewById(R.id.board_cell_update_button);
//            holder.btnDelete.setOnClickListener(this);
//            holder.btnUpdate.setOnClickListener(this);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (IS_BOARD_THREAD_DONE) {
            ////////////////////////////////////////////////////////////////////
            holder.tvName.setText(allAry.get(position).get("name"));
            holder.tvTitle.setText(allAry.get(position).get("title"));

            if (allAry.get(position).get("viewcount").equals("zero")) {
                holder.tvViewCount.setText("조회수 : +0");
            } else {
                holder.tvViewCount.setText("조회수 : +"+allAry.get(position).get("viewcount"));
            }
//            if (allAry.get(position).get("goodcount").equals("null")) {
//                holder.tvGoodCount.setText("좋아요 : 0");
//            } else {
//                holder.tvGoodCount.setText("좋아요 : " + allAry.get(position).get("goodcount"));
//            }
            ////////////////////////////////////////////////////////////////////


            holder.thumbnailImg.setImageDrawable(roundedAvatarDrawable.get(position));

            holder.btnDelete.setTag(allAry.get(position).get("id"));
            holder.btnDelete.setTag(R.id.post_concil_delete_pos, position);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String  vIdTag = (String)v.getTag();
                    final int vPos = (int) v.getTag(R.id.post_concil_delete_pos);
                    AlertDialog.Builder builder = new AlertDialog.Builder(inflaterA.getContext(),R.style.MyAlertDialogStyle);
                    builder.setTitle("게시물을 삭제");
                    builder.setMessage("게시물을 삭제 하시겠습니까?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeletePost(vIdTag).execute();
                            allAry.remove(vPos);
                            notifyDataSetChanged();
                        }}).setNegativeButton("Cancel", null).show();
                }
            });

            holder.btnUpdate.setTag(allAry.get(position).get("id"));
            holder.btnUpdate.setTag(R.id.post_concil_update_title, allAry.get(position).get("title"));
            holder.btnUpdate.setTag(R.id.post_concil_update_context, allAry.get(position).get("context"));

            holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String  vIdTag = (String)v.getTag();
                    final String  vContextTag = (String)v.getTag(R.id.post_concil_update_context);
                    final String  vTitleTag = (String)v.getTag(R.id.post_concil_update_title);

                    AlertDialog.Builder builder = new AlertDialog.Builder(inflaterA.getContext(),R.style.MyAlertDialogStyle);
                    builder.setTitle("게시물을 수정");
                    builder.setMessage("게시물을 수정 하시겠습니까?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(inflaterA.getContext(),ConcilAddPostActivity.class);
                            intent.putExtra("Post","Update");
                            intent.putExtra("id",vIdTag);
                            intent.putExtra("Title",vTitleTag);
                            intent.putExtra("Context",vContextTag);
                            inflaterA.inflate(R.layout.board_cell,null).getContext().startActivity(intent);
                        }}).setNegativeButton("Cancel", null).show();
                }
            });
            ////////////////////////////////////////////////////////////////////

            holder.tvPostTime.setText(allAry.get(position).get("posttime").substring(0, 16).replace("-", "."));

            if (!String.valueOf(UserProfile.loadFromCache().getId()).equals(allAry.get(position).get("kakaoid"))) {
                holder.btnUpdate.setVisibility(View.INVISIBLE);
                holder.btnDelete.setVisibility(View.INVISIBLE);
            } else {
                holder.btnUpdate.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            }
            ////////////////////////////////////////////////////////////////////
        }

//        //add font
//        Typeface font = Typeface.createFromAsset(inflaterA.getContext().getAssets(),"font.otf.mp3");
//        holder.tvContext.setTypeface(font);
//        holder.tvName.setTypeface(font);
//        holder.tvTitle.setTypeface(font);
//        holder.tvCommnetCount.setTypeface(font);
//        holder.tvPostTime.setTypeface(font);
//        //
        return convertView;
    }


//    @Override
//    public void onClick(View v) {
//        final Integer vId = v.getId();
//        final int  vPos = (int) v.getTag(R.id.post_delete_pos);
//        final String  vIdTag = (String)v.getTag();
//        final String  vContextTag = (String)v.getTag(R.id.post_update_context);
//        final String  vTitleTag = (String)v.getTag(R.id.post_update_title);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this.inflaterA.inflate(R.layout.board_cell,null).getContext(),
//                                                                R.style.MyAlertDialogStyle);
//        builder.setTitle("파일을 수정/삭제");
//        builder.setMessage("파일을 수정/삭제 하시겠습니까?");
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (vId) {
//                    case R.id.board_cell_delete_button:
//                        new DeletePost(vIdTag).execute();
//                        allAry.remove(vPos);
//                        notifyDataSetChanged();
//                        //삭제
//                        break;
//                    case R.id.board_cell_update_button:
//                        //수정
//                        Intent intent = new Intent(inflaterA.inflate(R.layout.board_cell,null).getContext(),AddPostActivity.class);
//                        intent.putExtra("Post","Update");
//                        intent.putExtra("id",vIdTag);
//                        intent.putExtra("Title",vTitleTag);
//                        intent.putExtra("Context",vContextTag);
//                        inflaterA.inflate(R.layout.board_cell,null).getContext().startActivity(intent);
//                        break;
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//        builder.show();
//    }


    private static class ViewHolder {
        //TextView tvContext;
        TextView tvName;
        TextView tvTitle;
        //TextView tvCommnetCount;
        TextView tvPostTime;
        //TextView tvGoodCount;
        TextView tvViewCount;

        ImageView btnUpdate;
        ImageView btnDelete;
        ImageView thumbnailImg;


    }

    private class ThumbNailCommentThread extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            roundedAvatarDrawable.clear();
            for (int i = 0; i < allAry.size(); i++) {
                roundedAvatarDrawable.add(loadImage(allAry.get(i).get("kakaothumbnail")));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            IS_BOARD_THREAD_DONE = true;
            notifyDataSetChanged();
        }
    }
    private RoundedAvatarDrawable loadImage(String str) {
        InputStream inputStream = null;
        try {
            HttpGet httpRequest = new HttpGet(URI.create(str));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            inputStream = bufHttpEntity.getContent();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            RoundedAvatarDrawable roundedAvatarDrawableTmp = new RoundedAvatarDrawable(myBitmap);

            return roundedAvatarDrawableTmp;
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

    //게시물 삭제 비동기 스레드
    private class DeletePost extends AsyncTask<Void, Void, String> {

        protected String url = "http://cinavro12.cafe24.com/cwnu/board_concil/cwnu_delete_post.php";

        private String kakaoId;
        private String boardId;

        public DeletePost(String boardId){
            this.boardId = boardId;
        }
        @Override
        protected String doInBackground(Void... params) {
            UserProfile userProfile = UserProfile.loadFromCache();
            this.kakaoId = String.valueOf(userProfile.getId());
            try {
                HttpPost request = new HttpPost(url);
                Vector<NameValuePair> nameValue = new Vector<>();
                //Vector를 이용하여 서버에 전송함
                nameValue.add(new BasicNameValuePair("kakaoid", this.kakaoId));
                nameValue.add(new BasicNameValuePair("boardid", this.boardId));

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




}
