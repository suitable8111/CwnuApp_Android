package com.yeho.cwnuapp.board.free;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
 * Created by KimDaeho on 16. 2. 15..
 */
public class CommentAdapter extends BaseAdapter {

    private LayoutInflater inflaterA = null;
    private ArrayList<HashMap<String,String>> allAry = new ArrayList<>();
    //private Bitmap userBitmap;
    private String boardId = null;
    private ArrayList<RoundedAvatarDrawable> roundedAvatarDrawable = new ArrayList<>();;
    //private ArrayList<Bitmap> userBitmap = new ArrayList<>();
    private boolean IS_COMMENT_THREAD_DONE = false;



    public void setAllAry(ArrayList<HashMap<String,String>> allAry){
        this.allAry = allAry;
        new ThumbNailThread().execute("go");
    }
    public CommentAdapter(Context conA,ArrayList<HashMap<String,String>> allAry, String boardId){
        this.inflaterA = (LayoutInflater)conA.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.allAry = allAry;
        this.boardId = boardId;
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
            convertView = this.inflaterA.inflate(R.layout.cmt_cell,null);
            holder = new ViewHolder();
            holder.tvContext = (TextView)convertView.findViewById(R.id.board_comment_cell_context_textView);
            holder.tvName = (TextView)convertView.findViewById(R.id.board_comment_cell_name_textView);
            holder.thumbnailImg = (ImageView)convertView.findViewById(R.id.board_comment_cell_thumbnail_imageView);
            holder.tvPostTime = (TextView)convertView.findViewById(R.id.board_comment_cell_posttime_textView);
            holder.btnDelete = (ImageView)convertView.findViewById(R.id.board_comment_cell_delete_button);
            holder.btnUpdate = (ImageView)convertView.findViewById(R.id.board_comment_cell_update_button);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (IS_COMMENT_THREAD_DONE) {
            holder.thumbnailImg.setImageDrawable(roundedAvatarDrawable.get(position));
            //holder.tvCommnetCount.setText(commentCount.get(position));

            holder.tvName.setText(allAry.get(position).get("name"));
            holder.tvContext.setText(allAry.get(position).get("context"));
            holder.tvPostTime.setText(allAry.get(position).get("posttime").substring(0, 16));


            if (!String.valueOf(UserProfile.loadFromCache().getId()).equals(allAry.get(position).get("kakaoid"))) {
                holder.btnUpdate.setVisibility(View.INVISIBLE);
                holder.btnDelete.setVisibility(View.INVISIBLE);
            } else {
                holder.btnUpdate.setVisibility(View.VISIBLE);
                holder.btnDelete.setVisibility(View.VISIBLE);
            }


//            holder.btnDelete.setTag(allAry.get(position).get("id"));
//            holder.btnUpdate.setTag(allAry.get(position).get("id"));
//            holder.btnUpdate.setTag(R.id.comment_update_name, allAry.get(position).get("name"));
//            holder.btnUpdate.setTag(R.id.comment_update_context, allAry.get(position).get("context"));
//
            ///////////////////////////////////////////////////////////////
            holder.btnDelete.setTag(allAry.get(position).get("id"));
            holder.btnDelete.setTag(R.id.comment_delete_pos, position);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String vIdTag = (String) v.getTag();
                    final int vPos = (int) v.getTag(R.id.comment_delete_pos);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(inflaterA.getContext(), R.style.MyAlertDialogStyle);
                    builder.setTitle("게시물을 삭제");
                    builder.setMessage("게시물을 삭제 하시겠습니까?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DeleteComment(vIdTag,boardId).execute();
                            allAry.remove(vPos);
                            notifyDataSetChanged();
                        }
                    }).setNegativeButton("Cancel", null).show();
                }
            });

            holder.btnUpdate.setTag(allAry.get(position).get("id"));
            holder.btnUpdate.setTag(R.id.comment_update_name, allAry.get(position).get("title"));
            holder.btnUpdate.setTag(R.id.comment_update_context, allAry.get(position).get("context"));

            holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String vIdTag = (String) v.getTag();
                    final String vContextTag = (String) v.getTag(R.id.comment_update_context);
                    final String vNameTag = (String) v.getTag(R.id.comment_update_name);

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(inflaterA.getContext(), R.style.MyAlertDialogStyle);
                    builder.setTitle("게시물을 수정");
                    builder.setMessage("게시물을 수정 하시겠습니까?");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(inflaterA.getContext(),AddPostActivity.class);
                            intent.putExtra("Post","UpdateComment");
                            intent.putExtra("id",vIdTag);
                            intent.putExtra("Name",vNameTag);
                            intent.putExtra("Context",vContextTag);
                            inflaterA.inflate(R.layout.cmt_cell,null).getContext().startActivity(intent);
                        }
                    }).setNegativeButton("Cancel", null).show();
                }
            });
            ///////////////////////////////////////////////////////////////

        }

        return convertView;
    }

