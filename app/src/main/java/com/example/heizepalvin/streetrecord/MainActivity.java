package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.navdrawer.SimpleSideDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.tools.debugger.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SimpleSideDrawer mSlidingMenu;
    private ArrayList<MenuItem> menuData;


    private MenuAdapter adapter;

    private ArrayList<TopMusicItem> topMusicData;
    private TopMusicAdapter topMusicAdapter;

    private MainMusicGenreAdapter mainMusicGenreAdapter;
    private NewMusicRecyclerAdapter newMusicRecyclerAdapter;

//    private ArrayList<SituationMusicItem> situationMusicData;
//    private SituationMusicAdapter situationMusicAdapter;

    //DB연동 변수
    private static String TAG = "musicChart";
    private static final String TAG_JSON = "musicChart";
    String mJsonString;

    //newMusic DB연동
    String newMusicJson;
    private ArrayList<NewMusicList> newMusics;

    //musicGenre db연동
    String musicGenreJson;
    private ArrayList<MusicGenreList> musicGenre;


    //mainWeekList db연동
//    String todayMusicJson;
    String mainWeekJson;

    String mainWeekImage;
    String mainWeekName;
    String mainWeekSong;
    String mainWeekContent;
    private TextView mainWeekMore;

    private Button loginBtn;
    private Button logoutBtn;
    private TextView loginUser;

    public static boolean loginBoolean;

    private LinearLayout playlistActStart;
    private ImageView mainControlPlaylist;
    private TextView mainControlTitle;
    private TextView mainControlArtist;
    private ImageView mainControlPre;
    private ImageView mainControlPlay;
    private ImageView mainControlNext;
    private ImageView mainControlImage;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        SharedPreferences preferences = getSharedPreferences("destroy",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("destroy",true);
        editor.commit();

        if(!GlobalApplication.getInstance().getServiceInterface().isPlaying()){
            GlobalApplication.getInstance().getServiceInterface().getRemoveNotificationPlayer();
        }
        Log.e("여기가 들어오는건지?","메인디스트로이");

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("리시브",intent.getAction());
//            if(intent.getAction().equals("NEXT")){
//                GlobalApplication.getInstance().getServiceInterface().forward();
//                updateUI();
//            } else if(intent.getAction().equals("TRUE")) {
//                updateUI();
//            } else {
//                updateUI();
//            }
            if(intent.getAction().equals("NEXT")){
                SharedPreferences randomPlay = getSharedPreferences("random",MODE_PRIVATE);
                Boolean random = randomPlay.getBoolean("random",false);
                if(random){
                    Log.e("NEXTRANDOM","ㅁㄴㅇㄹ");
                    updateUI();
                }   else {
                    GlobalApplication.getInstance().getServiceInterface().forward();
                    updateUI();
                }
            } else {
                updateUI();
            }
        }
    };

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        Stetho.initializeWithDefaults(this);

        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.left_menu);
        ImageButton menuBtn = (ImageButton)findViewById(R.id.imgBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggleLeftDrawer();
            }
        });


        menuData = new ArrayList<>();
        MenuItem myInfo = new MenuItem("내 정보",R.drawable.myinfo);
        MenuItem musicList = new MenuItem("플레이리스트",R.drawable.musiclist);
        menuData.add(myInfo);
        menuData.add(musicList);

        ListView listView = (ListView) findViewById(R.id.menuList);

        adapter = new MenuAdapter(this, R.layout.menu_item, menuData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    if(loginUser.getText().toString().equals("로그인을 해주세요.")){
                        Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent intent = new Intent(MainActivity.this,PlaylistActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } else if(position==0){
                    SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
                    Boolean loginSet = pref.getBoolean("login",false);
                    String loginLink = pref.getString("loginLink","no");
                    if(loginSet){
                        if(loginLink.equals("no")){
                            Intent intent = new Intent(MainActivity.this,MyinfoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this,MyInfoLinkActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        //SlidingMenu 끝


        //NewMusic 시작

        newMusics = new ArrayList<>();
        newMusicData newMusicData = new newMusicData();
        newMusicData.execute("http://115.71.232.155/getNewMusic.php");




        //NewMusic 끝


        //topMusicList 시작
        topMusicData = new ArrayList<>();

        GetData task = new GetData();
        task.execute("http://115.71.232.155/getMusicChart.php");


        //topMusicList 끝

        //음악장르 부분 시작


        musicGenre = new ArrayList<>();
        getGenre getGenre = new getGenre();
        getGenre.execute("http://115.71.232.155/getGenre.php");


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainMusicGenre);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);





        //mainWeekList 시작

        mainWeekMore = (TextView) findViewById(R.id.mainWeekMore);
        mainWeekData mainWeekData = new mainWeekData();
        mainWeekData.execute("http://115.71.232.155/mainWeekList.php");



        //mainWeekList 끝

        TextView newMusicMore = (TextView) findViewById(R.id.newMusicMore);
        newMusicMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewMusicActivity.class);
                startActivity(intent);
            }
        });

        //login,logout 버튼
        loginBtn = (Button) findViewById(R.id.menuLoginBtn);
        loginUser = (TextView) findViewById(R.id.menuLoginText);

        final SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
        final Boolean loginSet = pref.getBoolean("login",false);
        if(loginSet){
            loginBoolean = true;
        } else {
            loginBoolean = false;
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn = (Button) findViewById(R.id.menuLogoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                logoutBtn.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                loginUser.setText("로그인을 해주세요.");
                loginBoolean = false;
                if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                    GlobalApplication.getInstance().getServiceInterface().stop();
                    Log.e("여기들어와요 로그아웃할때","ㅁㄴㅇㄹ");
                    updateUI();
                    GlobalApplication.getInstance().getServiceInterface().getRemoveNotificationPlayer();
                }
            }
        });


        //메인 컨트롤러

        mainControlPlaylist = (ImageView) findViewById(R.id.mainControlPlaylist);
        mainControlTitle = (TextView) findViewById(R.id.mainControlTitle);
        mainControlArtist = (TextView) findViewById(R.id.mainControlArtist);
        mainControlPre = (ImageView) findViewById(R.id.mainControlPre);
        mainControlPlay = (ImageView) findViewById(R.id.mainControlPlay);
        mainControlNext = (ImageView) findViewById(R.id.mainControlNext);
        mainControlImage = (ImageView) findViewById(R.id.mainControlImage);
        playlistActStart = (LinearLayout) findViewById(R.id.mainPlayActStart);

        playlistActStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!mainControlTitle.getText().toString().equals("StreetRecord")){
                        if(loginBoolean){
                            Intent intent = new Intent(MainActivity.this,PlayActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                        }

                    }
            }
        });


        mainControlPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    Intent intent = new Intent(MainActivity.this,PlaylistActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mainControlPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!mainControlTitle.getText().toString().equals("StreetRecord")){
                if(loginBoolean){
                    GlobalApplication.getInstance().getServiceInterface().rewind();
                } else {
                    Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            }
        });

        mainControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!mainControlTitle.getText().toString().equals("StreetRecord")){
                        if(loginBoolean){
                            GlobalApplication.getInstance().getServiceInterface().togglePlay();
                            if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                                mainControlPlay.setImageResource(R.drawable.pausewhite);
                            } else {
                                mainControlPlay.setImageResource(R.drawable.playwhite);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                        }

                    }
            }
        });

        mainControlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mainControlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        GlobalApplication.getInstance().getServiceInterface().forwardClick();

                    } else {
                        Toast.makeText(MainActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mainControlTitle.setSelected(true);

        registerBroadcast();
        updateUI();

        SharedPreferences preferences = getSharedPreferences("destroy",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();


    }



    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        filter.addAction(BroadcastActions.NEXT);
        filter.addAction(BroadcastActions.PLAY_START);
        filter.addAction(BroadcastActions.isPlaying);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

//    private void updateButton(){
//        mainControlPlay.setImageResource(R.drawable.pause);
//    }

    private void updateUI(){

        if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
            mainControlPlay.setImageResource(R.drawable.pausewhite);
        } else {
            mainControlPlay.setImageResource(R.drawable.playwhite);
        }

        PlaylistItem musicItem = GlobalApplication.getInstance().getServiceInterface().getMusicItem();
        if(musicItem != null){
            mainControlTitle.setText(musicItem.getTitle());
            mainControlArtist.setText(musicItem.getArtist());
            Glide.with(this).load(musicItem.getImgPath()).into(mainControlImage);
        }

    }


    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if(result != null) {
                mJsonString = result;
                showResult();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("rmstn","두번째 asyncTask");

            String serverURL = params[0];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG,"response code = " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e){
                Log.d(TAG,"InsertData: Error", e);
                errorString = e.toString();
                return null;
            }
        }
    }

    private void showResult(){

        try{

            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i<5; i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                Log.e(TAG,"title ? "+title);
                String artist = item.getString("artist");
                Log.e(TAG,"artist ? "+artist);
                String rank = item.getString("rank");
                Log.e(TAG,"rank ? "+rank);
                String albumImg = item.getString("albumImg");
                Log.e(TAG,"albumImg ? "+albumImg);
                String musicURL = item.getString("musicURL");
                Log.e(TAG,"musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
//                Log.e(TAG,"lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e(TAG,"albumName ? " + albumName);
                String date = item.getString("date");
                Log.e(TAG,"date ? " + date);
                String genre = item.getString("genre");
                Log.e(TAG,"genre ? " + genre);

                TopMusicItem top = new TopMusicItem(rank,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                topMusicData.add(top);
            }

            ListView topMusic = (ListView) findViewById(R.id.topMusicList);


            View topMusicHeader = getLayoutInflater().inflate(R.layout.top_music_header,null,false);
            View topMusicFooter = getLayoutInflater().inflate(R.layout.top_music_footer,null, false);

            topMusic.addHeaderView(topMusicHeader);
            topMusic.addFooterView(topMusicFooter);

            topMusicAdapter = new TopMusicAdapter(this,R.layout.top_music_item,topMusicData);
            topMusic.setAdapter(topMusicAdapter);

            TextView topMusicMore = (TextView) findViewById(R.id.topMusicMore);
            topMusicMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,TopMusicActivity.class);
                    startActivity(intent);
                }
            });


        } catch (JSONException e){
            Log.d(TAG, "showResult : ", e);
        }

    }

    private class newMusicData extends AsyncTask<String,Void, String>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if(result !=null){
                newMusicJson = result;
                showNewMusic();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("rmstn","첫번째 asyncTask");


            String serverURL = params[0];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.connect();

                int responseStatusCode = conn.getResponseCode();
                Log.d("palvin","response code = " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == conn.HTTP_OK){
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine())!= null){
                    sb.append(line + "\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch(Exception e){

                return null;

            }
        }
    }

    private void showNewMusic(){

        try{

            JSONObject jsonObject = new JSONObject(newMusicJson);
            JSONArray jsonArray = jsonObject.getJSONArray("newMusic");

            for(int i =0; i<10;i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                Log.e("syllowa","title ? "+title);
                String artist = item.getString("artist");
                Log.e("syllowa","artist ? "+artist);
                String albumImg = item.getString("albumImg");
                Log.e("syllowa","albumImg ? "+albumImg);
                String musicURL = item.getString("musicURL");
                String lyrics = item.getString("lyrics");
                String albumName = item.getString("albumName");
                String date = item.getString("date");
                String genre = item.getString("genre");


                NewMusicList newMusicList = new NewMusicList(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                newMusics.add(newMusicList);

            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainNewMusicList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);

            newMusicRecyclerAdapter = new NewMusicRecyclerAdapter(getApplicationContext(),newMusics,R.layout.activity_main);
            recyclerView.setAdapter(newMusicRecyclerAdapter);

//

        }catch (JSONException e){
            Log.d(TAG,"showNewMusic : ", e);
        }


    }

    private class mainWeekData extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result != null){
                mainWeekJson = result;
                showWeekList();
            }


        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("rmstn","네번째 asyncTask");


            String serverURL = params[0];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");
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

                while ((line = bufferedReader.readLine()) != null){
                    sb.append(line + '\n');
                }

                bufferedReader.close();

                return sb.toString().trim();



            } catch (Exception e){

                Log.d("mainWeekList","InsertData : Error ",e);
                return null;

            }

        }
    }

    private void showWeekList(){

        try{
            JSONObject jsonObject = new JSONObject(mainWeekJson);
            JSONArray jsonArray = jsonObject.getJSONArray("mainWeekArtist");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                mainWeekName = item.getString("name");
                Log.e("mainWeek","name ? " + mainWeekName);
                mainWeekSong = item.getString("song");
                Log.e("mainWeek","song ? " + mainWeekSong);
                mainWeekImage = item.getString("image");
                Log.e("mainWeek","image ? " + mainWeekImage);
                mainWeekContent = item.getString("content");
                Log.e("mainWeek","content ? "+ mainWeekContent);
            }

            ImageView img = (ImageView) findViewById(R.id.mainWeekImg);
            TextView artist = (TextView) findViewById(R.id.mainWeekArtist);
            TextView content = (TextView) findViewById(R.id.mainWeekContent);
            TextView song = (TextView) findViewById(R.id.mainWeekSong);

            Glide.with(this).load(mainWeekImage).into(img);
            artist.setText(mainWeekName);
            song.setText(mainWeekSong);
            content.setText(mainWeekContent);


            mainWeekMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,WeekArtistActivity.class);
                    intent.putExtra("image",mainWeekImage);
                    intent.putExtra("name",mainWeekName);
