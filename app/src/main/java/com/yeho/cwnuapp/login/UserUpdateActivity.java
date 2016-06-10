package com.yeho.cwnuapp.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.yeho.cwnuapp.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

//닉네임 프로필을 수정할 수 있는 Activity

public class UserUpdateActivity extends Activity {

    private int serverResponseCode = 0;
    private String fileName = "";
    private final String upLoadServerUri = "http://cinavro12.cafe24.com/cwnu/user/profile/upload_server_image.php";

    private boolean isDefault = false;
    private static final int MY_PERMISSION_REQUEST_STORAGE = 3;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private  static final String NICK_NAME_KEY = "nick";
    private  static final String SCHOOL_NUM = "schoolnum";
    private  static final String THUMBNAIL_IMAGE = "thumb_path";

    private String barcodeNum = null;
    private Uri mImageCaptureUri;

    //private Bitmap toServerBitmap = null;

    private UserProfile userProfile;

    private EditText nickNameEditText = null;
    private ImageButton profileImage = null;
    private Button updateButton = null;
    private Button updateBarcodeButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);
        checkPermission();
        userProfile = UserProfile.loadFromCache();
        nickNameEditText = (EditText)findViewById(R.id.user_update_nickname_editText);
        updateButton = (Button)findViewById(R.id.user_update_confirm_button);
        updateBarcodeButton = (Button)findViewById(R.id.user_update_barcode_button);
        profileImage = (ImageButton)findViewById(R.id.user_update_profile_imageButton);
        nickNameEditText.setText(userProfile.getProperties().get("nick"));
        new DownThumbProfile().execute();

        if (userProfile.getProperty("schoolnum") != null){
            updateBarcodeButton.setText("당신의 학번은 "+userProfile.getProperty("schoolnum").toString()+"입니다");
        }
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAlert();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
            }
        });
        updateBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(UserUpdateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(UserUpdateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(UserUpdateActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA },
                    MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
        }
    }

    private void scanBarcode(){
        IntentIntegrator.initiateScan(UserUpdateActivity.this);
    }
    private void checkInput(){
        //기본적으로 별명에 데이터가 있는지 체크 여부
        if (nickNameEditText.getText().length() == 0){
            Toast.makeText(getApplicationContext(), "별명을 써주세요", Toast.LENGTH_SHORT).show();
        }else {
            //이미지를 업로드 시킨 경우, 이럴땐 서버에서 이미지를 업로드해야함
            if (mImageCaptureUri!=null){
                new UploadImage().execute(getRealImagePath(mImageCaptureUri));
                //new UploadImage().execute();
            }else {
                //단순히 별명만 바꿀경우
                updateKakaoUser();
            }
        }

    }
    private abstract class UsermgmtResponseCallback<T> extends ApiResponseCallback<T> {
        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            String message = "failed to get user info. msg=" + errorResult;
            Logger.e(message);
            //KakaoToast.makeToast(self, message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
        }
    }
    private void updateKakaoUser(){
        final HashMap<String,String> mapA = new HashMap<>();
        mapA.put(NICK_NAME_KEY,nickNameEditText.getText().toString());
        if (barcodeNum!=null){
            mapA.put(SCHOOL_NUM,barcodeNum);
        }
        if (isDefault){
            mapA.put(THUMBNAIL_IMAGE,"http://cinavro12.cafe24.com/cwnu/user/profile/uploads/thumb_story.png");
        }else if (mImageCaptureUri!=null){
            mapA.put(THUMBNAIL_IMAGE,"http://cinavro12.cafe24.com/cwnu/user/profile/uploads/"+fileName);
        }

        final Map<String,String> properties = mapA;

        UserManagement.requestUpdateProfile(new UsermgmtResponseCallback<Long>() {

            @Override
            public void onSuccess(Long result) {
                userProfile.updateUserProfile(properties);
                if (userProfile != null) {
                    userProfile.saveUserToCache();
                }
                //KakaoToast.makeToast(getApplicationContext(), "succeeded to update user profile", Toast.LENGTH_SHORT).show();
                Logger.d("succeeded to update user profile" + userProfile);
                onBackPressed();
            }

        }, properties);
    }
    //////이미지 서버 전송 관련
    ///바뀐 이미지를 우리쪽 서버에 저장을 시킨다
    ///저장된 경로를 카카오 서버의 유저 thumb_path로 변경시켜준다
    public String getRealImagePath (Uri uriPath)
    {

        String []proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uriPath, proj, null, null, null);
        if (cursor == null){
            return uriPath.getPath();
        }else {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            String path = cursor.getString(index);

            path = path.replace(".", "_change.");

            return path;
        }
    }
    private class UploadImage extends AsyncTask<String, Void, String> {


        //업로드 url
        @Override
        protected String doInBackground(String... params) {


            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
//            Log.d("PARAMS",params[0]);
            File sourceFile = new File(params[0]);

//            if (!sourceFile.exists()){
//                sourceFile = new File(params[1]);
//            }
            if (sourceFile.exists()) {

                try {
                    fileName = ""+userProfile.getId()+".png";
                    Log.d("FILENAME : ", fileName);
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", params[0]);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);


                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            updateKakaoUser();
        }
    }
    /////////////////////////////

    //////이미지 파일 업로드 관련
    //Alert 창으로 가져올 사진을 부른다
    private void callAlert(){
//        final DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                doTakePhotoAction();
//            }
//        };
//        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                doTakeAlbumAction();
//            }
//        };
//        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener(){
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                doTakeDefault();
//                dialog.dismiss();
//            }
//        };
        final CharSequence[] items = {"사진촬영","앨범 선택","이미지 없음"};

//        new AlertDialog.Builder(this)
//                .setTitle("업로드 할 이미지 선택")
//                .setPositiveButton("사진 촬영", cameraListener)
//                .setNeutralButton("앨범 선택", albumListener)
//                .setNegativeButton("이미지 없음", cancelListener)
//                .show();

            new AlertDialog.Builder(this)
                .setTitle("업로드 할 이미지 선택")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0 :
                                doTakePhotoAction();
                                break;
                            case 1 :
                                doTakeAlbumAction();
                                break;
                            case 2 :
                                doTakeDefault();
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();


    }
    //카메라에서 이미지 가져오기
    private void doTakePhotoAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
        isDefault = false;
    }
    //앨범에서 이미지 가져오기
    private void doTakeAlbumAction(){
        //앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        isDefault = false;
    }
    private void doTakeDefault(){

            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.thumb_story);
            Bitmap bitmap = drawable.getBitmap();
            profileImage.setImageBitmap(bitmap);
            isDefault = true;


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case CROP_FROM_CAMERA:
                //크롭이 된 후 이미지를 넘겨받아 임시파일까지 작동하는 알고리즘
                final Bundle extras = data.getExtras();
                Bitmap photo = null;
                if(extras != null){
                    photo = extras.getParcelable("data");
                    profileImage.setImageBitmap(photo);
                }

//                //임시 파일 저장
                //String url = "tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
                //mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), url));
                //File f = Environment.getExternalStorageDirectory();
                Log.d("MIMAGE ", getRealImagePath(mImageCaptureUri));
                File f = new File(getRealImagePath(mImageCaptureUri));
                OutputStream out = null;

                try {
                    f.createNewFile();
                    out = new FileOutputStream(f);
                    photo.compress(Bitmap.CompressFormat.PNG,100,out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case PICK_FROM_ALBUM:
            {
                mImageCaptureUri = data.getData();
            }
            case PICK_FROM_CAMERA:
            {
                //카메라를 자를 크기를 정함
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX", 280);
                intent.putExtra("outputY", 280);
                intent.putExtra("aspectX",1);
                intent.putExtra("aspectY",1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);
                break;
            }

            default:{
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                Log.d("result", result.getContents() + ":" + result.getContents());
                if (result.getContents()==null){
                    new AlertDialog.Builder(this)
                            .setTitle("학생증 인식 실패")
                            .setMessage("다시 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    scanBarcode();
                                }
                            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }else {
                    String schoolnum = "";
                    if (result.getContents().length() > 8){
                        schoolnum = result.getContents().substring(0,8);
                    }else {
                        schoolnum = result.getContents();
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("학생증 인식 완료!")
                            .setMessage("[" + schoolnum + "] 학번이 맞습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scanBarcode();
                        }
                    }).show();

                    updateBarcodeButton.setText("당신의 학번은 "+schoolnum+"입니다.");
                    barcodeNum = schoolnum;
                }
            }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "권한 설정 완료", Toast.LENGTH_SHORT).show();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Toast.makeText(this, "권한 거부", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    private class DownThumbProfile extends AsyncTask<Bitmap, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {

            return loadImage(userProfile.getProperties().get(THUMBNAIL_IMAGE));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            profileImage.setImageBitmap(bitmap);

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
