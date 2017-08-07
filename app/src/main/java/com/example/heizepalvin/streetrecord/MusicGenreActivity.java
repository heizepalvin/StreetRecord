package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.navdrawer.SimpleSideDrawer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.heizepalvin.streetrecord.MainActivity.loginBoolean;


/**
 * Created by soyounguensoo on 2017-06-25.
 */

public class MusicGenreActivity extends AppCompatActivity {


    private SimpleSideDrawer mSlidingMenu;

    private String getBalladJson;
    private String getDanceJson;
    private String getHiphopJson;
    private String getOstJson;
    private String getPopJson;
    private String getRnbJson;


    private ArrayList<MusicGenreActivityItem> items;

    private ArrayList<MenuItem> menuData;

    private Button loginBtn;
    private Button logoutBtn;
    private TextView loginUser;

    private TextView allPlay;
    private PlaylistDatabase helper;
    private SQLiteDatabase db;

    //컨트롤러

    private LinearLayout controlPlayer;
    private ImageView controlPlaylist;
    private ImageView controlImage;
    private TextView controlTitle;
    private TextView controlArtist;
    private ImageView controlPre;
    private ImageView controlPlay;
    private ImageView controlNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genre_music_activity);

        //사이드메뉴
        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.left_menu);
        ImageButton menuBtn = (ImageButton) findViewById(R.id.genreMusicSideMenu);
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

        MenuAdapter adapter = new MenuAdapter(this, R.layout.menu_item,menuData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    if(loginUser.getText().toString().equals("로그인을 해주세요.")){
                        Toast.makeText(MusicGenreActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent intent = new Intent(MusicGenreActivity.this,PlaylistActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        //메인에서 선택한 장르 데이터 가져오기

        Intent intent = getIntent();
        TextView title = (TextView) findViewById(R.id.genreMusicActTitle);
        String genre = intent.getStringExtra("genre");
        title.setText(genre);

        //홈버튼 구현

        final ImageView homeBtn = (ImageView) findViewById(R.id.genreMusicHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicGenreActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        //해당 장르 음악리스트 가져오기

        items = new ArrayList<>();


        if(genre.equals("발라드")){

            getBallad ballad = new getBallad();
            ballad.execute("http://115.71.232.155/maingenre/getBalladList.php");

        } else if(genre.equals("댄스")){

            getDance dance = new getDance();
            dance.execute("http://115.71.232.155/maingenre/getDanceList.php");


        } else if(genre.equals("힙합")){

            getHiphop hiphop = new getHiphop();
            hiphop.execute("http://115.71.232.155/maingenre/getHiphopList.php");

        } else if(genre.equals("OST")){

            getOst ost = new getOst();
            ost.execute("http://115.71.232.155/maingenre/getOstList.php");

        } else if(genre.equals("POP")){

            getPop pop = new getPop();
            pop.execute("http://115.71.232.155/maingenre/getPopList.php");

        } else if(genre.equals("R&B/Soul")){

            getRnb rnb = new getRnb();
            rnb.execute("http://115.71.232.155/maingenre/getRnbList.php");

        }

        //login,logout 버튼
        loginBtn = (Button) findViewById(R.id.menuLoginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicGenreActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logoutBtn = (Button) findViewById(R.id.menuLogoutBtn);
        loginUser = (TextView) findViewById(R.id.menuLoginText);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Toast.makeText(MusicGenreActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                logoutBtn.setVisibility(View.INVISIBLE);
                loginBtn.setVisibility(View.VISIBLE);
                loginUser.setText("로그인을 해주세요.");
                loginBoolean = false;
                if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                    GlobalApplication.getInstance().getServiceInterface().stop();
                    updateUI();
                    GlobalApplication.getInstance().getServiceInterface().getRemoveNotificationPlayer();
                }
            }
        });

        //전체재생 버튼

        allPlay = (TextView) findViewById(R.id.genreMusicActAllPlay);
        helper = new PlaylistDatabase(getApplicationContext(),"playlist",null,1);
        db = helper.getWritableDatabase();
        allPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    int count = 0;
                    try{
                        for(int i = items.size() - 1; i>=0; i--){
                            String title = items.get(i).getTitle();
                            String artist = items.get(i).getArtist();
                            String albumImg = items.get(i).getAlbumImg();
                            String musicURL = items.get(i).getMusicURL();
                            String lyrics = items.get(i).getLyrics();
                            String albumName = items.get(i).getAlbumName();
                            String date = items.get(i).getDate();
                            String genre = items.get(i).getGenre();
                            count = helper.search(db,title);
                            if(count != 1){
                                helper.insert(db,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                            } else{
                                helper.delete(db,title);
                                helper.insert(db,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                            }
                        }
                        if(count > 0){
                            Toast.makeText(MusicGenreActivity.this, "중복되는 곡을 삭제하고 재생합니다.", Toast.LENGTH_SHORT).show();
                        }

                        ArrayList<PlaylistItem> playlist = new ArrayList<PlaylistItem>();
                        helper.select(db,"playlist",playlist);
                        GlobalApplication.getInstance().getServiceInterface().setPlaylist(playlist);
                        GlobalApplication.getInstance().getServiceInterface().play(0);
                    }catch (SQLiteAbortException e){
                        e.printStackTrace();
                        Log.e("MusicGenreActivityDatabaseException","데이터베이스를 가져올 수 없음.");
                    }
                } else {
                    Toast.makeText(MusicGenreActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //컨트롤러

        controlPlayer = (LinearLayout) findViewById(R.id.genreMusicActControlPlayer);
        controlPlaylist = (ImageView) findViewById(R.id.genreMusicActControlPlaylist);
        controlImage = (ImageView) findViewById(R.id.genreMusicActControlImage);
        controlTitle = (TextView) findViewById(R.id.genreMusicActControlTitle);
        controlArtist = (TextView) findViewById(R.id.genreMusicActControlArtist);
        controlPre = (ImageView) findViewById(R.id.genreMusicActControlPre);
        controlPlay = (ImageView) findViewById(R.id.genreMusicActControlPlay);
        controlNext = (ImageView) findViewById(R.id.genreMusicActControlNext);

        controlPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        Intent intent = new Intent(MusicGenreActivity.this,PlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MusicGenreActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        controlPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    Intent intent = new Intent(MusicGenreActivity.this,PlaylistActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(MusicGenreActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        controlPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        GlobalApplication.getInstance().getServiceInterface().rewind();
                    } else {
                        Toast.makeText(MusicGenreActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        controlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        GlobalApplication.getInstance().getServiceInterface().togglePlay();
                        if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
                            controlPlay.setImageResource(R.drawable.pausewhite);
                        } else {
                            controlPlay.setImageResource(R.drawable.playwhite);
                        }
                    } else {
                        Toast.makeText(MusicGenreActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        controlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        GlobalApplication.getInstance().getServiceInterface().forwardClick();
                    } else {
                        Toast.makeText(MusicGenreActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        controlTitle.setSelected(true);

        registerBroadcast();
        updateUI();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        Log.e("여기가 들어오는건지?","뮤직장르디스트로이");

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("NEXT")){
                updateUI();
            } else if(intent.getAction().equals("TRUE")){
                updateUI();
            } else {
                updateUI();
            }
        }
    };

    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        filter.addAction(BroadcastActions.NEXT);
        filter.addAction(BroadcastActions.isPlaying);
        filter.addAction(BroadcastActions.PLAY_START);
        registerReceiver(receiver,filter);
    }

    public void unregisterBroadcast(){
        unregisterReceiver(receiver);
    }

    private void updateUI(){

        if(GlobalApplication.getInstance().getServiceInterface().isPlaying()){
            controlPlay.setImageResource(R.drawable.pausewhite);
        } else {
            controlPlay.setImageResource(R.drawable.playwhite);
        }

        PlaylistItem musicItem = GlobalApplication.getInstance().getServiceInterface().getMusicItem();
        if(musicItem != null){
            controlTitle.setText(musicItem.getTitle());
            controlArtist.setText(musicItem.getArtist());
            Glide.with(this).load(musicItem.getImgPath()).into(controlImage);
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
                String name = preferences.getString("userName","facebook");
                loginUser.setText(name+"님");
            } else {
                loginUser.setText(user+"님");
            }
        }
    }

    private class getBallad extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MusicGenreActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                getBalladJson = result;
                getBalladJsonData();
            }


        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
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

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line + "\n");
                }
                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e){
                Log.e("balladList","EXception : "+ e);
                return null;
            }


        }
    }

    private void getBalladJsonData(){

        try {

            JSONObject jsonObject = new JSONObject(getBalladJson);
            JSONArray jsonArray = jsonObject.getJSONArray("balladMusic");

            for(int i=0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                String artist = item.getString("artist");
                String albumImg = item.getString("albumImg");
                String musicURL = item.getString("musicURL");
                Log.e("MusicGenreActivity","musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
//                Log.e("MusicGenreActivity","lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e("MusicGenreActivity","albumName ? " + albumName);
                String date = item.getString("date");
                Log.e("MusicGenreActivity","date ? " + date);
                String genre = item.getString("genre");
                Log.e("MusicGenreActivity","genre ? " + genre);


                MusicGenreActivityItem getBallad = new MusicGenreActivityItem(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                items.add(getBallad);
            }
            ListView listView = (ListView) findViewById(R.id.genreMusicActivityList);
            MusicGenreActivityAdapter adapter = new MusicGenreActivityAdapter(this,R.layout.genre_music_activity_item,items);
            listView.setAdapter(adapter);

        } catch (JSONException e) {
           Log.e("balladList","JSONException : "+ e);
        }


    }


    private class getDance extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MusicGenreActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                getDanceJson = result;
                getDanceJsonData();
            }
        }

        @Override
        protected String doInBackground(String... params) {

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

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e){

                Log.e("danceMusic","Exception : "+ e);
                return null;

            }
        }
    }

    private void getDanceJsonData(){

        try{
            JSONObject jsonObject = new JSONObject(getDanceJson);
            JSONArray jsonArray = jsonObject.getJSONArray("danceMusic");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                String artist = item.getString("artist");
                String albumImg = item.getString("albumImg");
                String musicURL = item.getString("musicURL");
                Log.e("MusicGenreActivity","musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
                Log.e("MusicGenreActivity","lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e("MusicGenreActivity","albumName ? " + albumName);
                String date = item.getString("date");
                Log.e("MusicGenreActivity","date ? " + date);
                String genre = item.getString("genre");
                Log.e("MusicGenreActivity","genre ? " + genre);



                MusicGenreActivityItem getDance = new MusicGenreActivityItem(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                items.add(getDance);
            }
            ListView listView = (ListView) findViewById(R.id.genreMusicActivityList);
            MusicGenreActivityAdapter adapter = new MusicGenreActivityAdapter(this,R.layout.genre_music_activity_item,items);
            listView.setAdapter(adapter);

        }catch(JSONException e){
            Log.e("danceList","JSONException : " + e);
        }


    }

    private class getHiphop extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MusicGenreActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                getHiphopJson = result;
                getHiphopJsonData();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
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
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("hiphopMusic","Exception : " + e);
                return null;
            }

        }
    }
    private void getHiphopJsonData(){

        try {

            JSONObject jsonObject = new JSONObject(getHiphopJson);
            JSONArray jsonArray = jsonObject.getJSONArray("hiphopMusic");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String title = item.getString("title");
                String artist = item.getString("artist");
                String albumImg = item.getString("albumImg");
                String musicURL = item.getString("musicURL");
                Log.e("MusicGenreActivity","musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
                Log.e("MusicGenreActivity","lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e("MusicGenreActivity","albumName ? " + albumName);
                String date = item.getString("date");
                Log.e("MusicGenreActivity","date ? " + date);
                String genre = item.getString("genre");
                Log.e("MusicGenreActivity","genre ? " + genre);


                MusicGenreActivityItem getHiphop = new MusicGenreActivityItem(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                items.add(getHiphop);
            }

            ListView listView = (ListView) findViewById(R.id.genreMusicActivityList);
            MusicGenreActivityAdapter adapter = new MusicGenreActivityAdapter(this,R.layout.genre_music_activity_item,items);
            listView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.e("hiphopMusic"," JSONException : " + e);

        }


    }

    private class getOst extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MusicGenreActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){

                getOstJson = result;
                getOstJsonData();
            }
        }

        @Override
        protected String doInBackground(String... params) {

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

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line + "\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e){

                Log.e("ostMusic","Exception : "+ e);
                return null;
            }


        }
    }

    private void getOstJsonData(){

        try {
            JSONObject jsonObject = new JSONObject(getOstJson);
            JSONArray jsonArray = jsonObject.getJSONArray("ostMusic");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                String artist = item.getString("artist");
                String albumImg = item.getString("albumImg");
                String musicURL = item.getString("musicURL");
                Log.e("MusicGenreActivity","musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
                Log.e("MusicGenreActivity","lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e("MusicGenreActivity","albumName ? " + albumName);
                String date = item.getString("date");
                Log.e("MusicGenreActivity","date ? " + date);
                String genre = item.getString("genre");
                Log.e("MusicGenreActivity","genre ? " + genre);


                MusicGenreActivityItem ost = new MusicGenreActivityItem(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                items.add(ost);
            }
            ListView listView = (ListView) findViewById(R.id.genreMusicActivityList);
            MusicGenreActivityAdapter adapter = new MusicGenreActivityAdapter(this,R.layout.genre_music_activity_item,items);
            listView.setAdapter(adapter);
        } catch (JSONException e) {

            Log.e("ostMusic","JSONException : " + e);

        }


    }

    private class getPop extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MusicGenreActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                getPopJson = result;
                getPopJsonData();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try{
                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
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

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("popMusic","Exception : "+e);
                return null;
            }

        }
    }

    private void getPopJsonData(){

        try {
            JSONObject jsonObject = new JSONObject(getPopJson);
            JSONArray jsonArray = jsonObject.getJSONArray("popMusic");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                String artist = item.getString("artist");
                String albumImg = item.getString("albumImg");
                String musicURL = item.getString("musicURL");
                Log.e("MusicGenreActivity","musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
                Log.e("MusicGenreActivity","lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e("MusicGenreActivity","albumName ? " + albumName);
                String date = item.getString("date");
                Log.e("MusicGenreActivity","date ? " + date);
                String genre = item.getString("genre");
                Log.e("MusicGenreActivity","genre ? " + genre);

                MusicGenreActivityItem pop = new MusicGenreActivityItem(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                items.add(pop);

            }

            ListView listView = (ListView) findViewById(R.id.genreMusicActivityList);
            MusicGenreActivityAdapter adapter = new MusicGenreActivityAdapter(this,R.layout.genre_music_activity_item,items);
            listView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.e("popMusic","JSONException : " + e);

        }


    }

    private class getRnb extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MusicGenreActivity.this,"잠시만 기다려주세요.",null,true,true);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                getRnbJson = result;
                getRnbJsonData();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try{

                URL url = new URL(serverURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
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

                while((line = bufferedReader.readLine())!= null){
                    sb.append(line+"\n");
                }

                bufferedReader.close();

                return sb.toString().trim();

            }catch (Exception e){

                Log.e("rnbMusic","Exception : "+ e);
                return null;
            }

        }
    }

    private void getRnbJsonData(){

        try {
            JSONObject jsonObject = new JSONObject(getRnbJson);
            JSONArray jsonArray = jsonObject.getJSONArray("rnbMusic");

            for(int i=0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                String artist = item.getString("artist");
                String albumImg = item.getString("albumImg");
                String musicURL = item.getString("musicURL");
                Log.e("MusicGenreActivity","musicURL ? " + musicURL);
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
                Log.e("MusicGenreActivity","lyrics ? " + lyrics);
                String albumName = item.getString("albumName");
                Log.e("MusicGenreActivity","albumName ? " + albumName);
                String date = item.getString("date");
                Log.e("MusicGenreActivity","date ? " + date);
                String genre = item.getString("genre");
                Log.e("MusicGenreActivity","genre ? " + genre);


                MusicGenreActivityItem rnb = new MusicGenreActivityItem(title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                items.add(rnb);

            }

            ListView listView = (ListView) findViewById(R.id.genreMusicActivityList);
            MusicGenreActivityAdapter adapter = new MusicGenreActivityAdapter(this,R.layout.genre_music_activity_item,items);
            listView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.e("rnbMusic","JSONException : " + e);

        }


    }
}
