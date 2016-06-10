package com.yeho.cwnuapp.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.client.android.integration.IntentIntegrator;
import com.google.zxing.client.android.integration.IntentResult;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;
import com.yeho.cwnuapp.MainLoginActivity;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by KimDaeho on 16. 1. 6..
 */
public class UsermgmtMainActivity extends Activity {

    private  static final String THUMBNAIL_IMAGE = "thumb_path";
    private  static final String SCHOOL_NUM = "schoolnum";
    private static final int MY_PERMISSION_REQUEST_STORAGE = 3;

    private String barcodeNum = null;
    private UserProfile userProfile;
    private ProfileLayout profileLayout;
    private ExtraUserPropertyLayout extraUserPropertyLayout;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private ImageView kakaoProfileImage = null;
    private ImageView barcodeImage = null;
    private RoundedAvatarDrawable roundedAvatarDrawable = null;


    //ImageCaputre
    private int serverResponseCode = 0;
    private String fileName = "";
    private final String upLoadServerUri = "http://cinavro12.cafe24.com/cwnu/user/profile/upload_server_image.php";

    private boolean isDefault = false;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;

    /**
     * 로그인 또는 가입창에서 넘긴 유저 정보가 있다면 저장한다.
     *
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();
        initializeView();
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(UsermgmtMainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(UsermgmtMainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(UsermgmtMainActivity.this, Manifest.permission.CAMERA)
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
    @Override
    protected void onResume() {
        super.onResume();
        userProfile = UserProfile.loadFromCache();
        profileLayout.requestMe();
        if (userProfile != null)
            showProfile();
    }

    /**
     * 사용자의 정보를 변경 저장하는 API를 호출한다.
     */
//    private void onClickUpdateProfile() {
//        final Map<String, String> properties = extraUserPropertyLayout.getProperties();
//        UserManagement.requestUpdateProfile(new UsermgmtResponseCallback<Long>() {
//            @Override
//            public void onSuccess(Long result) {
//                userProfile.updateUserProfile(properties);
//                if (userProfile != null) {
//                    userProfile.saveUserToCache();
//                }
//                //KakaoToast.makeToast(getApplicationContext(), "succeeded to update user profile", Toast.LENGTH_SHORT).show();
//                Logger.d("succeeded to update user profile" + userProfile);
//                showProfile();
//            }
//
//        }, properties);
//    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                onBackPressed();
            }
        });
    }


    private void showProfile() {
        if (profileLayout != null) {
            profileLayout.setUserProfile(userProfile);
        }
    }

    private void initializeView() {

        setContentView(R.layout.layout_usermgmt_main);
        initializeButtons();
        initializeProfileView();
    }

    private void initializeButtons() {
        final Button buttonUpdatePhoto = (Button) findViewById(R.id.kakao_user_photo_change_button);
        buttonUpdatePhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Change Photo
                showSelectPhoto();
            }
        });

        final Button changeBarcodeButton = (Button) findViewById(R.id.kakao_user_change_barcode_button);
        changeBarcodeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Change Barcode
                scanBarcode();
            }
        });

        final Button logoutButton = (Button) findViewById(R.id.kakao_user_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onClickLogout();
                onBackPressed();
            }
        });
    }

    private void initializeProfileView() {
        profileLayout = (ProfileLayout) findViewById(R.id.com_kakao_user_profile);
        kakaoProfileImage = (ImageView) findViewById(R.id.kakao_user_profile_imageView);

        profileLayout.setMeResponseCallback(new MeResponseCallback() {
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

            @Override
            public void onSuccess(UserProfile result) {
                //KakaoToast.makeToast(getApplicationContext(), "succeeded to get user profile", Toast.LENGTH_SHORT).show();
                if (result != null) {
                    UsermgmtMainActivity.this.userProfile = result;
                    userProfile.saveUserToCache();
                    String barcode_data = result.loadFromCache().getProperty("schoolnum");
                    Bitmap bitmap = null;
                    barcodeImage = (ImageView) findViewById(R.id.user_school_barcode_imageView);
                    try {
                        bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 100, 30);
                        barcodeImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    new CheckUserLoged().execute("" + userProfile.getId());
                }
            }
        });

    }
    private void showSelectPhoto(){
        final CharSequence[] items = {"사진촬영","앨범 선택","이미지 없음"};
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
        kakaoProfileImage.setImageBitmap(bitmap);
        isDefault = true;


    }

    private void scanBarcode(){
        IntentIntegrator.initiateScan(UsermgmtMainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CROP_FROM_CAMERA:
                //크롭이 된 후 이미지를 넘겨받아 임시파일까지 작동하는 알고리즘
                final Bundle extras = data.getExtras();
                Bitmap photo = null;
                if (extras != null) {
                    photo = extras.getParcelable("data");
                    kakaoProfileImage.setImageBitmap(photo);
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
                    photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                    new UploadImage().execute(getRealImagePath(mImageCaptureUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
            }
            case PICK_FROM_CAMERA: {
                //카메라를 자를 크기를 정함
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX", 280);
                intent.putExtra("outputY", 280);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);
                break;
            }

            default: {
                final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                Log.d("result", result.getContents() + ":" + result.getContents());
                if (result.getContents() == null) {
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
                } else {
                    String schoolnum = "";
                    if (result.getContents().length() > 8) {
                        schoolnum = result.getContents().substring(0, 8);
                    } else {
                        schoolnum = result.getContents();
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("학생증 인식 완료!")
                            .setMessage("[" + schoolnum + "] 학번이 맞습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Bitmap bitmap = null;
                                    try {
                                        if (result.getContents().length() > 8) {
                                            bitmap = encodeAsBitmap(result.getContents().substring(0, 8), BarcodeFormat.CODE_128, 200, 40);
                                            barcodeNum = result.getContents().substring(0, 8);
                                        } else {
                                            bitmap = encodeAsBitmap(result.getContents(), BarcodeFormat.CODE_128, 200, 40);
                                            barcodeNum = result.getContents();
                                        }
                                        profileLayout.profileDescriptionSchoolNum.setText(barcodeNum);
                                        barcodeImage.setImageBitmap(bitmap);
                                        updateKakaoUser();
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scanBarcode();
                        }
                    }).show();
                }
                break;
            }
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
    private abstract class UsermgmtResponseCallback<T> extends ApiResponseCallback<T> {
        @Override
        public void onNotSignedUp() {

        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            String message = "failed to get user info. msg=" + errorResult;
            Logger.e(message);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
        }
    }

    protected void redirectSignupActivity() {
        final Intent intent = new Intent(this, KakaoSignUpActivity.class);
        startActivity(intent);
        finish();
    }

    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, MainLoginActivity.class);
        intent.putExtra("intentCate","Profile");
        startActivity(intent);
        finish();
    }
    private void updateKakaoUser(){
        final HashMap<String,String> mapA = new HashMap<>();

        if (barcodeNum!=null){
            mapA.put(SCHOOL_NUM,barcodeNum);
        }
        if (isDefault){
            mapA.put(THUMBNAIL_IMAGE,"http://cinavro12.cafe24.com/cwnu/user/profile/uploads/thumb_default.png");
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

            }

        }, properties);
    }
    private class ThumbNailThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            loadImage(userProfile.getProperties().get(THUMBNAIL_IMAGE));

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            kakaoProfileImage.setImageDrawable(roundedAvatarDrawable);
            //kakaoProfileImage.setImageBitmap(userBitmap);

        }
    }

    private void loadImage(String str) {
        InputStream inputStream = null;
        try {
            HttpGet httpRequest = new HttpGet(URI.create(str));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            inputStream = bufHttpEntity.getContent();
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

            roundedAvatarDrawable = new RoundedAvatarDrawable(myBitmap);

        } catch (Exception e) {
            e.printStackTrace();
            //return null;
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

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }


    //비동기 유저 탈퇴 스레드
//    private class DeleteUser extends AsyncTask<String, Void, String> {
//
//        protected String url = "http://cinavro12.cafe24.com/cwnu/user/cwnu_delete_user_info.php";
//
//        @Override
//        protected String doInBackground(String... params) {
//
//
//            try {
//                HttpPost request = new HttpPost(url);
//                Vector<NameValuePair> nameValue = new Vector<>();
//                //Vector를 이용하여 서버에 전송함
//                nameValue.add(new BasicNameValuePair("kakaoid", params[0]));
//
//                HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
//                request.setEntity(enty);
//
//                HttpClient client = new DefaultHttpClient();
//                HttpResponse res = client.execute(request);
//                HttpEntity entityResponse = res.getEntity();
//                InputStream im = entityResponse.getContent();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));
//
//                String total = "";
//                String tmp = "";
//                while ((tmp = reader.readLine())!=null){
//                    if (tmp!=null){
//                        total += tmp;
//                    }
//                }
//
//                im.close();
//                Log.d("SUSSEC", total);
//                return total;
//
//            } catch (UnsupportedEncodingException e){
//                e.printStackTrace();
//            } catch (IOException e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//        }
//    }

    private class CheckUserLoged extends AsyncTask<String, Void, String> {
        //업로드 url
        protected String url = "http://cinavro12.cafe24.com/cwnu/user/cwnu_check_user_info.php";

        @Override
        protected String doInBackground(String... params) {


            try {
                HttpPost request = new HttpPost(url);
                Vector<NameValuePair> nameValue = new Vector<>();
                //Vector를 이용하여 서버에 전송함

                nameValue.add(new BasicNameValuePair("kakaoid", params[0]));


                HttpEntity enty = new UrlEncodedFormEntity(nameValue, HTTP.UTF_8);
                request.setEntity(enty);

                HttpClient client = new DefaultHttpClient();
                HttpResponse res = client.execute(request);
                HttpEntity entityResponse = res.getEntity();
                InputStream im = entityResponse.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));

                String total = "";
                String tmp = "";
                while ((tmp = reader.readLine()) != null) {
                    if (tmp != null) {
                        total += tmp;
                    }
                }

                im.close();
                Log.d("SUSSEC", total);
                return total;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("Y")){
                Log.d(" 로그인 :", "성공");
                new ThumbNailThread().execute();
                showProfile();
            }else {
                Log.d(" 로그인 :","실패 인증절차 필요");
                final Intent intent = new Intent(UsermgmtMainActivity.this, CertificationActivity.class);
                startActivity(intent);
                finish();
            }

        }
    }
}



