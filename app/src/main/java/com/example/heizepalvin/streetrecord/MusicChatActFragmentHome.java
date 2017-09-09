package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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


    public MusicChatActFragmentHome(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        layout = (LinearLayout) inflater.inflate(R.layout.music_chat_fragment_first,container,false);

        // 새로고침
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.musicChatSwipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vp.getAdapter().notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //새로 고침 끝


        // 채팅방 db에서 불러오기
        chatItems.clear();
        getChatRoomCount count = new getChatRoomCount();
        count.execute();

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

    private class getChattingRoom extends AsyncTask<String,Void,String>{


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
            }
        }

        @Override
        protected String doInBackground(String... params) {

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


    //채팅방 정보를 담은 JSON데이터 처리

    private void getChattingJson(){
        try{

            JSONObject jsonObject = new JSONObject(getChattingJsonData);
            final JSONArray jsonArray = jsonObject.getJSONArray("chattingData");

                for(int i = 0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);

                    String title = item.getString("title");
                    Log.e("title?",title);
                    String genre = item.getString("genre");
                    Log.e("genre?",genre);
                    String date = item.getString("date");
                    Log.e("date?",date);
                    SimpleDateFormat transFormat = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
                    Date parseDate = transFormat.parse(date);
                    int memberCount = item.getInt("memberCount");
                    Log.e("memberCount?", String.valueOf(memberCount));
                    String image = item.getString("image");
                    Log.e("image?", image);

                    if(!image.equals("null")){
                        MusicChatItem list = new MusicChatItem(title,genre,memberCount,parseDate,image);
                        chatItems.add(list);
                    } else {
                        MusicChatItem list = new MusicChatItem(title,genre,memberCount,parseDate);
                        chatItems.add(list);
                    }


            }

            if(!readJsonData){
                recyclerView = (RecyclerView) layout.findViewById(R.id.musicChatActRecycle);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new MusicChatAdapter(getApplicationContext(), chatItems, R.layout.music_chat_activity));
                readJsonData = true;

            } else {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
//            Collections.sort(chatItems, new MusicChatActFragmentHome.MiniComparator());

//            memoryPosition = recyclerView.getLayoutManager().onSaveInstanceState();
            Log.e("roomCount", String.valueOf(roomCount));

            jsonCount.add(lastJsonData);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                    Log.e("둘다",lastJsonData + " 라스트" + chatItems.size() + " 사이즈");
                    if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastVisibleItemPosition == chatItems.size()-1){
                        if(lastJsonData == chatItems.size()){
                            if(!jsonCount.isEmpty()){
                                Log.e("여기들어와 제발","플리즈");
                                Log.e("아이템사이즈",chatItems.size()+"");
                                Log.e("아이템사이즈",lastJsonData+"");
                                if(jsonCount.get(0).equals(lastJsonData)){
                                    if(lastJsonData < roomCount){
                                        Log.e("들어가나?","들어간다." + lastJsonData);
                                        getChattingRoom chattingRoom = new getChattingRoom();
                                        chattingRoom.execute();
                                        jsonCount.clear();
                                    }
                                }
                            }
                        }
                    }

                }
            });



        }catch (JSONException e){
            Log.e("MusicChatFragmentHome","JsonException : " + e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
