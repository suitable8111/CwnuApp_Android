package com.yeho.cwnuapp.bf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.yeho.cwnuapp.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class BestFoodMenuImageDetailActivity extends Activity {

    private ProgressDialog mProgressDialog;
    private ImageView menuImageView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_food_menu_image_detail);
        mProgressDialog = ProgressDialog.show(this, "", "잠시만 기다려 주세요.",true);
        menuImageView = (ImageView)findViewById(R.id.best_food_detail_menu_detail_imageView);
        Bundle bundle = getIntent().getExtras();
        new ImageLoadThread().execute(bundle.getString("menuImg"));
    }
    private class ImageLoadThread extends AsyncTask<String,Void,Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {
            return loadImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            if (s != null) {
                menuImageView.setImageBitmap(s);
            }else {
                Toast.makeText(getApplicationContext(), "이미지를 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
            mProgressDialog.dismiss();
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
}
