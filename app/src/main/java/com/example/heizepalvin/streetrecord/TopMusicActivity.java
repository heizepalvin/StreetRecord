package com.example.heizepalvin.streetrecord;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.heizepalvin.streetrecord.MainActivity.loginBoolean;


/**
 * Created by soyounguensoo on 2017-06-24.
 */

public class TopMusicActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private String topMusicJson;
    private ArrayList<TopMusicItem> musicChart;

    private SimpleSideDrawer mSlidingMenu;

    private ArrayList<MenuItem> menuData;

    private Button loginBtn;
    private Button logoutBtn;
    private TextView loginUser;

    private TextView allPlay;
    private PlaylistDatabase helper;
    private SQLiteDatabase db;


    //컨트롤

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
        setContentView(R.layout.top_music_activity);

        //사이드메뉴

        mSlidingMenu = new SimpleSideDrawer(this);
        mSlidingMenu.setLeftBehindContentView(R.layout.left_menu);
        ImageButton menuBtn = (ImageButton) findViewById(R.id.topMusicSideMenu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingMenu.toggleLeftDrawer();
            }
        });

        menuData = new ArrayList<>();
        MenuItem myInfo = new MenuItem("내 정보",R.drawable.myinfo);
        final MenuItem musicList = new MenuItem("플레이리스트",R.drawable.musiclist);
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
                        Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    } else{
                        Intent intent = new Intent(TopMusicActivity.this,PlaylistActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }  else if(position==0){
                    SharedPreferences pref = getSharedPreferences("login",MODE_PRIVATE);
                    Boolean loginSet = pref.getBoolean("login",false);
                    String loginLink = pref.getString("loginLink","no");
                    if(loginSet){
                        if(loginLink.equals("no")){
                            Intent intent = new Intent(TopMusicActivity.this,MyinfoActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(TopMusicActivity.this,MyInfoLinkActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //사이드 메뉴 끝

        //LOGO 버튼 이벤트

        ImageView home = (ImageView) findViewById(R.id.topMusicHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopMusicActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        musicChart = new ArrayList<>();
        getTopMusic getData = new getTopMusic();
        getData.execute("http://115.71.232.155/getMusicChart.php");


        //login,logout 버튼
        loginBtn = (Button) findViewById(R.id.menuLoginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopMusicActivity.this,LoginActivity.class);
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
                Toast.makeText(TopMusicActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
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

        allPlay = (TextView) findViewById(R.id.topMusicActAllPlay);
        helper = new PlaylistDatabase(getApplicationContext(),"playlist",null,1);
        db = helper.getWritableDatabase();
        allPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    int count = 0;
                    try{
                        for(int i = musicChart.size() - 1; i>=0; i--){
                            String title = musicChart.get(i).getTitle();
                            String artist = musicChart.get(i).getArtist();
                            String albumImg = musicChart.get(i).getAlbumImg();
                            String musicURL = musicChart.get(i).getMusicURL();
                            String lyrics = musicChart.get(i).getLyrics();
                            String albumName = musicChart.get(i).getAlbumName();
                            String date = musicChart.get(i).getDate();
                            String genre = musicChart.get(i).getGenre();
                            count = helper.search(db,title);
                            if(count != 1){
                                helper.insert(db,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);

                            } else {
                                helper.delete(db,title);
                                helper.insert(db,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);

                            }
                        }
                        if(count > 0){
                            Toast.makeText(TopMusicActivity.this, "중복되는 곡을 삭제하고 재생합니다.", Toast.LENGTH_SHORT).show();
                        }
                        ArrayList<PlaylistItem> playlist = new ArrayList<PlaylistItem>();
                        helper.select(db,"playlist",playlist);
                        GlobalApplication.getInstance().getServiceInterface().setPlaylist(playlist);
                        GlobalApplication.getInstance().getServiceInterface().play(0);

                    } catch (SQLiteException e){
                        e.printStackTrace();
                        Log.e("NewMusicActDatabaseException","데이터베이스를 가져올 수 없음.");
                    }
                } else {
                    Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //컨트롤러

        controlPlayer = (LinearLayout) findViewById(R.id.topMusicActControlPlayer);
        controlPlaylist = (ImageView) findViewById(R.id.topMusicActControlPlaylist);
        controlImage = (ImageView) findViewById(R.id.topMusicActControlImage);
        controlTitle = (TextView) findViewById(R.id.topMusicActControlTitle);
        controlArtist = (TextView) findViewById(R.id.topMusicActControlArtist);
        controlPre = (ImageView) findViewById(R.id.topMusicActControlPre);
        controlPlay = (ImageView) findViewById(R.id.topMusicActControlPlay);
        controlNext = (ImageView) findViewById(R.id.topMusicActControlNext);

        controlPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!controlTitle.getText().toString().equals("StreetRecord")){
                    if(loginBoolean){
                        Intent intent = new Intent(TopMusicActivity.this,PlayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        controlPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginBoolean){
                    Intent intent = new Intent(TopMusicActivity.this,PlaylistActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TopMusicActivity.this, "로그인을 해주세요.", Toast.LENGTH_SHORT).show();
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

    private class getTopMusic extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(TopMusicActivity.this,"잠시만 기다려주세요.",null,true,true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if(result != null){
                topMusicJson = result;
                getTopMusicJson();
            }

        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {

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

                while ((line = bufferedReader.readLine())!=null){
                    sb.append(line+'\n');
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void getTopMusicJson(){

        try {

            JSONObject jsonObject = new JSONObject(topMusicJson);
            JSONArray jsonArray = jsonObject.getJSONArray("musicChart");

            for(int i = 0; i<jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String title = item.getString("title");
                String artist = item.getString("artist");
                String rank = item.getString("rank");
                String albumImg = item.getString("albumImg");
                String musicURL = item.getString("musicURL");
                String getLyrics = item.getString("lyrics");
                String lyrics = getLyrics.replace("*","\n");
                String albumName = item.getString("albumName");
                String date = item.getString("date");
                String genre = item.getString("genre");

                TopMusicItem items = new TopMusicItem(rank,title,artist,albumImg,musicURL,lyrics,albumName,date,genre);
                musicChart.add(items);
            }

            ListView listView = (ListView) findViewById(R.id.topMusicActivityList);

            TopMusicActivityAdapter topMusicActivityAdapter = new TopMusicActivityAdapter(this,R.layout.top_music_activity_item,musicChart);
            listView.setAdapter(topMusicActivityAdapter);

        } catch (JSONException e) {
            Log.e("topMusicActivity","JsonException  : "+e);
        }


    }
}
