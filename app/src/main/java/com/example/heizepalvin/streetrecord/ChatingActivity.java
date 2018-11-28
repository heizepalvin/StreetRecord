package com.example.heizepalvin.streetrecord;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.navdrawer.SimpleSideDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.heizepalvin.streetrecord.ChattingService.roomlists;
import static com.example.heizepalvin.streetrecord.MusicChatActFragmentRoomList.fragmentRoomListAdapter;
import static com.example.heizepalvin.streetrecord.MusicChatActivity.chatItems;
import static com.example.heizepalvin.streetrecord.MusicChatActivity.vp;

/**
 * Created by soyounguensoo on 2017-08-24.
 */

public class ChatingActivity extends AppCompatActivity {

    public static Socket socket;

    //레이아웃 요소들 정의
    private EditText chatingInput;
    private Button chatingSendBtn;
    TextView chatView;
    private TextView chatActTitle;
    private ImageView chatActImageBtn;

    BufferedReader br;
    BufferedWriter bw;

    PrintWriter sendWriter;

    private String sendString;


    public static   ArrayList<ChatingItem> chatlist;

    public static Handler chatingHandler;

    public static ChatingAdapter chatAdapter;

    static String receiveMsg;
    public static String receiveUserID;
    public static String roomTitle;
    public static String receiveRoomTitle;
    public static int receiveToken;
    public static String receiveServer;


    public static boolean s_threadBoolean = false;

    private static String userID;
    //채팅을 보낸 다른 유저
    private static String otherUser;

    private int chatToken;

    private String saveData;

    //유저가 참여하고 있는 방번호
    public static ArrayList<String> userRoomTokenList = new ArrayList<>();


    //사이드 메뉴

    private SimpleSideDrawer mSlidingMenu;
    private ListView chatActSideMenuList;
    private ArrayList<NavigationDrawerItem> navigationItems;
    private ImageView chatActMenuBtn;
    private Button chatActSideMenuExit;
    private ChatActNavigationDrawerAdapter navigationAdapter;

    //채팅유저목록JSON

    private String getChattingUserJSON;

    //참여중인 채팅방을 입장했을때 로컬데이터베이스에서 주고받은 메시지 가져오기
    private SQLiteDatabase db;
    private ChattingDatabase dbHelper;

    //백버튼

    private ImageView chatActBackBtn;

    //이미지 전송 관련

    private AlertDialog alertDialog;
    private Uri imageCaptureUri;
    private String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;
    private File userImages;

    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int CROP_FROM_IMAGE = 2;

    private File croppedFileName = null;
    private File tempFile = null;


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case PICK_FROM_ALBUM: {

                imageCaptureUri = data.getData();
                Log.d("SmartWheel", imageCaptureUri.getPath().toString());
            }

