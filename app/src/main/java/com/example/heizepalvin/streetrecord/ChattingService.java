package com.example.heizepalvin.streetrecord;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.heizepalvin.streetrecord.ChatingActivity.chatAdapter;
import static com.example.heizepalvin.streetrecord.ChatingActivity.chatingHandler;
import static com.example.heizepalvin.streetrecord.ChatingActivity.chatlist;
import static com.example.heizepalvin.streetrecord.ChatingActivity.receiveMsg;
import static com.example.heizepalvin.streetrecord.ChatingActivity.receiveRoomTitle;
import static com.example.heizepalvin.streetrecord.ChatingActivity.receiveToken;
import static com.example.heizepalvin.streetrecord.ChatingActivity.receiveUserID;
import static com.example.heizepalvin.streetrecord.ChatingActivity.socket;
import static com.example.heizepalvin.streetrecord.MusicChatActFragmentRoomList.fragmentRoomListItems;
import static com.example.heizepalvin.streetrecord.MusicChatActivity.chatItems;
import static com.example.heizepalvin.streetrecord.MusicChatActivity.vp;

public class ChattingService extends Service {

    NotificationManager notificationManager;
    ChatingActivity.receiveThread thread;
    Notification notification;

    BufferedReader br;
    BufferedWriter bw;

    ChattingDatabase dbHelper;
    SQLiteDatabase db;

    String userID;
    String jsonList;

    RemoteViews contentView;

   public static ArrayList<String> roomlists;

    String otherUser;


    int chatToken;
    public ChattingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        socketConnection conn = new socketConnection();
        conn.execute();

        roomlists = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        userID = preferences.getString("userID","false");
        if(userID.equals("false")){
            userID = preferences.getString("userName","false");
        }

        getUserRoomList getList = new getUserRoomList();
        getList.execute();

