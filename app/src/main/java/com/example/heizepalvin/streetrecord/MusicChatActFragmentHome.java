package com.example.heizepalvin.streetrecord;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static com.example.heizepalvin.streetrecord.MusicChatActivity.chatItems;
import static com.example.heizepalvin.streetrecord.MusicChatActivity.vp;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by soyounguensoo on 2017-08-24.
 */

public class MusicChatActFragmentHome extends Fragment {

    private RecyclerView recyclerView;

    private String getChattingJsonData;

    private LinearLayout layout;

    //JSON 데이터를 어디까지 가져왔는지 체크

    private int lastJsonData = 0;

    private ArrayList<Integer> jsonCount = new ArrayList<>();

    // 채팅방 개수 배열

    private int roomCount;

    // 한번 데이터를 읽어 왔다는 표시

    private boolean readJsonData = false;

    //채팅방 필터 스피너 및 검색

    private Spinner chatRoomFilter;
    private EditText filterSearch;
    private String selectFilter;

    private ChattingDatabase dbHelper;
    private SQLiteDatabase db;

    //로컬에있는 데이터베이스 걸러내는 토큰

    private int localToken;

    //채팅방 고유 num

    private int num;

    //선택한 필터

    private String choiceFilter;

    private MusicChatAdapter adapter;

    private boolean chattingsjsonBoolean = false;

    //인원수가 0명인 채팅방리스트

    private ArrayList<Integer> deleteList;


    public MusicChatActFragmentHome(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();
        chatItems.clear();
        jsonCount.clear();
        lastJsonData = 0;
        if(choiceFilter.equals("모든 채팅방")){
            Log.e("필터","모든채팅방");
            getChatRoomCount getNotFilterRoom = new getChatRoomCount();
            getNotFilterRoom.execute();
            chattingsjsonBoolean = true;
        } else if(choiceFilter.equals("기타")){
            Log.e("필터","기타");

            getFilterChatRoom filterRoom = new getFilterChatRoom();
            filterRoom.execute(choiceFilter);

            chattingsjsonBoolean = true;

        } else{
            Log.e("필터","3번째");

            getFilterChatRoom filterRoom = new getFilterChatRoom();
            filterRoom.execute(choiceFilter);

            chattingsjsonBoolean = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        layout = (LinearLayout) inflater.inflate(R.layout.music_chat_fragment_first,container,false);

        // 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.musicChatSwipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e("clear된나?",chatItems.size()+"");
                chatItems.clear();
                jsonCount.clear();
                lastJsonData = 0;
                if(choiceFilter.equals("모든 채팅방")){
                    Log.e("필터","모든채팅방");
                    getChatRoomCount getNotFilterRoom = new getChatRoomCount();
                    getNotFilterRoom.execute();
                    chattingsjsonBoolean = true;
                } else if(choiceFilter.equals("기타")){
                    Log.e("필터","기타");

                    getFilterChatRoom filterRoom = new getFilterChatRoom();
                    filterRoom.execute(choiceFilter);

                    chattingsjsonBoolean = true;

                } else{
                    Log.e("필터","3번째");

                    getFilterChatRoom filterRoom = new getFilterChatRoom();
                    filterRoom.execute(choiceFilter);

                    chattingsjsonBoolean = true;
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //새로 고침 끝


        //채팅방 필터 스피너 및 검색

        chatRoomFilter = (Spinner) layout.findViewById(R.id.chatFragmentFilter);
        filterSearch = (EditText) layout.findViewById(R.id.chatFragmentSearch);
        filterSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectFilter = filterSearch.getText().toString().toLowerCase(Locale.getDefault());
                lastJsonData = 0;
                getFilterChatRoom filterRoom = new getFilterChatRoom();
                Log.e("selectFilter는?",selectFilter);
                filterRoom.execute(selectFilter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chatRoomFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getItemAtPosition(position).equals("통합검색")){
                    filterSearch.setVisibility(View.VISIBLE);

                } else if(parent.getItemAtPosition(position).equals("모든 채팅방")){
                    filterSearch.setVisibility(View.GONE);

                    choiceFilter = "모든 채팅방";
                    //분류하지않았을때 모든 채팅방 불러오기
                    Log.e("채팅방분류","분류");
                    getChattingJsonData = null;
                    chatItems.clear();
                    jsonCount.clear();
                    lastJsonData = 0;
                    getChatRoomCount getNotFilterRoom = new getChatRoomCount();
                    getNotFilterRoom.execute();

                    chattingsjsonBoolean = true;

                } else if(parent.getItemAtPosition(position).equals("기타")){
                    filterSearch.setVisibility(View.GONE);
                    selectFilter = (String) parent.getItemAtPosition(position);
                    choiceFilter = selectFilter;
                    lastJsonData = 0;
                    getFilterChatRoom filterRoom = new getFilterChatRoom();
                    filterRoom.execute(selectFilter);

                    chattingsjsonBoolean = true;

                }
                else {
                    filterSearch.setVisibility(View.GONE);
                    selectFilter = (String) parent.getItemAtPosition(position);
                    choiceFilter = selectFilter;
                    Log.e("selectFilter는?",selectFilter);
                    //사용자가 선택한걸로 db에서 검색해서 가져오기
                    lastJsonData = 0;
                    getFilterChatRoom filterRoom = new getFilterChatRoom();
                    filterRoom.execute(selectFilter);

                    chattingsjsonBoolean = true;

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        // 채팅방 db에서 불러오기
//        chatItems.clear();
//        getChatRoomCount count = new getChatRoomCount();
//        count.execute();

        //불러오기 끝


        Log.e("채팅방몇개?",chatItems.size()+"");

        return layout;
    }






    private class MiniComparator implements Comparator<MusicChatItem> {

        @Override
        public int compare(MusicChatItem o1, MusicChatItem o2) {
            return o2.getTime().compareTo(o1.getTime());
        }
    }

    //필터에 적용된 채팅방 목록 불러오는 AsyncTask
    private class getFilterChatRoom extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.e(" 결과좀보여주세요",s);
            getChattingJsonData = s;
            chatItems.clear();
            jsonCount.clear();
            getChattingJson();
            Log.e("혹시 여기?","ㅁㄴㅇㄹ");
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/getFilterChattingRoom.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();

                String filter = "filter="+params[0] +"&num="+lastJsonData;

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(filter.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                lastJsonData = lastJsonData+10;

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

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){
                Log.e("MusicChatFragmentHome","Exception : " + e );
                return null;
            }


        }


    }

    //채팅방 개수 불러오는 AsyncTask

    private class getChatRoomCount extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null){
                roomCount = Integer.parseInt(s);
                Log.e("룸카",roomCount+"");
//                Toast.makeText(getApplicationContext(), s , Toast.LENGTH_SHORT).show();
                getChattingRoom chattingRoom = new getChattingRoom();
                chattingRoom.execute();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/getChattingRoomCount.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();

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

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("MusicChatActFragmentHome","Exception : " + e);

