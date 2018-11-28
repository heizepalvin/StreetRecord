package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.stetho.inspector.database.ContentProviderSchema;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Manifest;


/**
 * Created by soyounguensoo on 2017-08-23.
 */

public class CreateChatRoomActivity extends AppCompatActivity {

    private EditText createTitle;
    private TextView titleCount;
    private Spinner genreSelect;
    private EditText createGenre;
    private TextView genreCount;
    private Button createChatBtn;
    private Button createChatCancelBtn;
    private Button createImgBtn; // 채팅방 이미지 설정 버튼
    private ImageView createRoomImg; //채팅방 이미지

    private String userSelectGenre;

    private static final int PICK_FROM_CAMERA = 0; //카메라로 촬영해서 가져왔을 때
    private static final int PICK_FROM_ALBUM = 1; // 앨범에서 가져왔을 때
    private static final int CROP_FROM_IMAGE = 2; //CROP해서 이미지를 가져왔을 때

    private Uri mImageCaptureUri; // 임시로 사용할 파일 경로를 가져올 때 사용

    private String absolutePath;

    private File tempFile = null;

    private File croppedFileName = null;

    private File images;

    //이미지 설정 다이얼로그

    private AlertDialog alertDialog;

    // 권한 설정 변수

    private String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101; // 권한 동의 여부 문의 후 CallBack 함수에 쓰일 변수

    //로컬데이터베이스 저장