        return START_STICKY;
    }

    public class serviceHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            dbHelper = new ChattingDatabase(getApplicationContext(),"chattingDB",null,1);
            db = dbHelper.getWritableDatabase();

            SharedPreferences sharedPreferences = getSharedPreferences("roomNumber",MODE_PRIVATE);
            chatToken = sharedPreferences.getInt("token",123456789);


            Date date = new Date(System.currentTimeMillis());

            SimpleDateFormat datePlusTime = new SimpleDateFormat("yy/MM/dd a hh:mm:ss");
            String datePlusTimes = datePlusTime.format(date);

            SimpleDateFormat curtimeFormat = new SimpleDateFormat("a hh:mm");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yy/MM/dd");
            String time = curtimeFormat.format(date);
            String dates = dateFormat2.format(date);

            switch (msg.what){

                case 1:
                    Date otherDate = new Date(System.currentTimeMillis());
                    SimpleDateFormat format = new SimpleDateFormat("a hh:mm");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd");
                    String otherTime = format.format(otherDate);
                    String otherDates = dateFormat.format(otherDate);
                    if(chatToken == receiveToken){
                        //다른 유저가 보낸 메시지 처리
                        otherUser = receiveUserID;
                        ChatingItem otherMsg = new ChatingItem(receiveMsg,"1",otherTime,otherUser,otherDates);
                        chatlist.add(otherMsg);
                        dbHelper.insert(db,otherUser,receiveMsg,otherTime,chatToken,"1",otherDates);
                        dbHelper.update(db,"chatRoom",receiveMsg,chatToken,otherTime,datePlusTimes);
                        vp.getAdapter().notifyDataSetChanged();
                        chatAdapter.notifyDataSetChanged();
                    } else {
                        //다른방에서 보낸 메시지 처리
                        otherUser = receiveUserID;
                        dbHelper.insert(db,otherUser,receiveMsg,otherTime,receiveToken,"1",otherDates);
                        dbHelper.update(db,"chatRoom",receiveMsg,receiveToken,otherTime,datePlusTimes);
                        if(vp != null && chatAdapter !=null){
                            Log.e("vp??????",vp+"");
                            if(vp.getAdapter()!=null){
                                Log.e("vp????/",vp.getAdapter()+"");
                                vp.getAdapter().notifyDataSetChanged();
                            }
                            chatAdapter.notifyDataSetChanged();
                        }

                    }

                    notificationGet();

                    break;
                case 2:

                    //자기가 보낸 메시지

                    ChatingItem myMsg = new ChatingItem(receiveMsg,"2",time,userID,dates);
                    chatlist.add(myMsg);
                    dbHelper.insert(db,userID,receiveMsg,time,chatToken,"2",dates);
                    dbHelper.update(db,"chatRoom",receiveMsg,chatToken,time,datePlusTimes);
                    chatAdapter.notifyDataSetChanged();

                    notificationGet();

                    break;

                case 3:

                    ChatingItem enterMsg = new ChatingItem(receiveUserID+receiveMsg,"3",time,userID,dates);
                    if(chatlist != null){
                        chatlist.add(enterMsg);
                    }
                    dbHelper.insert(db,userID,receiveUserID+receiveMsg,time,receiveToken,"3",dates);
                    if(chatAdapter != null){
                        chatAdapter.notifyDataSetChanged();
                    }
                    break;

                case 4:

                    ChatingItem exitMsg = new ChatingItem(receiveUserID+receiveMsg,"3",time,userID,dates);
                    if(chatlist != null){
                        chatlist.add(exitMsg);
                    }
                    if(!userID.equals(receiveUserID)){
                        dbHelper.insert(db,userID,receiveUserID+receiveMsg,time,receiveToken,"3",dates);
                    }
                    if(chatAdapter!=null){
                        chatAdapter.notifyDataSetChanged();
                    }
                    roomlists.clear();
                    getUserRoomList getList = new getUserRoomList();
                    getList.execute();
                    break;

                case 5:

                    ChatingItem myImageMsg = new ChatingItem(receiveMsg,"2",time,userID,dates);
                    chatlist.add(myImageMsg);
                    dbHelper.insert(db,userID,receiveMsg,time,receiveToken,"2",dates);
                    dbHelper.update(db,"chatRoom","사진",receiveToken,time,datePlusTimes);
                    chatAdapter.notifyDataSetChanged();

                    break;

                case 6:

                    if(chatToken == receiveToken){

                        otherUser = receiveUserID;
                        ChatingItem otherImageMsg = new ChatingItem(receiveMsg,"1",time,otherUser,dates);
                        chatlist.add(otherImageMsg);
                        dbHelper.insert(db,otherUser,receiveMsg,time,chatToken,"1",dates);
                        dbHelper.update(db,"chatRoom","사진",chatToken,time,datePlusTimes);
                        vp.getAdapter().notifyDataSetChanged();
                        chatAdapter.notifyDataSetChanged();
                    } else {

                        otherUser = receiveUserID;
                        dbHelper.insert(db,otherUser,receiveMsg,time,receiveToken,"1",dates);
                        dbHelper.update(db,"chatRoom","사진",receiveToken,time,datePlusTimes);
                        if(vp!=null&& chatAdapter != null){
                            if(vp.getAdapter()!=null){
                                vp.getAdapter().notifyDataSetChanged();
                            }
                            chatAdapter.notifyDataSetChanged();
                        }
                    }

                    notificationGet();
            }


        }
    }

    private void notificationGet(){



        if(receiveToken != chatToken){

            dbHelper.countUpdate(db,receiveToken,1);
            if(vp != null && chatAdapter !=null){
                if(vp.getAdapter()!=null){
                    vp.getAdapter().notifyDataSetChanged();
                }
                chatAdapter.notifyDataSetChanged();
            }


            Intent intent = new Intent(ChattingService.this, MusicChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(ChattingService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            if(receiveMsg.contains("http://115.71.232.155")){
                receiveMsg = "사진";
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){


                notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(receiveRoomTitle)
                        .setContentText(receiveUserID+" : "+receiveMsg)
                        .setSmallIcon(R.drawable.logoface)
                        .setTicker("메시지가 도착했습니다.")
                        .setContentIntent(pendingIntent)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .build();
            } else {
                notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle(receiveRoomTitle)
                        .setContentText(receiveUserID+" : "+receiveMsg)
                        .setSmallIcon(R.drawable.logoface)
                        .setTicker("메시지가 도착했습니다.")
                        .setContentIntent(pendingIntent)
                        .build();
            }


            notification.defaults = Notification.DEFAULT_SOUND;
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(receiveToken,notification);

            if(fragmentRoomListItems != null){
                fragmentRoomListItems.clear();
                dbHelper.receiveSelect(db,"chatRoom",fragmentRoomListItems);
            }
        }
    }

    private class socketConnection extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            serviceHandler handler = new serviceHandler();
            thread = new ChatingActivity.receiveThread(handler);
            thread.setSocket(socket);

        }

        @Override
        protected String doInBackground(String... params) {

            try{

                socket = new Socket("115.71.232.155",9999);

                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));



                String serverMsg = br.readLine();


                return serverMsg;

            }catch (IOException e){

                e.printStackTrace();
                return null;
            }
        }
    }

    private class getUserRoomList extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!= null){
                jsonList = s;
                getJsonList();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/getUserRoomList.php";

            try{
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String id = "id="+userID;

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(id.getBytes("UTF-8"));
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
                Log.e("ChattingService","Exception : " + e);
                return null;
            }
        }
    }

    private void getJsonList(){

        try{

            JSONObject jsonObject = new JSONObject(jsonList);
            JSONArray jsonArray = jsonObject.getJSONArray("chatToken");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String token = String.valueOf(item.getInt("token"));
                roomlists.add(token);
            }
            thread.setUserRoomList(roomlists);
            if(!thread.isAlive()){
                thread.start();
            }
        }catch (JSONException e){
            Log.e("ChattingService","JSONException : " + e);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