                return null;
            }
        }
    }


    //채팅방 불러오는 AsyncTask


    private class getChattingRoom extends AsyncTask<Integer,Void,String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null){

                getChattingJsonData = s;
                getChattingJson();
                Log.e("여기도 거치니?","ㅁㄴㅇㄹ");
            }
        }

        @Override
        protected String doInBackground(Integer... params) {

            String serverURL = "http://115.71.232.155/chat/getChattingRoom.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setDoInput(true);
                conn.connect();

                String lastNum = "num="+lastJsonData;

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(lastNum.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();
                lastJsonData = lastJsonData+10;




                Log.e("라스트제이슨데이타 뭐냐 ?" ,lastJsonData+"");


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

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("MusicChatActFragmentHome","Exception : " + e);

                return null;
            }


        }
    }

    //인원수가 0명인 채팅방 삭제

    private class deleteChattingRoom extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s!=null){
                Log.e("deleteChattingRoom",s);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://115.71.232.155/chat/deleteChattingRoom.php";

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();
                Log.e("token ?",Integer.valueOf(params[0])+"");
                String token = "token="+Integer.valueOf(params[0]);

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

                while((line=bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();


            }catch (Exception e){

                Log.e("MusicChatActFragmentHome","Exception : " + e);
                return null;
            }
        }
    }


    //채팅방 정보를 담은 JSON데이터 처리

    private void getChattingJson() {

        if (chattingsjsonBoolean) {
//            Log.e("여긴 안거쳐?",getChattingJsonData);
            try {
                JSONObject jsonObject = new JSONObject(getChattingJsonData);
                final JSONArray jsonArray = jsonObject.getJSONArray("chattingData");

                dbHelper = new ChattingDatabase(getApplicationContext(), "chattingDB", null, 1);
                db = dbHelper.getWritableDatabase();
                db.beginTransaction();
                String sql = "select * from chatRoom";
                Cursor cursor = db.rawQuery(sql, null);
                deleteList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    Log.e("여긴 들어와?","ㅁㄴㅇㄹ");
                    //로컬데이터베이스에 있는 채팅방 목록을 불러와서 그 목록을 제외하고 화면에 뿌려준다.
                    int memberCount = item.getInt("memberCount");
                    if(memberCount==0){
                        deleteList.add(item.getInt("num"));
                        Log.e("델리트리스트",deleteList.size()+"");
                        deleteChattingRoom deleteRoom = new deleteChattingRoom();
                        deleteRoom.execute(String.valueOf(item.getInt("num")));
                        continue;
                    }
                    Log.e("localToken",cursor.getCount()+" ? ? " + num + " ? ?");
                    if(cursor.getCount() != 0){
                        for(int j = 0; j<cursor.getCount(); j++) {
                            cursor.moveToPosition(j);
                            localToken = cursor.getInt(5);
                            Log.e("token?",localToken+"");
                            num = item.getInt("num");
                            Log.e("num?",num+"");
                            if(localToken == num){
                                break;
                            }
                        }

                        if (localToken == num) {
                            continue;
                        }
                    } else {
                        num = item.getInt("num");
                    }
                    String title = item.getString("title");
                    Log.e("title?", title);
                    String genre = item.getString("genre");
//                    Log.e("genre?",genre);
                    String date = item.getString("date");
//                    Log.e("date?",date);
                    SimpleDateFormat transFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    Date parseDate = transFormat.parse(date);

//                    Log.e("memberCount?", String.valueOf(memberCount));
                    String image = item.getString("image");
//                    Log.e("image?", image);

                    if (!image.equals("null")) {
                        MusicChatItem list = new MusicChatItem(title, genre, memberCount, parseDate, image, num);
                        chatItems.add(list);
                    } else {
                        MusicChatItem list = new MusicChatItem(title, genre, memberCount, parseDate, num);
                        chatItems.add(list);
                    }


                }



                db.endTransaction();


                if (!readJsonData) {
                    recyclerView = (RecyclerView) layout.findViewById(R.id.musicChatActRecycle);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new MusicChatAdapter(getApplicationContext(), chatItems,R.layout.music_chat_activity);
                    recyclerView.setAdapter(adapter);
                    readJsonData = true;

                } else {
                    Log.e("갱신되고있어요", "ㅁㄴㅇㄹ");
                    recyclerView.getAdapter().notifyDataSetChanged();
                }


                Log.e("roomCount", String.valueOf(roomCount));

                jsonCount.add(lastJsonData);
                Log.e("제이슨카운트", jsonCount.size() + "");
                Log.e("쳇아이템사이즈", chatItems.size() + "");

                //9주차때 수정
//                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//                    @Override
//                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//
//                        Log.e("둘다", lastJsonData + " 라스트" + chatItems.size() + " 사이즈");
//                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastVisibleItemPosition == chatItems.size() - 1) {
//                            if (lastJsonData >= chatItems.size()) {
//                                if (!jsonCount.isEmpty()) {
//                                    Log.e("여기들어와 제발", "플리즈");
//                                    Log.e("아이템사이즈", chatItems.size() + "");
//                                    Log.e("아이템사이즈", lastJsonData + "");
//                                    if (jsonCount.get(0).equals(lastJsonData)) {
//                                        if (lastJsonData < roomCount) {
//                                            Log.e("들어가나?", "들어간다." + lastJsonData);
//                                            getChattingRoom chattingRoom = new getChattingRoom();
//                                            chattingRoom.execute();
//                                            jsonCount.clear();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                });

            } catch (JSONException e) {
                Log.e("MusicChatFragmentHome", "JsonException : " + e);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onPause() {
        super.onPause();
//        adapter.notifyDataSetChanged();
    }


}