            case PICK_FROM_CAMERA: {


                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(imageCaptureUri,"image/*");

                this.grantUriPermission("com.android.camera",imageCaptureUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,0);
                Log.e("list.get 0 " , list.get(0).activityInfo.packageName);
                grantUriPermission(list.get(0).activityInfo.packageName, imageCaptureUri , Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

                    imageCaptureUri = FileProvider.getUriForFile(ChatingActivity.this,"com.example.heizepalvin.streetrecord.provider", tempFile);

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    intent.putExtra("return-data",false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                    intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());


                    Intent i = new Intent(intent);
                    ResolveInfo res = list.get(0);
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(res.activityInfo.packageName, imageCaptureUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION| Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    startActivityForResult(i,CROP_FROM_IMAGE);

                }
                break;
            }

            case CROP_FROM_IMAGE:{

                try{

                    uploadImage upload = new uploadImage();
                    upload.execute();

                    getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));


                }catch (Exception e){
                }
            }
        }
    }

    private class uploadImage extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null){
                messageSend imageSend = new messageSend();
                sendString = "http://115.71.232.155/uploads/"+croppedFileName.getName();
                imageSend.execute();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/uploadImage.php";

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


//            if(!tempFile.equals(null)){
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

                dos.writeBytes(twoHyphens+boundary+lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
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

//            }
            return null;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences userRoomNumber = getSharedPreferences("roomNumber",MODE_PRIVATE);
        SharedPreferences.Editor editor = userRoomNumber.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        Stetho.initializeWithDefaults(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        chatlist = new ArrayList<>();


        //뒤로가기 버튼

        chatActBackBtn = (ImageView) findViewById(R.id.chatActBackBtn);
        chatActBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        userID = preferences.getString("userID","false");
        if(userID.equals("false")){
            userID = preferences.getString("userName","false");
        }


        chatActTitle = (TextView) findViewById(R.id.chatActTitle);
        chatingInput = (EditText) findViewById(R.id.chatActInput);
        chatingSendBtn = (Button) findViewById(R.id.chatActSendBtn);
        chatActImageBtn = (ImageView) findViewById(R.id.chatActImageBtn);
//        chatView = (TextView) findViewById(R.id.chatActView);

        //이미지 전송할 수 있는 버튼 이벤트

        chatActImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                final CharSequence[] items = {"앨범에서 사진 가져오기","사진 촬영하기","취소"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatingActivity.this);
                alertDialogBuilder.setTitle("사진 전송");
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(items[which].equals("앨범에서 사진 가져오기")){

                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                            startActivityForResult(intent,PICK_FROM_ALBUM);
                            Toast.makeText(ChatingActivity.this, "앨범으로 이동합니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else if(items[which].equals("사진 촬영하기")){

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File photoFile = null;

                            try{
                                photoFile = createImageFile();
                            } catch (IOException e){
                                Toast.makeText(ChatingActivity.this, "이미지 처리 오류", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            if(photoFile != null){
                                imageCaptureUri = FileProvider.getUriForFile(ChatingActivity.this,"com.example.heizepalvin.streetrecord.provider",photoFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                                startActivityForResult(intent,PICK_FROM_CAMERA);
                            }
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });

                alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });


        //채팅 참여 인원 데이터베이스에 저장


        saveChatUser saveUser = new saveChatUser();
        saveUser.execute();

        //채팅방 제목

        Intent getData = getIntent();
        roomTitle = getData.getStringExtra("title");
        chatToken = getData.getIntExtra("token",123456789);
        chatActTitle.setText(roomTitle);

        //유저가 참여하고 있는 방 토큰 리스트

        //notification 알림 지우기

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(chatToken);

        //이 방번호가 저장이 안되어있을 경우에만 저장
        if(!userRoomTokenList.contains(String.valueOf(chatToken))){
            userRoomTokenList.add(String.valueOf(chatToken));
        }



        //현재 방의 위치 저장

        final SharedPreferences userRoomNumber = getSharedPreferences("roomNumber",MODE_PRIVATE);
        SharedPreferences.Editor editor = userRoomNumber.edit();
        editor.putInt("token",chatToken);
        editor.commit();

        //현재 방안에 위치해있다면 방의 번호가 저장되어있을 것이고, 대기실이라면 없을 것이다.


        //대화 목록 불러오기(로컬데이터베이스에 저장되있을때) 처음 참여자 말고
        dbHelper = new ChattingDatabase(this,"chattingDB",null,1);
        db = dbHelper.getReadableDatabase();
        dbHelper.selectMsg(db,chatlist,chatToken);


        // 17/09/24 채팅방 카운트 표시 초기화

        dbHelper.countUpdate(db,chatToken,2);

        //초기화 끝


        //사이드 메뉴 구현

        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setRightBehindContentView(R.layout.chat_activity_side_menu);

        chatActSideMenuList = (ListView) findViewById(R.id.chatActSideMenuList);
        chatActMenuBtn = (ImageView) findViewById(R.id.chatActSideMenuBtn);
        chatActSideMenuExit = (Button) findViewById(R.id.chatActSideMenuExit);




        navigationItems = new ArrayList<>();
        navigationAdapter = new ChatActNavigationDrawerAdapter(this,R.layout.chat_activity_side_item,navigationItems);
        chatActSideMenuList.setAdapter(navigationAdapter);

        chatActMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggleRightDrawer();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(chatingInput.getWindowToken(),0);
                navigationAdapter.notifyDataSetChanged();

            }
        });

        chatActSideMenuExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅방 나가기 눌렀을때

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ChatingActivity.this);

                alertBuilder.setTitle("참여중인 채팅방 나가기");
                alertBuilder.setMessage("채팅방에서 나가시겠습니까?").setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setNegativeButton("나가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //참여중인 채팅방 데이터베이스에서 삭제하기

                        navigationItems.remove(userID);
                        navigationAdapter.notifyDataSetChanged();
                        deleteChatUser deleteData = new deleteChatUser();
                        deleteData.execute();

                        //참여중인 채팅방목록에서 삭제하기 (로컬데이터베이스 삭제) 채팅기록도 삭제

                        dbHelper.delete(db,chatToken);

                        userRoomTokenList.remove(String.valueOf(chatToken));

                        Intent intent = new Intent(ChatingActivity.this,ChattingService.class);
                        startService(intent);

                        messageEnterExit exitMsg = new messageEnterExit();
                        exitMsg.execute(" 님이 퇴장하셨습니다.","exit");

                        //나가기 메시지 보내기

//                        exitMessageSend userExit = new exitMessageSend();
//                        userExit.execute();

                        finish();
                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });






        //socket

        chatingSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sendString = chatingInput.getText().toString();
                    if(!sendString.equals("")){
                            messageSend messageSend = new messageSend();
                            messageSend.execute();
                            chatingInput.setText("");
                    }