    private ChattingDatabase dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case PICK_FROM_ALBUM: {

                mImageCaptureUri = data.getData();
                Log.d("SmartWheel", mImageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA: {


                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri,"image/*");

                this.grantUriPermission("com.android.camera",mImageCaptureUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,0);
                Log.e("list.get 0 " , list.get(0).activityInfo.packageName);
                grantUriPermission(list.get(0).activityInfo.packageName, mImageCaptureUri , Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                int size = list.size();
                if(size==0){
                    Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else{

                    Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra("crop","true");
//                    intent.putExtra("outputX",100); //CROP한 이미지의 x축 크기
//                    intent.putExtra("outputY",100); //CROP한 이미지의 y축 크기
                    intent.putExtra("aspectX",3); // CROP 박스의 X축 비율
                    intent.putExtra("aspectY", 3); // CROP 박스의 Y축 비율
                    intent.putExtra("scale",true);


                    try{
                        croppedFileName = createImageFile();

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    File folder = new File(Environment.getExternalStorageDirectory()+"/StreetRecord/");
                    tempFile = new File(folder.toString(), croppedFileName.getName());

                    mImageCaptureUri = FileProvider.getUriForFile(CreateChatRoomActivity.this,"com.example.heizepalvin.streetrecord.provider", tempFile);

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    intent.putExtra("return-data",false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());


                    Intent i = new Intent(intent);
                    ResolveInfo res = list.get(0);
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(res.activityInfo.packageName, mImageCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION| Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    startActivityForResult(i,CROP_FROM_IMAGE);

                }

                break;
            }

            case CROP_FROM_IMAGE:{

              try{
                  Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mImageCaptureUri);
                  Bitmap thumbImage = ThumbnailUtils.extractThumbnail(bitmap, 200, 200);
                  createRoomImg.setImageBitmap(thumbImage);
                  getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));
              }catch (Exception e){
                  Log.e("ERROR", e.getMessage());
              }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chatroom_activity);

        createTitle = (EditText) findViewById(R.id.createChatActTitle);
        titleCount = (TextView) findViewById(R.id.createChatActTitleCount);
        genreSelect = (Spinner) findViewById(R.id.createChatActGenreSpinner);
        createGenre = (EditText) findViewById(R.id.createChatActGenreEdit);
        genreCount = (TextView) findViewById(R.id.createChatActGenreCount);
        createChatBtn = (Button) findViewById(R.id.createChatActCreateBtn);
        createChatCancelBtn = (Button) findViewById(R.id.createChatActCancelBtn);
        createImgBtn = (Button) findViewById(R.id.createChatActImgBtn);
        createRoomImg = (ImageView) findViewById(R.id.createChatActImg);

        createTitle.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                titleCount.setText(createTitle.getText().toString().length() + "/50");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        genreSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("직접입력")){
                    createGenre.setVisibility(View.VISIBLE);
                    genreCount.setVisibility(View.VISIBLE);

                    userSelectGenre = (String) parent.getItemAtPosition(position);
                } else {
                    createGenre.setVisibility(View.GONE);
                    genreCount.setVisibility(View.GONE);

                    userSelectGenre = (String) parent.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        createGenre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                genreCount.setText(createGenre.getText().toString().length() + "/50");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createChatCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(createTitle.getText().toString().length()==0){
                    Toast.makeText(CreateChatRoomActivity.this, "채팅방 이름을 설정해주세요.", Toast.LENGTH_SHORT).show();
                } else if(createGenre.getVisibility() == View.VISIBLE){
                    if(createGenre.getText().toString().length() == 0){
                        Toast.makeText(CreateChatRoomActivity.this, "장르를 설정해주세요.", Toast.LENGTH_SHORT).show();
                    }
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat formatNow = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    String formatDate = formatNow.format(date);

                    createChatRoom create = new createChatRoom();
                    create.execute(createTitle.getText().toString(), createGenre.getText().toString(),formatDate);

                    if(tempFile!=null){
                        uploadImage uploadImage = new uploadImage();
                        uploadImage.execute();
                    }

                } else{

                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat formatNow = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    String formatDate = formatNow.format(date);
                    createChatRoom create = new createChatRoom();
                    create.execute(createTitle.getText().toString(), userSelectGenre, formatDate);
                    if(tempFile!=null){
                        uploadImage uploadImage = new uploadImage();
                        uploadImage.execute();
                    }

                }
            }
        });

        //이미지 설정 버튼 클릭 시
        createImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                final CharSequence[] items = {"앨범에서 사진 선택","사진 촬영","사진 삭제","취소"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateChatRoomActivity.this);

                alertDialogBuilder.setTitle("채팅방 이미지 설정"); // 다이얼로그 제목 설정

                // 다이얼로그 아이템 선택 시 이벤트
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //앨범에서 사진 가져오기
                        if(items[which].equals("앨범에서 사진 선택")){


                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(intent,PICK_FROM_ALBUM);
                            Toast.makeText(CreateChatRoomActivity.this, "앨범으로 이동합니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                        // 사진 촬영해서 가져오기
                        else if(items[which].equals("사진 촬영")) {

                            //카메라,외부저장소 권한 설정

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File photoFile = null;

                            try{
                                photoFile = createImageFile();
                            } catch (IOException e){
                                Toast.makeText(CreateChatRoomActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            if(photoFile!=null){
                                mImageCaptureUri = FileProvider.getUriForFile(CreateChatRoomActivity.this,"com.example.heizepalvin.streetrecord.provider",photoFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                                startActivityForResult(intent, PICK_FROM_CAMERA);
                            }
                            dialog.dismiss();

                        }
                        // 사진 삭제하기 ( 기본 이미지로 )
                        else if(items[which].equals("사진 삭제")) {

                            createRoomImg.setImageResource(R.drawable.logos);
                            tempFile = null;
                            dialog.dismiss();

                        }
                        // 다이얼로그 닫기
                        else {

                            dialog.dismiss();
                        }
                    }
                });

                // 다이얼로그 생성

                alertDialog = alertDialogBuilder.create();

                // 다이얼로그 보여주기

                alertDialog.show();
            }

        });




    }


    private boolean checkPermissions(){

        int result;

        List<String> permissionList = new ArrayList<>();
        for(String pm : permissions){
            result = ContextCompat.checkSelfPermission(this, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                // 사용자가 해당 권한을 가지고 있지 않을 경우 리스트에 해당 권한명 추가
                permissionList.add(pm);
            }
        }
        if(!permissionList.isEmpty()){
            //권한이 추가 되었으면 해당 리스트가 empty가 아니므로 request 즉 권한을 요청한다.

            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]),MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MULTIPLE_PERMISSIONS:
                if(grantResults.length > 0){
                    for(int i = 0; i<permissions.length; i++){
                        if(permissions[i].equals(this.permissions[0])){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        } else if(permissions[i].equals(this.permissions[1])){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        } else if(permissions[i].equals(this.permissions[2])){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }

                        }
                    }
                } else {
                    Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                return;

        }
    }

    private File createImageFile() throws IOException{

        //이미지 파일 만들기

        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/StreetRecord/"); //streetRecord 경로에 이미지를 저장하기 위함
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }

        images = File.createTempFile(imageFileName,".jpg",storageDir);

        return images;
    }

    //채팅방 데이터베이스에 업데이트

    private class createChatRoom extends AsyncTask<String,Void,String>{

        ProgressDialog progressDialog;
        String image = null;
        String postData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(CreateChatRoomActivity.this,"잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if(s != null){
                String[] splitResult = s.split("/");
                String toastResult = splitResult[0];
                int token = Integer.parseInt(splitResult[1]);
                Toast.makeText(CreateChatRoomActivity.this, toastResult, Toast.LENGTH_LONG).show();
                dbHelper = new ChattingDatabase(getApplicationContext(),"chattingDB",null,1);
                db = dbHelper.getWritableDatabase();
                Date otherDate = new Date(System.currentTimeMillis());
                SimpleDateFormat format = new SimpleDateFormat("a hh:mm");
                String otherTime = format.format(otherDate);
                SimpleDateFormat datePlusTime = new SimpleDateFormat("yy/MM/dd a hh:mm:ss");
                String datePlusTimes = datePlusTime.format(otherDate);
                dbHelper.insert(db,createTitle.getText().toString(),"",image,otherTime,token,datePlusTimes);
                Intent intent = new Intent(CreateChatRoomActivity.this, ChatingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("token",token);
                intent.putExtra("title",createTitle.getText().toString());
                startActivity(intent);
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/createRoom.php";

            String title = params[0];
            String genre = params[1];
            if(tempFile != null){
                image = String.valueOf("http://115.71.232.155/uploads/"+croppedFileName.getName());
            }
            String date = params[2];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                if(image !=null ){
                   postData = "title="+title+"&genre="+genre+"&date="+date+"&image="+image+"&memberCount="+0;
                } else{
                   postData = "title="+title+"&genre="+genre+"&date="+date+"&memberCount="+0;
                }

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = conn.getResponseCode();
                InputStream inputStream;

                if(responseCode == conn.HTTP_OK){
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e){

                Log.e("createChatRoomActivity","Exception : " + e);

                return null;
            }



        }
    }

    private class uploadImage extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null){
                Toast.makeText(CreateChatRoomActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/uploadImage.php";

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

                try{

                    FileInputStream fileInputStream = new FileInputStream(tempFile);
                    URL url = new URL(serverURL);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", String.valueOf(tempFile));

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                    Log.e("이미지경로", String.valueOf(tempFile));
                    dos.writeBytes(twoHyphens+boundary+lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + String.valueOf(tempFile) + "\"" + lineEnd);
                    Log.e("dddddasdflkajsdflk","Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + String.valueOf(tempFile) + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);


                    int bytesAvailable = fileInputStream.available();
                    int maxBufferSize = 1*1024*1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead = fileInputStream.read(buffer,0,bufferSize);

                    while(bytesRead > 0){

                        dos.write(buffer,0,bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer,0,bufferSize);

                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    int serverResponseCode = 0;

                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.e("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseCode == 200){

                        return "서버에 전송 완료";
                    }

                    fileInputStream.close();
                    dos.flush();
                    dos.close();


                }catch (Exception e){

                    e.printStackTrace();
                    return "서버에 전송 실패";

                }

            return null;

        }
    }
}