//            intent.putExtra("song",mainWeekSong);
                    intent.putExtra("content",mainWeekContent);
                    startActivity(intent);
                }
            });

        } catch(JSONException e){

            Log.e("mainWeek","JSONException ? " + e);

        }

    }

    private class getGenre extends AsyncTask<String , Void,String>{

        ProgressDialog progressDialog;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this, "잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                musicGenreJson = result;
                showGenre();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            Log.e("rmstn","세번째 asyncTask");


            String serverURL = params[0];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");
                conn.connect();

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
                    sb.append(line + "\n");
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e){

                Log.e("MainGenre","Exceipton : "+ e);
                return null;

            }


        }
    }

    private void showGenre(){
        try {

            JSONObject jsonObject = new JSONObject(musicGenreJson);
            JSONArray jsonArray = jsonObject.getJSONArray("mainGenreList");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String image = item.getString("image");
                String genre = item.getString("genre");

                MusicGenreList genreLists = new MusicGenreList(image,genre);

                musicGenre.add(genreLists);

            }

            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mainMusicGenre);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);

            mainMusicGenreAdapter = new MainMusicGenreAdapter(this,musicGenre,R.layout.musicgenre_main_item);
            recyclerView.setAdapter(mainMusicGenreAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
        Boolean login = preferences.getBoolean("login",false);
        if(login){
            loginBtn.setVisibility(View.INVISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
            String user = preferences.getString("userID","user");
            if(user.equals("user")){
               String name = preferences.getString("userName","app");
               loginUser.setText(name+"님");
            } else {
               loginUser.setText(user+"님");
            }
        }
    }

}