//                }
            }
        });

        socketGet getso = new socketGet();
        getso.execute();


        final ListView listView = (ListView) findViewById(R.id.chatActList);

        chatAdapter = new ChatingAdapter(this,R.layout.chat_item,chatlist);

        listView.setAdapter(chatAdapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setStackFromBottom(true);



    }

    //사용자 권한 체크

    private boolean checkPermissions(){

        int result;

        List<String> permissionList = new ArrayList<>();
        for(String pm : permissions){
            result = ContextCompat.checkSelfPermission(this, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }
        if(!permissionList.isEmpty()){

            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]),MULTIPLE_PERMISSIONS);
            return  false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MULTIPLE_PERMISSIONS:
                if(grantResults.length > 0 ){
                    for (int i = 0; i<permissions.length; i++){
                        if(permissions[i].equals(this.permissions[0])){
                            if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "권한 요청에 동의를 해야 이용 가능합니다. 설정에서 권한 허용을 해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        } else if(permissions[i].equals(this.permissions[1])){
                            if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
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

    private File createImageFile() throws  IOException{

        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "IP"+timeStamp+"_";
        File storageDir = new File(Environment.getExternalStorageDirectory()+"/StreetRecord/");
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }

        userImages = File.createTempFile(imageFileName, ".jpg",storageDir);

        return userImages;
    }

    //이 채팅방에 참여중인 유저 리스트 가져오기

    private class getChattingRoomUser extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null){
                getChattingUserJSON = s;
                getChattingUserJSONData();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/getChattingUser.php";

            try{
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                conn.setConnectTimeout(5000);
                conn.connect();

                String token = "token="+chatToken;

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(token.getBytes("UTF-8"));
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

                while((line=bufferedReader.readLine())!= null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();


            }catch (Exception e){

                return null;
            }
        }
    }

    //채팅방 유저 JSON 데이터 가져오기

    private  void getChattingUserJSONData (){

        try{
            JSONObject jsonObject = new JSONObject(getChattingUserJSON);
            final JSONArray jsonArray = jsonObject.getJSONArray("chattingUser");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString("id");

                NavigationDrawerItem user = new NavigationDrawerItem(id);
                navigationItems.add(user);
            }
            navigationAdapter.notifyDataSetChanged();

        }catch (JSONException e){
        }
    }


    //참여중인 채팅방유저 데이터베이스에서 삭제하기

    private class deleteChatUser extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!= null){
                Toast.makeText(ChatingActivity.this, s , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/deleteChatUser.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String deleteData = "id="+userID + "&token="+chatToken;

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(deleteData.getBytes("UTF-8"));
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
            }catch (Exception e){

                return null;
            }

        }
    }

    private class messageSend extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... params) {

            sendWriter.println(chatToken+">"+roomTitle+">"+userID+">"+sendString);
            sendWriter.flush();

            return null;
        }
    }

    private  class messageEnterExit extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            sendString = params[0];
            sendWriter.println(chatToken+">"+roomTitle+">"+userID+">"+sendString+">"+params[1]);
            sendWriter.flush();

            return null;
        }
    }

    private class socketGet extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if(!roomlists.contains(String.valueOf(chatToken))){
                messageEnterExit enterMsg = new messageEnterExit();
                enterMsg.execute(" 님이 입장하셨습니다.","enter");
            }



        }

        @Override
        protected String doInBackground(String... params) {

            try {

                socket = new Socket("115.71.232.155",9999);

                sendWriter = new PrintWriter(socket.getOutputStream());


                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return null;

            }


        }
    }

    public static class receiveThread extends Thread{



        private Socket r_socket;
        private TextView tv;

        Handler handler;

        private ArrayList<String> userRoomList;

        public receiveThread(){

        }

        public receiveThread (Handler handler){
            this.handler = handler;
        }


        @Override

        public void run() {
            super.run();

            try{

                if(r_socket != null){

                BufferedReader br = new BufferedReader(new InputStreamReader(r_socket.getInputStream()));
                while(true){

                    receiveMsg = br.readLine();
                    Log.e("receiveㅇㅇㅇ",receiveMsg);
                    String[] receiveUser = receiveMsg.split(">");
                    receiveToken = Integer.parseInt(receiveUser[0]);
                    receiveRoomTitle = receiveUser[1];
                    receiveUserID = receiveUser[2];
                    receiveMsg = receiveUser[3];
                    if(receiveUser.length == 5){
                        receiveServer = receiveUser[4];
                    }
                    //받아온 메시지가 자기 자신 것인지 다른 유저 것인지 판단하는 로직
                    if(receiveUserID.equals(userID)){

                        if(receiveUser.length==5){
                            //입장,퇴장 멘트
                            if(receiveServer.equals("enter")){
                                Message msg = handler.obtainMessage(3,receiveMsg);
                                handler.sendMessage(msg);
                            } else {
                                Message msg = handler.obtainMessage(4,receiveMsg);
                                handler.sendMessage(msg);
                            }

                        } else {
                            if(receiveMsg.contains("http://115.71.232.155")){

                                Message msg = handler.obtainMessage(5,receiveMsg);
                                handler.sendMessage(msg);

                            } else {
                                Message msg = handler.obtainMessage(2,receiveMsg);
                                handler.sendMessage(msg);
                            }
                        }

                    } else {


                        otherUser = receiveUserID;
                        if(userRoomList.contains(String.valueOf(receiveToken))){
                            if(receiveUser.length==5){
                                if(receiveServer.equals("enter")){
                                    Message msg = handler.obtainMessage(3,receiveMsg);
                                    handler.sendMessage(msg);
                                } else {
                                    Message msg = handler.obtainMessage(4,receiveMsg);
                                    handler.sendMessage(msg);
                                }
                            } else {

                                if(receiveMsg.contains("http://115.71.232.155")){
                                    Message msg = handler.obtainMessage(6,receiveMsg);
                                    handler.sendMessage(msg);
                                } else {
                                    Message msg = handler.obtainMessage(1,receiveMsg);
                                    handler.sendMessage(msg);
                                }

                            }
                        }
                    }

                }



                }



            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void setSocket(Socket _socket){
            r_socket = _socket;
        }

        public void setUserRoomList(ArrayList<String> userRoomList){
            this.userRoomList = userRoomList;
        }
    }

    //채팅방 들어왔을 때 참여유저 테이블에 저장

    private class saveChatUser extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null){
                if(!s.equals("palvin")){
                    Toast.makeText(ChatingActivity.this, s , Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(ChatingActivity.this, ChattingService.class);
                startService(intent);

                //채팅방에 참여하고 있는 인원 리스트뷰에 저장
                getChattingRoomUser getRoomUser = new getChattingRoomUser();
                getRoomUser.execute();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/saveChatUser.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                if(chatToken != 123456789){
                    saveData = "id="+userID+"&token="+chatToken;
                }

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(saveData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = conn.getResponseCode();
                InputStream inputStream;

                if(responseCode == conn.HTTP_OK){
                    inputStream = conn.getInputStream();
                } else{
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("ChattingActivity","Exception : " + e );
                return null;
            }
        }
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.heizepalvin.streetrecord.ChattingService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