//    @Override
//    public void onClick(View v) {
//        final Integer vId = v.getId();
//        final String  vIdTag = (String)v.getTag();
//        final String  vNameTag = (String)v.getTag(R.id.comment_update_name);
//        final String  vContextTag = (String)v.getTag(R.id.comment_update_context);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this.inflaterA.inflate(R.layout.cmt_cell,null).getContext(),
//                R.style.MyAlertDialogStyle);
//        builder.setTitle("파일을 수정/삭제");
//        builder.setMessage("파일을 수정/삭제 하시겠습니까?");
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (vId) {
//                    case R.id.board_comment_cell_delete_button:
//                        new DeleteComment(vIdTag,boardId).execute();
//                        Handler handler = new Handler(){
//                            @Override
//                            public void handleMessage(Message msg) {
//                                super.handleMessage(msg);
//                                switch (msg.what){
//                                    case 0://메시지 실패
//
//                                        break;
//                                    case 1://메시지 성공
//                                        allAry = (ArrayList<HashMap<String,String>>)msg.obj;
//                                        notifyDataSetChanged();
//                                        break;
//                                }
//                            }
//                        };
//                        //파싱 종류 ALL일 경우 모두의 정보 값을 받아 온다
//                        BoardPaserThread thread = new BoardPaserThread(handler, "COMMENT",boardId);
//                        thread.start();
//                        //서버에서 삭제는 되지만 여기서는 삭제가 안됨
//                        //삭제
//                        break;
//                    case R.id.board_comment_cell_update_button:
//                        //수정
//                        Intent intent = new Intent(inflaterA.inflate(R.layout.cmt_cell,null).getContext(),AddPostActivity.class);
//                        intent.putExtra("Post","UpdateComment");
//                        intent.putExtra("id",vIdTag);
//                        intent.putExtra("Name",vNameTag);
//                        intent.putExtra("Context",vContextTag);
//                        inflaterA.inflate(R.layout.cmt_cell,null).getContext().startActivity(intent);
//                        break;
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", null);
//        builder.show();
//    }

    private static class ViewHolder {
        TextView tvContext;
        TextView tvName;
        ImageView btnUpdate;
        ImageView btnDelete;
        ImageView thumbnailImg;
        TextView tvPostTime;

    }
    private class ThumbNailThread extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            for (int i = 0; i < allAry.size(); i++) {
                roundedAvatarDrawable.add(loadImage(allAry.get(i).get("kakaothumbnail")));
                //commentCount.add(loadCommentCount(allAry.get(i).get("id")));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            IS_COMMENT_THREAD_DONE = true;
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

    private class DeleteComment extends AsyncTask<Void, Void, String> {
        protected String url = "http://cinavro12.cafe24.com/cwnu/board/comment/cwnu_delete_comment.php";

        private String kakaoId;
        private String id;
        private String boardid;

        public DeleteComment(String id, String boardid){
            this.id = id;
            this.boardid = boardid;
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
                nameValue.add(new BasicNameValuePair("id", this.id));
                nameValue.add(new BasicNameValuePair("boardid", this.boardid));

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
